package com.example.instageram.main.ui.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.model.*
import com.example.instageram.main.ui.view.postdetail.*
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private var _currentuserprofile = MutableLiveData<ProfileModel>()
    fun currentuserprofile(): LiveData<ProfileModel> {
        return _currentuserprofile
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

    private var _caption = MutableLiveData<CaptionModel>()
    fun caption(): LiveData<CaptionModel> {
        return _caption
    }

    private var _love = MutableLiveData<LoveDetailModel>()
    fun love(): LiveData<LoveDetailModel> {
        return _love
    }

    private var _follower = MutableLiveData<FollowerDetailModel>()
    fun follower(): LiveData<FollowerDetailModel> {
        return _follower
    }

    private var _following = MutableLiveData<FollowingDetailModel>()
    fun following(): LiveData<FollowingDetailModel> {
        return _following
    }

    private var _comment = MutableLiveData<CommentDetailModel>()
    fun comment(): LiveData<CommentDetailModel> {
        return _comment
    }

    fun getCurrentUserUID(): String {
        return repository.getCurrentUserUID()
    }

    var caption = CaptionModel("", "")
    var captionUsername: String = ""
    var captionDesc: String = ""

    fun makeDesc(it: CaptionModel) {
        if (it.username.isNotEmpty()) {
            caption = CaptionModel("${it.username} ", captionDesc)
            captionUsername = it.username
        }
        if (it.desc.isNotEmpty()) {
            caption = CaptionModel(captionUsername, it.desc)
            captionDesc = it.desc
        }
        _caption.value = caption
    }

    fun getPostUser(
        postID: String
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

    fun getPostUserComment(
        postID: String
    ) {
        detailListener?.onLoading()

        val disposable = repository.getPostUser(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                detailListener?.onSuccess()
                _profile.value = it
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getPostDetail(
        postID: String
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

    fun getPostDetailComment(
        postID: String
    ) {
        detailListener?.onLoading()

        val disposable = repository.getPostDetail(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _post.value = it
                detailListener?.onSuccess()
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
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
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun sendUnlove(postID: String) {
        val disposable = repository.sendUnlove(postID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun sendComment(postID: String, comment: String, token : String, username : String) {
        val disposable = repository.sendComment(postID, comment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                detailListener?.onSuccessPhoto()
                val message = PushNotifModel(
                    NotifDataModel("Instageram Memanggil", "$username mengomentari Postingan anda"),
                    token
                )
                repository.sendNotif(message)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun getFollowerList(UserList: String, holder: FollowerDetailAdapter.ViewHolder) {
        val disposable2 = repository.getUser(UserList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _follower.value = FollowerDetailModel(holder, it.userid, it.username, it.color)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun getFollowingList(UserList: String, holder: FollowingDetailAdapter.ViewHolder) {
        val disposable2 = repository.getUser(UserList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _following.value = FollowingDetailModel(holder, it.userid, it.username, it.color)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun getLoveList(UserList: String, holder: LoveDetailAdapter.ViewHolder) {
        val disposable2 = repository.getUser(UserList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _love.value = LoveDetailModel(holder, it.userid, it.username, it.color)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun getCommentList(userID: String, holder: DetailCommentAdapter.ViewHolder, model: CommentListModel) {
        val disposable2 = repository.getUser(userID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _comment.value = CommentDetailModel(holder, it.userid, it.color, model)
            }, {
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable2)
    }

    fun getUserProfile(profilUID: String) {
        detailListener?.onLoading()
        val disposable2 = repository.getUserProfile(profilUID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profile.value = it
                detailListener?.onSuccess()
            }, {
                detailListener?.onFailure(it.message!!)
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
                detailListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun LoveListConfig(fragment: Fragment, postID: String): FirestorePagingOptions<LoveListModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("post")
                .document(postID)
                .collection("love")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .orderBy("userid")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(6)
            .setPageSize(16)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<LoveListModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, LoveListModel::class.java)
            .build()

        return options
    }

    fun CommentListConfig(fragment: Fragment, postID: String): FirestorePagingOptions<CommentListModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("post")
                .document(postID)
                .collection("comment")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .orderBy("userid")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(6)
            .setPageSize(16)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<CommentListModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, CommentListModel::class.java)
            .build()

        return options
    }

    fun FollowerListConfig(fragment: Fragment, userID: String): FirestorePagingOptions<FollListModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("user")
                .document(userID)
                .collection("follower")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .orderBy("userid")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(6)
            .setPageSize(16)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<FollListModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, FollListModel::class.java)
            .build()

        return options
    }

    fun FollowingListConfig(fragment: Fragment, userID: String): FirestorePagingOptions<FollListModel> {
        val mQuery =
            FirebaseFirestore.getInstance()
                .collection("user")
                .document(userID)
                .collection("following")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .orderBy("userid")

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(6)
            .setPageSize(16)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<FollListModel>()
            .setLifecycleOwner(fragment)
            .setQuery(mQuery, config, FollListModel::class.java)
            .build()

        return options
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}