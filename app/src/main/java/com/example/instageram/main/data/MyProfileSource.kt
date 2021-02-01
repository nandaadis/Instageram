package com.example.instageram.main.data

import android.net.Uri
import android.util.Log
import com.example.instageram.main.data.model.EditProfileModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

class MyProfileSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userCollectionRef = Firebase.firestore.collection("user")
    private val postCollectionRef = FirebaseFirestore.getInstance().collection("post")
    private val StorageRef = Firebase.storage.reference

    fun getMyProfileData(): Observable<ProfileModel> {
        return Observable.create { emitter ->
            userCollectionRef.document(firebaseAuth.uid!!).get()
                .addOnCompleteListener { task ->
                    val data = task.result?.toObject(ProfileModel::class.java)
                    if (data != null) {
                        emitter.onNext(data)
                        emitter.onComplete()
                    }

                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getPhotolist(): Observable<List<String>> {
        return Observable.create() { emitter ->

            postCollectionRef.whereEqualTo("postowner", getCurrentUserUID()).orderBy("timestamp")
                .get()
                .addOnSuccessListener { documents ->
                    var data: MutableList<String> = mutableListOf()
                    for (doc in documents){
                        Log.d(Util.TAG, "source ${doc.id}")
                        data.add(doc.id)
                    }
                    emitter.onNext(data)
                    emitter.onComplete()
                }.addOnFailureListener {
                    emitter.onError(it)
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

    fun uploadImage(
        photo: Uri,
        photopathold: String,
        userID: String,
        username: String,
        title: String,
        bio: String
    ): Observable<EditProfileModel> {
        return Observable.create { emitter ->
            val dateNow = Calendar.getInstance().timeInMillis
            StorageRef.child("photoprofile/${dateNow}.jpeg").putFile(photo)
                .addOnFailureListener {
                    emitter.onError(it)
                }
                .addOnSuccessListener { taskSnapshot ->
                    val data = EditProfileModel(
                        taskSnapshot.storage.path,
                        userID,
                        username,
                        title,
                        bio
                    )
                    emitter.onNext(data)
                }
        }
    }

    fun setUserProfile(it: EditProfileModel) = Completable.create { emitter ->
        userCollectionRef.document(firebaseAuth.uid!!).set(it, SetOptions.merge())
            .addOnSuccessListener {
                emitter.onComplete()
            }
            .addOnFailureListener {
                emitter.onError(it)
            }
    }

    //MakePost
    fun uploadImagePost(photo: Uri): Observable<String> {
        return Observable.create { emitter ->
            val dateNow = Calendar.getInstance().timeInMillis
            StorageRef.child("post/${dateNow}.jpeg").putFile(photo)
                .addOnFailureListener {
                    emitter.onError(it)
                }
                .addOnSuccessListener { taskSnapshot ->
                    emitter.onNext(taskSnapshot.storage.path)
                }
        }
    }

    fun makePost(photopath: String, desc: String) = Completable.create { emitter ->
        Firebase.firestore.runTransaction { it ->
            val millis = Calendar.getInstance().timeInMillis
            val docName = UUID.randomUUID().toString()
            val userUID = firebaseAuth.uid!!
            val userDocRef = userCollectionRef.document(userUID)
            val postDocRef = postCollectionRef.document(docName)

            val result = it.get(userDocRef).toObject(ProfileModel::class.java)
            val data = PostModel(
                userUID,
                arrayListOf(),
                arrayListOf(photopath),
                millis.toString(),
                desc,
                result?.follower!!,
                0
            )

            it.set(postDocRef, data)
            it.update(userDocRef, "post", FieldValue.arrayUnion(docName))
        }.addOnSuccessListener {
            emitter.onComplete()
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }

    fun getPostData(postUID: String): Observable<PostModel> {
        return Observable.create { emitter ->
            postCollectionRef.document(postUID).get()
                .addOnCompleteListener { task ->
                    val data = task.result?.toObject(PostModel::class.java)
                    if (data != null) {
                        emitter.onNext(data)
                        emitter.onComplete()
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getThumbnail(imagePath: String): Observable<ByteArray> {
        return Observable.create() { emitter ->
            val MAX_DOWNLOAD = (10 * 1024 * 1024).toLong()
            StorageRef.child(imagePath).getBytes(MAX_DOWNLOAD).addOnCompleteListener { bytes ->
                bytes.result?.let { emitter.onNext(it) }
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }


    fun getCurrentUserUID(): String {
        return firebaseAuth.uid!!
    }


}