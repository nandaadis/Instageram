package com.example.instageram.main.data

import android.graphics.Color
import android.util.Log
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.pushnotification.RetrofitInstance
import com.example.instageram.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PostSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userCollectionRef = Firebase.firestore.collection("user")
    private val postCollectionRef = FirebaseFirestore.getInstance().collection("post")
    private val StorageRef = Firebase.storage.reference

    fun getPostDetail(postID: String): Observable<PostModel> {
        return Observable.create { emitter ->
            postCollectionRef.document(postID).addSnapshotListener { querySnapshot, e ->
                e?.let {
                    emitter.onError(it)
                }
                querySnapshot?.let {
                    val data = it.toObject<PostModel>()
                    emitter.onNext(data!!)
                }
            }
        }
    }

    fun getCurrentUserProfile(): Observable<ProfileModel> {
        return Observable.create { emitter ->
            userCollectionRef.document(getCurrentUserUID()).addSnapshotListener { querySnapshot, e ->
                e?.let {
                    emitter.onError(it)
                }
                querySnapshot?.let {
                    val data = it.toObject<ProfileModel>()
                    emitter.onNext(data!!)
                }
            }
        }
    }

    fun getUserProfile(profilUID: String): Observable<ProfileModel> {
        return Observable.create { emitter ->
            userCollectionRef.document(profilUID).addSnapshotListener { querySnapshot, e ->
                e?.let {
                    emitter.onError(it)
                }
                querySnapshot?.let {
                    val data = it.toObject<ProfileModel>()
                    emitter.onNext(data!!)
                }
            }
        }
    }


    fun sendLove(postID: String) = Completable.create { emitter ->
        Firebase.firestore.runBatch { batch ->
            val doc = postCollectionRef.document(postID)
            val collection = postCollectionRef.document(postID).collection("love")
            val data = mapOf(
                "userid" to getCurrentUserUID(),
                "timestamp" to Calendar.getInstance().timeInMillis.toString()
            )
            batch.update(doc, "loveby", FieldValue.arrayUnion(getCurrentUserUID()))
            collection.add(data)
        }
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { emitter.onError(it) }
    }


    fun sendUnlove(postID: String) = Completable.create { emitter ->
        val doc = postCollectionRef.document(postID)
        val collection = postCollectionRef.document(postID).collection("love")
        Firebase.firestore.runTransaction { transaction ->
            val data = mapOf(
                "userid" to getCurrentUserUID()
            )
            transaction.update(doc, "loveby", FieldValue.arrayRemove(getCurrentUserUID()))
        }
            .addOnSuccessListener {
                collection.whereEqualTo("userid", getCurrentUserUID()).get().addOnSuccessListener {

                    if (it.documents.isNotEmpty()) {

                        for (document in it.documents) {

                            collection.document(document.id).delete()
                        }
                    }
                }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
            }
            .addOnFailureListener {
                emitter.onError(it)
            }
    }

    fun sendNotif(message: PushNotifModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotif(message)
            if (response.isSuccessful) {
            } else {

            }
        } catch (e: Exception) {

        }
    }

    fun getCurrentUserUID(): String {
        return firebaseAuth.uid!!
    }

    fun getPhoto(imagePath: String): Observable<ByteArray> {
        return Observable.create() { emitter ->
            val MAX_DOWNLOAD = (10 * 1024 * 1024).toLong()
            StorageRef.child(imagePath).getBytes(MAX_DOWNLOAD).addOnCompleteListener { bytes ->
                bytes.result?.let { emitter.onNext(it) }
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    fun checkQuery(): Observable<List<String>> {
        return Observable.create() { emitter ->
            FirebaseFirestore.getInstance()
                .collection("post")
                .whereArrayContains("viewby", getCurrentUserUID())
                .orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { document ->
                    val list : MutableList<String> = mutableListOf()
                    for(doc in document){
                        list.add(doc.id)
                    }
                    emitter.onNext(list)
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun checkUserId() = Completable.create { emitter ->
        userCollectionRef.document(getCurrentUserUID()).get().addOnCompleteListener { task ->
            val data = task.result?.toObject(ProfileModel::class.java)
            data?.userid?.let {
                if (it.isNotEmpty()) {
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable("noUserID"))
                }
            }
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }


}