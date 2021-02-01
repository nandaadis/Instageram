package com.example.instageram.main.data

import android.util.Log
import com.example.instageram.main.data.model.PostModel
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
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody


class DetailSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userCollectionRef = Firebase.firestore.collection("user")
    private val postCollectionRef = FirebaseFirestore.getInstance().collection("post")
    private val StorageRef = Firebase.storage.reference

    fun getPostUser(postID: String): Observable<ProfileModel> {
        lateinit var data: ProfileModel

        return Observable.create { emitter ->
            Firebase.firestore.runTransaction { it ->
                val postDocRef = postCollectionRef.document(postID)
                val resultPost = it.get(postDocRef).toObject(PostModel::class.java)

                val userDocRef = userCollectionRef.document(resultPost?.postowner!!)
                val resultUser = it.get(userDocRef).toObject(ProfileModel::class.java)

                resultUser?.let {
                    data = it
                }
            }.addOnSuccessListener {
                emitter.onNext(data)
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

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

    fun sendLove(postID: String) = Completable.create { emitter ->
        postCollectionRef.document(postID)
            .update("loveby", FieldValue.arrayUnion(getCurrentUserUID()))
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun sendUnlove(postID: String) = Completable.create { emitter ->
        postCollectionRef.document(postID)
            .update("loveby", FieldValue.arrayRemove(getCurrentUserUID()))
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { emitter.onError(it) }
    }


    fun sendNotif(message: PushNotifModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotif(message)
            if (response.isSuccessful) {
                Log.d(Util.TAG, "Response: ${response}")
            } else {
                Log.d(Util.TAG, "error lagi ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.d(Util.TAG, "halo $e")
        }
    }


//        if(response.isSuccessful) {
//                Log.d(Util.TAG, "Response: ${Gson().toJson(response)}")
//            } else {
//                Log.d(Util.TAG, response.errorBody().toString())
//            }

//        try {
//            val response = RetrofitInstance.api.postNotif(message)
//            if(response.isSuccessful) {
//                Log.d(Util.TAG, "Response: ${Gson().toJson(response)}")
//            } else {
//                Log.d(Util.TAG, response.errorBody().toString())
//            }
//        } catch(e: Exception) {
//            Log.e(Util.TAG, e.toString())
//        }
//    }
//    }

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


}