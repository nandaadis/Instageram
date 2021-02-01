package com.example.instageram.main.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.model.PhotoThumbnailModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.main.ui.view.postdetail.DetailListener
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repository: DetailRepository) : ViewModel() {

    var disposables = CompositeDisposable()

    var detailListener: DetailListener? = null

    private var _profile = MutableLiveData<ProfileModel>()
    fun profile(): LiveData<ProfileModel> {
        return _profile
    }

    private var _post = MutableLiveData<PostModel>()
    fun post(): LiveData<PostModel> {
        return _post
    }

    private var _photoprofile = MutableLiveData<ByteArray>()
    fun photoProfile(): LiveData<ByteArray> {
        return _photoprofile
    }

    private var _photo = MutableLiveData<ByteArray>()
    fun photo(): LiveData<ByteArray> {
        return _photo
    }

    fun getCurrentUserUID(): String {
         return repository.getCurrentUserUID()
    }

    fun getPostUser(
        postID : String
    ) {
        detailListener?.onLoading()

        val disposable = repository.getPostUser(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                detailListener?.onSuccess()
                _profile.value = it
                val disposable2 = repository.getPhoto(it.photopath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _photoprofile.value = it
                        detailListener?.onSuccessPhotoProfile()
                    }, {
                        detailListener?.onFailure(it.message!!)
                    })
                disposables.add(disposable2)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getPostDetail(
        postID : String
    ) {
        detailListener?.onLoading()

        val disposable = repository.getPostDetail(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _post.value = it
                detailListener?.onSuccess()
                val disposable2 = repository.getPhoto(it.photopath[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _photo.value = it
                        detailListener?.onSuccessPhoto()
                    }, {
                        detailListener?.onFailure(it.message!!)
                    })
                disposables.add(disposable2)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun sendLove(postID:String, token: String, username: String) {
        val disposable = repository.sendLove(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val message = PushNotifModel(
                    NotifDataModel("Instageram Memanggil", "$username menyukai Postingan anda"),
                    token
                )
                repository.sendNotif(message)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun sendUnlove(postID:String) {
        val disposable = repository.sendUnlove(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}