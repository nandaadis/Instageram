package com.example.instageram.auth.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.instageram.auth.data.AuthRepository
import com.example.instageram.auth.ui.view.AuthListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var authListener: AuthListener? = null

    private val disposables = CompositeDisposable()

    fun registerUser(username: String, password: String) {
        authListener?.onLoading()

        val disposable = repository.register(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun registerGoogle(account: GoogleSignInAccount) {
        authListener?.onLoading()

        val disposable = repository.registerGoogle(account)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun uploadImageUser(photo : Uri, username: String) {
        authListener?.onLoading()

        val disposable = repository.uploadImage(photo, username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val disposable2 = repository.firestoreUser(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        authListener?.onSuccess()
                    }, {
                        authListener?.onFailure(it.message!!)
                    } )
                disposables.add(disposable2)
            }, {
                authListener?.onFailure(it.message!!)
            } )
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }



}