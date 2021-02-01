package com.example.instageram.auth.data

import android.net.Uri
import com.example.instageram.auth.data.model.RegisterUsername
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AuthRepository(private val firebase: FirebaseAuthSource) {

    fun login(email: String, password: String) = firebase.login(email, password)

    fun checkUserId() = firebase.checkUserId()

    fun register(email: String, password: String) = firebase.register(email, password)

    fun uploadImage(photo: Uri, username:String) = firebase.uploadImage(photo, username)

    fun firestoreUser(data : RegisterUsername) = firebase.firestoreUser(data)

    fun registerGoogle(account: GoogleSignInAccount) = firebase.googleAuthForFirebase(account)
}