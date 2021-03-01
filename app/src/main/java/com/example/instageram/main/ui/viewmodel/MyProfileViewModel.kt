package com.example.instageram.main.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.model.EditProfileModel
import com.example.instageram.main.data.model.PhotoThumbnailModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.main.ui.view.myprofile.*
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyProfileViewModel(private val repository: MyProfileRepository) : ViewModel() {

    var myProfileListener: MyProfileListener? = null
    var myProfileEditProfilListener: MyProfileEditProfilListener? = null
    var getMyProfileListener: GetMyProfileListener? = null
    var makePostListener: MakePostListener? = null
    var myProfileMyPhotoListener: MyProfileMyPhotoListener? = null
    var disposables = CompositeDisposable()

    private var _retreiveMyProfile = MutableLiveData<ProfileModel>()
    fun retreiveMyProfile(): LiveData<ProfileModel> {
        return _retreiveMyProfile
    }

    private var _profile = MutableLiveData<ProfileModel>()
    fun profile(): LiveData<ProfileModel> {
        return _profile
    }

    private var _photoList = MutableLiveData<List<String>>()
    fun photoList(): LiveData<List<String>> {
        return _photoList
    }

    private var _thumbnail = MutableLiveData<PhotoThumbnailModel>()
    fun thumbnail(): LiveData<PhotoThumbnailModel> {
        return _thumbnail
    }

    fun getMyProfile() {
        getMyProfileListener?.onLoading()

        val disposable = repository.getMyProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profile.value = it
                getMyProfileListener?.onSuccess()
            }, {
                getMyProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun getPhotoProfile(imagePath: String) {
        getMyProfileListener?.onLoadingPhotoProfile()
        Log.d(Util.TAG, imagePath)

        val disposable = repository.getPhotoProfile(imagePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getMyProfileListener?.onSuccessPhotoProfile(it)
            }, {
                getMyProfileListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun getMyProfileEdit() {
        myProfileEditProfilListener?.onLoading()

        val disposable = repository.getMyProfileEdit()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _retreiveMyProfile.value = it
            }, {
                myProfileEditProfilListener?.onFailure(it.message.toString())
            }, { myProfileEditProfilListener?.onSuccess() })
        disposables.add(disposable)
    }

    fun getPhotoProfileEdit(imagePath: String) {
        myProfileEditProfilListener?.onLoadingPhotoProfile()
        Log.d(Util.TAG, imagePath)

        val disposable = repository.getPhotoProfile(imagePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                myProfileEditProfilListener?.onSuccessPhotoProfile(it)
            }, {
                myProfileEditProfilListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
    }

    fun uploadImage(
        photo: Uri,
        photopathOld: String,
        userid: String,
        username: String,
        title: String = "",
        bio: String = ""
    ) {
        myProfileEditProfilListener?.onLoading()

        val disposable = repository.uploadImage(photo, photopathOld, userid, username, title, bio)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val disposable2 = repository.setUserProfile(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        myProfileEditProfilListener?.setEditProfileSuccess()
                    }, {
                        myProfileEditProfilListener?.setEditProfileFailure(it.message!!)
                    })
                disposables.add(disposable2)
            }, {
                myProfileEditProfilListener?.uploadImageFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun setEditProfile(
        photopathOld: String,
        userid: String,
        username: String,
        title: String = "",
        bio: String = ""
    ) {
        val data = EditProfileModel(
            photopathOld,
            userid,
            username,
            title,
            bio
        )

        val disposable2 = repository.setUserProfile(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                myProfileEditProfilListener?.setEditProfileSuccess()
            }, {
                myProfileEditProfilListener?.setEditProfileFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun makePost(
        photo: Uri,
        desc: String = ""
    ) {
        makePostListener?.onLoading()

        val disposable = repository.uploadImagePost(photo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val disposable2 = repository.makePost(it, desc)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        makePostListener?.makePostSuccess()
                    }, {
                        makePostListener?.makePostFailure(it.message!!)
                    })
                disposables.add(disposable2)
            }, {
                makePostListener?.makePostFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    //Tab Layout My Photo
    fun getMyPhoto() {
        myProfileListener?.onLoading()

        val disposable = repository.getPhotolist()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _photoList.value = it.reversed()

                Log.d(Util.TAG, "viewmodel $it")
                Log.d(Util.TAG, "viewmodel descending ${it.reversed()}")

            }, {
                myProfileListener?.onFailure(it.message.toString())
            }, { myProfileListener?.onSuccess() })
        disposables.add(disposable)
    }

    fun getThumbnail(postUID: String, holder: MyProfileMyPhotoAdapter.ViewHolder) {
        myProfileMyPhotoListener?.onLoading()

        val disposable = repository.getPostData(postUID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(Util.TAG, "photopath ${it.photopath[0]}")
                val disposable2 = repository.getThumbnail(it.photopath[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _thumbnail.value = PhotoThumbnailModel(it, holder)
                        myProfileMyPhotoListener?.onSuccess(it)
                    }, {
//                        myProfileMyPhotoListener?.onFailure(it.message!!)
                    })
                disposables.add(disposable2)
            }, {
                myProfileMyPhotoListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getThumbnail2(photopath: String, holder: MyProfileMyPhotoAdapter.ViewHolder) {
        myProfileMyPhotoListener?.onLoading()
        val disposable2 = repository.getThumbnail(photopath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _thumbnail.value = PhotoThumbnailModel(it, holder)
                myProfileMyPhotoListener?.onSuccess(it)
            }, {
                myProfileMyPhotoListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }


    fun getCurrentUser(): String {
        return repository.getCurrentUserUID()
    }

    fun MyProfileMyphotoConfig(fragment: Fragment): FirestorePagingOptions<PostModel> {
        val mQuery = FirebaseFirestore.getInstance().collection("post")
            .whereEqualTo(
                "postowner",
                FirebaseAuth.getInstance().uid
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

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}