package com.example.instageram.main.data

import android.util.Log
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.pushnotification.RetrofitInstance
import com.example.instageram.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

class OtherProfileSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userCollectionRef = Firebase.firestore.collection("user")
    private val postCollectionRef = FirebaseFirestore.getInstance().collection("post")
    private val StorageRef = Firebase.storage.reference

    fun getProfileData(UserID: String): Observable<ProfileModel> {
        return Observable.create { emitter ->
            userCollectionRef.document(UserID).addSnapshotListener { querySnapshot, e ->
                e?.let {
                    emitter.onError(it)
                }
                querySnapshot?.let {
                    val data = it.toObject<ProfileModel>()
                    data?.let {
                        emitter.onNext(it)

                    }
                    emitter.onComplete()
                }
            }
        }
    }

    fun getPhotoProfile(imagePath: String): Observable<ByteArray> {
        return Observable.create() { emitter ->
            val MAX_DOWNLOAD = (10 * 1024 * 1024).toLong()
            StorageRef.child(imagePath).getBytes(MAX_DOWNLOAD).addOnCompleteListener { bytes ->
                bytes.result?.let { emitter.onNext(it) }
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    fun onFollow(userID: String) = Completable.create { emitter ->
        Firebase.firestore.runTransaction { batch ->
            val UserDoc = userCollectionRef.document(userID)
            val UserCollection = userCollectionRef.document(userID).collection("follower")
            val CurrentUserDoc = userCollectionRef.document(getCurrentUserUID())
            val CurrentUserCollection = userCollectionRef.document(getCurrentUserUID()).collection("following")



            val dataFollower = mapOf(
                "userid" to getCurrentUserUID(),
                "timestamp" to Calendar.getInstance().timeInMillis.toString()
            )
            val dataFollowing = mapOf(
                "userid" to userID,
                "timestamp" to Calendar.getInstance().timeInMillis.toString()
            )

            val result = batch.get(UserDoc).toObject(ProfileModel::class.java)
            result?.post?.let{
                for (doc in it){
                    val postCollectionDoc = postCollectionRef.document(doc)
                    batch.update(postCollectionDoc, "viewby", FieldValue.arrayUnion(getCurrentUserUID()))
                }
            }


            batch.update(UserDoc, "follower", FieldValue.arrayUnion(getCurrentUserUID()))
            batch.update(CurrentUserDoc, "following", FieldValue.arrayUnion(userID))
            UserCollection.add(dataFollower)
            CurrentUserCollection.add(dataFollowing)
        }
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun onUnfollowUser(userID: String) = Completable.create { emitter ->
        val UserDoc = userCollectionRef.document(userID)
        val UserCollection = userCollectionRef.document(userID).collection("follower")
        Firebase.firestore.runTransaction { transaction ->
            val result = transaction.get(UserDoc).toObject(ProfileModel::class.java)
            result?.post?.let{
                for (doc in it){
                    val postCollectionDoc = postCollectionRef.document(doc)
                    transaction.update(postCollectionDoc, "viewby", FieldValue.arrayRemove(getCurrentUserUID()))
                }
            }
            transaction.update(UserDoc, "follower", FieldValue.arrayRemove(getCurrentUserUID()))
        }
            .addOnSuccessListener {
                UserCollection.whereEqualTo("userid", getCurrentUserUID()).get()
                    .addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            for (document in it.documents) {
                                UserCollection.document(document.id).delete().addOnFailureListener {
                                    emitter.onComplete()
                                }
                            }
                        }
                        emitter.onComplete()
                    }
                    .addOnFailureListener { emitter.onError(it) }
            }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun onUnfollowCurrentUser(userID: String) = Completable.create { emitter ->
        val CurrentUserDoc = userCollectionRef.document(getCurrentUserUID())
        val CurrentUserCollection = userCollectionRef.document(getCurrentUserUID()).collection("following")
        Firebase.firestore.runTransaction { transaction ->
            transaction.update(CurrentUserDoc, "following", FieldValue.arrayRemove(userID))
        }
            .addOnSuccessListener {
                CurrentUserCollection.whereEqualTo("userid", userID).get()
                    .addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            for (document in it.documents) {
                                CurrentUserCollection.document(document.id).delete()
                            }
                        }
                    }
                    .addOnFailureListener { emitter.onError(it) }
            }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun getCurrentUserProfile(): Observable<ProfileModel> {
        return Observable.create { emitter ->
            userCollectionRef.document(getCurrentUserUID())
                .addSnapshotListener { querySnapshot, e ->
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

}