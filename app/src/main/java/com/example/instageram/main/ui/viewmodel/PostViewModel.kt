package com.example.instageram.main.ui.viewmodel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.instageram.main.data.PostRepository
import com.example.instageram.main.data.model.CaptionModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.main.data.model.postmodel.CaptionHolderModel
import com.example.instageram.main.data.model.postmodel.PhotoHolderModel
import com.example.instageram.main.data.model.postmodel.PostHolderModel
import com.example.instageram.main.data.model.postmodel.ProfileHolderModel
import com.example.instageram.main.ui.view.search.PostAdapter
import com.example.instageram.main.ui.view.search.PostListener
import com.example.instageram.main.ui.view.search.TimelineListener
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    var disposables = CompositeDisposable()
    var postListener: PostListener? = null
    var timelineListener: TimelineListener? = null

    private var _profile = MutableLiveData<ProfileHolderModel>()
    fun profile(): LiveData<ProfileHolderModel> {
        return _profile
    }

    private var _currentuserprofile = MutableLiveData<ProfileModel>()
    fun currentuserprofile(): LiveData<ProfileModel> {
        return _currentuserprofile
    }

    private var _post = MutableLiveData<PostHolderModel>()
    fun post(): LiveData<PostHolderModel> {
        return _post
    }

    private var _photoprofile = MutableLiveData<PhotoHolderModel>()
    fun photoProfile(): LiveData<PhotoHolderModel> {
        return _photoprofile
    }

    private var _photo = MutableLiveData<PhotoHolderModel>()
    fun photo(): LiveData<PhotoHolderModel> {
        return _photo
    }

    private var _caption = MutableLiveData<CaptionHolderModel>()
    fun caption(): LiveData<CaptionHolderModel> {
        return _caption
    }



    fun getCurrentUserUID(): String {
        return repository.getCurrentUserUID()
    }

    lateinit var caption: CaptionHolderModel
    var captionUsername: String = ""
    var captionDesc: String = ""
    var postOwnerUID: String = ""
    var postID: String = ""

    fun makeDesc(it: CaptionModel, holder: PostAdapter.ViewHolder, PostOwnerUID: String = "", PostID: String = "") {
        if (PostOwnerUID.isNotEmpty()) {
            postOwnerUID = PostOwnerUID
            postID = PostID
        }

        if (it.username.isNotEmpty()) {
            caption = CaptionHolderModel(CaptionModel("${it.username} ", captionDesc), holder, postOwnerUID, postID)
            captionUsername = it.username
        }
        if (it.desc.isNotEmpty()) {
            caption = CaptionHolderModel(CaptionModel(captionUsername, it.desc), holder, postOwnerUID, postID)
            captionDesc = it.desc
        }
        _caption.value = caption
    }

    fun getPostDetail(
        postID: String,
        holder: PostAdapter.ViewHolder
    ) {
        val disposable = repository.getPostDetail(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _post.value = PostHolderModel(it, holder)
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getUserDetail(
        userID: String,
        holder: PostAdapter.ViewHolder
    ) {
        val disposable = repository.getUserProfile(userID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profile.value = ProfileHolderModel(it, holder)
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getPhotoProfile(photopath: String, holder: PostAdapter.ViewHolder) {
        val disposable2 = repository.getPhoto(photopath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _photoprofile.value = PhotoHolderModel(it, holder)
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun getCurrentUserProfile() {
        val disposable = repository.getCurrentUserProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentuserprofile.value = it
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getPhotoPost(photopath: String, holder: PostAdapter.ViewHolder) {
        val disposable2 = repository.getPhoto(photopath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _photo.value = PhotoHolderModel(it, holder)
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun sendLove(postID: String, token: String, username: String) {
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
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun sendUnlove(postID: String) {
        val disposable = repository.sendUnlove(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                postListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun SearchListConfig(fragment: Fragment): FirestorePagingOptions<PostModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .orderBy("postowner")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(2)
            .setPageSize(6)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<PostModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, PostModel::class.java)
            .build()

        return options
    }

    fun TimelineListConfig(fragment: Fragment): FirestorePagingOptions<PostModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("post")
                .whereArrayContains("viewby", getCurrentUserUID())
                .orderBy("timestamp", Query.Direction.DESCENDING)

        Log.d(Util.TAG, "di adapter mQuery cek${getCurrentUserUID()}")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(2)
            .setPageSize(6)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<PostModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, PostModel::class.java)
            .build()

        return options
    }

    fun checkQuery() {
        val disposable = repository.checkQuery()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isEmpty()) {
                    timelineListener?.onNoQuery()
                    Log.d(Util.TAG, "Apakah ini berjalan?")
                }
            }, {
                timelineListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun checkUserID() {
        val disposable = repository.checkUserId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(Util.TAG, "ini di VM on Complete")
            }, {
                Log.d(Util.TAG, "ini di VM on Error == ${it.message}")
                Log.d(Util.TAG, "cekk ${it.message}")
                if (it.message == "noUserID") {
                    timelineListener?.onNoUserID()
                } else {
                    timelineListener?.onFailure(it.message!!)
                }
            })
        disposables.add(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}