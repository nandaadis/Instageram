package com.example.instageram.main.ui.view.postdetail

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.main.data.model.CommentListModel
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.item_comment.view.*

class DetailCommentAdapter(
    private val viewModel: DetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val view: View,
    private val options: FirestorePagingOptions<CommentListModel>,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val CurrentUID : String
) : FirestorePagingAdapter<CommentListModel, DetailCommentAdapter.ViewHolder>(options) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailCommentAdapter.ViewHolder {
        return DetailCommentAdapter.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun onError(e: Exception) {
        super.onError(e)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: CommentListModel) {
        viewModel.getCommentList(model.userid, holder, model)
//        var name = ""
//        var text = name + model.comment

        viewModel.comment().observe(lifecycleOwner) {
//            Glide.with(it.holder.itemView.context).load(it.color).circleCrop()
//                .into(it.holder.itemView.iv_photo_comment)
            it.holder.itemView.iv_photo_comment.setBackgroundColor(it.color)
            it.holder.itemView.iv_photo_text.text = it.userid.substring(0, 1).toUpperCase()
            it.holder.itemView.tv_date.text =
                android.text.format.DateFormat.format("dd MMM", it.model.timestamp.toLong()).toString()

            val name = "${it.userid} "
            val text = name + it.model.comment
            val startName = text.indexOf(name)
            val endName = startName + name.length
            val startComment = text.indexOf(it.model.comment)
            val endComment = text.length
            val spannableString = SpannableString(text)
            val nameClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (CurrentUID == it.model.userid) {
                        val action =
                            DetailCommentFragmentDirections.actionDetailCommentFragmentToMyProfileFragment()
                        view.findNavController().navigate(action)
                    } else {
                        val action =
                            DetailCommentFragmentDirections.actionDetailCommentFragmentToOtherProfileFragment(
                                it.model.userid
                            )
                        view.findNavController().navigate(action)
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

            it.holder.itemView.iv_photo_comment.setOnClickListener { view ->
                if (CurrentUID == it.model.userid) {
                    val action =
                        DetailCommentFragmentDirections.actionDetailCommentFragmentToMyProfileFragment()
                    view.findNavController().navigate(action)
                } else {
                    val action =
                        DetailCommentFragmentDirections.actionDetailCommentFragmentToOtherProfileFragment(
                            it.model.userid
                        )
                    view.findNavController().navigate(action)
                }
            }
        }


    }


}