package com.example.instageram.auth.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.instageram.auth.data.AuthRepository
import com.example.instageram.auth.ui.view.AuthListener
import com.example.instageram.auth.ui.view.CheckListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var authListener: AuthListener? = null
    var checkListener: CheckListener? = null

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

    fun loginUser(email: String, password: String) {
        authListener?.onLoading()

        val disposable = repository.login(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun checkUserId() {
        val disposable = repository.checkUserId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkListener?.UserYes()
            }, {
                checkListener?.UserNo()
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