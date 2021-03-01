package com.example.instageram.main.ui.view.search

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.main.data.model.CaptionModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.view.MainActivity
import com.example.instageram.main.ui.view.postdetail.DetailPostFragment
import com.example.instageram.main.ui.view.postdetail.DetailPostFragmentDirections
import com.example.instageram.main.ui.viewmodel.PostViewModel
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.activity_dummy.view.*
import kotlinx.android.synthetic.main.item_post_list.view.*

class PostAdapter(
    private val viewModel: PostViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val view: View,
    private val options: FirestorePagingOptions<PostModel>,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    val navListener: NavListener,
    val activity: FragmentActivity?
) : FirestorePagingAdapter<PostModel, PostAdapter.ViewHolder>(options) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PostAdapter.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_list, parent, false)
        )
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> {
                swipeRefreshLayout.isRefreshing = true
            }

            LoadingState.LOADING_MORE -> {
                swipeRefreshLayout.isRefreshing = true
            }

            LoadingState.LOADED -> {
                swipeRefreshLayout.isRefreshing = false
            }

            LoadingState.ERROR -> {
                swipeRefreshLayout.isRefreshing = false
            }

            LoadingState.FINISHED -> {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: PostModel) {
        var isLove = false
        var token = ""
        var username = ""
        var caption: CaptionModel = CaptionModel("", "")
        val UserUID = viewModel.getCurrentUserUID()
        var PostOwnerUID = model.postowner
        var PostID = model.postid

        holder.itemView.shimmer_photo_profile.startShimmerAnimation()
        holder.itemView.shimmer_photo.startShimmerAnimation()

        viewModel.getPostDetail(model.postid, holder)
        viewModel.getUserDetail(model.postowner, holder)
        viewModel.getCurrentUserProfile()

        viewModel.currentuserprofile().observe(lifecycleOwner) {
            username = it.username
        }


        viewModel.profile().observe(lifecycleOwner) {
            it.holder.itemView.tv_userid.text = it.profile.userid
            token = it.profile.token

            caption = CaptionModel(username = it.profile.username)
            viewModel.makeDesc(caption, it.holder)
            viewModel.getPhotoProfile(it.profile.photopath, it.holder)
        }

        viewModel.post().observe(lifecycleOwner) {
            if (it.post.loveby.contains(UserUID)) {
                isLove = true
                it.holder.itemView.iv_love.visibility = View.INVISIBLE
                it.holder.itemView.iv_loved.visibility = View.VISIBLE
            } else {
                isLove = false
                it.holder.itemView.iv_love.visibility = View.VISIBLE
                it.holder.itemView.iv_loved.visibility = View.INVISIBLE
            }
            it.holder.itemView.tv_love_by.text = "${it.post.loveby.size} suka"

            if (it.post.commentcount != 0) {
                it.holder.itemView.tv_comment.text = "Lihat semua ${it.post.commentcount} komentar"
            } else {
                it.holder.itemView.tv_comment.visibility = View.GONE
            }

            var timestamp =
                android.text.format.DateFormat.format("dd MMM", it.post.timestamp.toLong())
                    .toString()

            it.holder.itemView.tv_date.text = timestamp

            caption = CaptionModel(desc = it.post.desc)
            viewModel.makeDesc(caption, it.holder, PostOwnerUID = it.post.postowner , PostID = it.post.postid)
            viewModel.getPhotoPost(it.post.photopath[0], it.holder)
        }

        viewModel.photo().observe(lifecycleOwner) {
            it.holder.itemView.shimmer_photo.visibility = View.GONE

            val bmp = BitmapFactory.decodeByteArray(it.photo, 0, it.photo.size)
            Glide
                .with(view)
                .load(bmp)
                .centerCrop()
                .into(it.holder.itemView.iv_photo)
        }

        viewModel.photoProfile().observe(lifecycleOwner) {
            it.holder.itemView.shimmer_photo_profile.visibility = View.GONE

            val bmp = BitmapFactory.decodeByteArray(it.photo, 0, it.photo.size)
            Glide
                .with(view)
                .load(bmp)
                .circleCrop()
                .into(it.holder.itemView.iv_photo_profile)
        }

        viewModel.caption().observe(lifecycleOwner) {
            //Desc
            val name = "${it.caption.username} "
            val text = name + it.caption.desc
            val startName = text.indexOf(name)
            val endName = startName + name.length
            val startComment = text.indexOf(it.caption.desc)
            val endComment = text.length
            val spannableString = SpannableString(text)
            val nameClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (UserUID == it.postOwnerUID) {
                        navListener.goToMyProfile()
                    } else {
                        navListener.goToOtherProfile(it.postOwnerUID)
                    }
                }



                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }
            val commentClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    navListener.goToCommentDetail(it.postID)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(
                nameClickableSpan,
                startName,
                endName,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                commentClickableSpan,
                startComment,
                endComment,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            it.holder.itemView.tv_desc.text = spannableString
            it.holder.itemView.tv_desc.movementMethod = LinkMovementMethod.getInstance()

            if (it.holder.itemView.tv_desc.lineCount > 1) {
                it.holder.itemView.tv_read_more.visibility = View.VISIBLE
            }

            it.holder.itemView.tv_read_more.setOnClickListener() { view ->
                it.holder.itemView.tv_desc.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                it.holder.itemView.tv_read_more.visibility = View.GONE
            }
        }

        holder.itemView.iv_photo_profile.setOnClickListener {
            if (UserUID == PostOwnerUID) {
                navListener.goToMyProfile()
            } else {
                navListener.goToOtherProfile(PostOwnerUID)
            }
        }

        holder.itemView.tv_userid.setOnClickListener {
            if (UserUID == PostOwnerUID) {
                navListener.goToMyProfile()
            } else {
                navListener.goToOtherProfile(PostOwnerUID)
            }
        }

        holder.itemView.tv_love_by.setOnClickListener {
            navListener.goToLoveDetail(model.postid)
        }

        holder.itemView.iv_comment.setOnClickListener {
            navListener.goToCommentDetail(model.postid)

        }

        holder.itemView.tv_comment.setOnClickListener {
            navListener.goToCommentDetail(model.postid)
        }

//Love
        holder.itemView.iv_love.setOnClickListener {
            viewModel.sendLove(model.postid, token, username)
            isLove = true
        }

        holder.itemView.iv_loved.setOnClickListener {
            viewModel.sendUnlove(model.postid)
            isLove = true
        }

//Photo
        val builder: Zoomy.Builder = Zoomy.Builder(activity).target(holder.itemView.iv_photo)
        builder.register()

        holder.itemView.view_photo.setOnClickListener(object : DetailPostFragment.DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                if (isLove == false) {
                    viewModel.sendLove(model.postid, token, username)

                    val message = PushNotifModel(
                        NotifDataModel("Instageram Memanggil", "$username menyukai Postingan anda"),
                        token
                    )

                    isLove = true
                    holder.itemView.lottie_love.visibility = View.VISIBLE
                    holder.itemView.lottie_love.playAnimation()
                    Handler(Looper.getMainLooper()).postDelayed({
                        holder.itemView.lottie_love.visibility = View.INVISIBLE
                    }, 800)
                } else {
                    viewModel.sendUnlove(model.postid)
                    isLove = false
                }
            }
        })
    }

    abstract class DoubleClickListener : View.OnClickListener {
        val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds

        private var lastClickTime: Long = 0
        override final fun onClick(v: View) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
                lastClickTime = 0
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View)

    }


}
