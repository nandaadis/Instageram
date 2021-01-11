package com.example.instageram.auth.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.instageram.auth.data.model.RegisterUsername
import com.example.instageram.utils.Util
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FirebaseAuthSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val imagesRef = Firebase.storage.reference.child("images")
    private val userCollectionRef = Firebase.firestore.collection("user")


    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
                emitter.onComplete()
            else
                emitter.onError(it.exception!!)
        }
    }

    fun uploadImage(photo: Uri, username: String): Observable<RegisterUsername> {
        return Observable.create { emitter ->
            var uploadTask = imagesRef.putFile(photo)
            uploadTask.addOnFailureListener {
                emitter.onError(it)
            }.addOnSuccessListener { taskSnapshot ->
                val data = RegisterUsername(taskSnapshot.storage.downloadUrl.toString(), username)
                emitter.onNext(data)
            }
        }
    }

    fun firestoreUser(RegisterUsername: RegisterUsername) = Completable.create { emitter ->
        val data = hashMapOf(
            "username" to RegisterUsername.Username,
            "photourl" to RegisterUsername.PhotoUrl
        )

        userCollectionRef.document(currentUser()).set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(Util.TAG, "DocumentSnapshot written with ID: ${documentReference}")
                emitter.onComplete()
            }
            .addOnFailureListener { e ->
                emitter.onError(e)
                Log.w(Util.TAG, "Error adding document", e)
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


fun currentUser(): String {
    return firebaseAuth.currentUser?.let {
        it.uid
    } ?: "404"
}

}