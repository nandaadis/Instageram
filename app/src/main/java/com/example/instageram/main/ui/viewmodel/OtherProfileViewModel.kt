package com.example.instageram.main.ui.viewmodel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.OtherProfileRepository
import com.example.instageram.main.data.model.PhotoThumbnail2Model
import com.example.instageram.main.data.model.PhotoThumbnailModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.view.otherprofile.OtherProfileListener
import com.example.instageram.main.ui.view.otherprofile.OtherProfilePhotoAdapter
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OtherProfileViewModel(private val repository: OtherProfileRepository) : ViewModel() {

    var disposables = CompositeDisposable()
    var otherProfileListener: OtherProfileListener? = null

    private var _profile = MutableLiveData<ProfileModel>()
    fun profile(): LiveData<ProfileModel> {
        return _profile
    }

    private var _profilecurrentuser = MutableLiveData<ProfileModel>()
    fun profileCurrentUser(): LiveData<ProfileModel> {
        return _profilecurrentuser
    }

    private var _photoprofile = MutableLiveData<ByteArray>()
    fun photoProfile(): LiveData<ByteArray> {
        return _photoprofile
    }

    private var _thumbnail = MutableLiveData<PhotoThumbnail2Model>()
    fun thumbnail(): LiveData<PhotoThumbnail2Model> {
        return _thumbnail
    }

    fun getProfile(UserID: String) {
        otherProfileListener?.onLoading()

        val disposable = repository.getProfileData(UserID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profile.value = it
                otherProfileListener?.onSuccess()
            }, {
                otherProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun getPhotoProfile(imagePath: String) {
        val disposable = repository.getPhotoProfile(imagePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _photoprofile.value = it
                otherProfileListener?.onSuccessPhotoProfile()
            }, {
                otherProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun getCurrentUserProfile() {
        val disposable = repository.getCurrentUserProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profilecurrentuser.value = it
            }, {
                otherProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun onFollow(userID: String, token: String, username: String) {
//        otherProfileListener?.onLoading()
        val disposable = repository.onFollow(userID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val message = PushNotifModel(
                    NotifDataModel("Instageram Memanggil", "$username mulai mengikuti anda"),
                    token
                )
                repository.sendNotif(message)
                otherProfileListener?.onFollowSuccess()
            }, {
                otherProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun onUnFollow(userID: String) {
//        otherProfileListener?.onLoading()
        val disposable = repository.onUnfollowUser(userID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                otherProfileListener?.onUnFollowSuccess()
                val disposable2 = repository.onUnfollowCurrentUser(userID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        otherProfileListener?.onUnFollowSuccess()
                    }, {
                        otherProfileListener?.onFailure(it.message!!)
                    })
                disposables.add(disposable2)

            }, {
                otherProfileListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getThumbnail(photopath: String, holder: OtherProfilePhotoAdapter.ViewHolder) {
        val disposable2 = repository.getPhotoProfile(photopath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _thumbnail.value = PhotoThumbnail2Model(it, holder)
            }, {
                otherProfileListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun OtherProfilePhotoConfig(
        fragment: Fragment,
        UserID: String
    ): FirestorePagingOptions<PostModel> {
        val mQuery = FirebaseFirestore.getInstance().collection("post")
            .whereEqualTo(
                "postowner",
                UserID
            ).orderBy("timestamp", Query.Direction.DESCENDING)

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(2)
            .setPageSize(5)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<PostModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, PostModel::class.java)
            .build()

        return options
    }


    fun getCurrentUser(): String {
        return repository.getCurrentUserUID()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}