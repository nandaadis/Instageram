package com.example.instageram.auth.data

import android.net.Uri
import com.example.instageram.auth.data.model.RegisterUsername
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

class FirebaseAuthSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val imagesRef = Firebase.storage.reference
    private val userCollectionRef = Firebase.firestore.collection("user")


    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
                emitter.onComplete()
            else
                emitter.onError(it.exception!!)
        }
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
                emitter.onComplete()
            else
                emitter.onError(it.exception!!)
        }
    }

    fun uploadImage(photo: Uri, userid: String): Observable<RegisterUsername> {
        return Observable.create { emitter ->
            val dateNow = Calendar.getInstance().timeInMillis
            val uploadTask = imagesRef.child("photoprofile/${dateNow}.jpeg").putFile(photo)
            uploadTask.addOnFailureListener {
                emitter.onError(it)
            }.addOnSuccessListener { taskSnapshot ->
                val data = RegisterUsername(taskSnapshot.storage.path, userid, userid)
                emitter.onNext(data)
            }
        }
    }

    fun firestoreUser(RegisterUsername: RegisterUsername) = Completable.create { emitter ->
        val data = RegisterUsername(
            RegisterUsername.photopath,
            RegisterUsername.userid,
            RegisterUsername.userid
        )

        userCollectionRef.document(currentUser()).set(data)
            .addOnSuccessListener { documentReference ->
                emitter.onComplete()
            }
            .addOnFailureListener { e ->
                emitter.onError(e)
            }
    }


    fun googleAuthForFirebase(account: GoogleSignInAccount) = Completable.create { emitter ->
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener {
                emitter.onComplete()
            }.addOnFailureListener {
                emitter.onError(it)
            }

    }

    fun checkUserId() = Completable.create { emitter ->
        userCollectionRef.document(currentUser()).get().addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()){
                        emitter.onComplete()
                    }
                } else {
                    emitter.onError(task.exception!!)
                }

        }
//            OnCompleteListener<DocumentSnapshot> { task ->
//                if (task.isSuccessful) {
//                    if (task.result!!.exists()){
//                        emitter.onComplete()
//                    } else {
//                        emitter.onError(task.exception!!)
//                    }
//                } else {
//                    emitter.onError(task.exception!!)
//                }
//            })
    }

    fun currentUser(): String {
        return firebaseAuth.currentUser?.let {
            it.uid
        } ?: "404"
    }

}