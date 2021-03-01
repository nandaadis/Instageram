package com.example.instageram.main.ui.view.postdetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.main.data.model.FollListModel
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_foll_list.view.*
import kotlinx.android.synthetic.main.item_foll_list.view.iv_photo_text


class FollowerDetailAdapter(
    private val viewModel: DetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val view: View,
    private val options: FirestorePagingOptions<FollListModel>,
    private val swipeRefreshLayout: SwipeRefreshLayout
) : FirestorePagingAdapter<FollListModel, FollowerDetailAdapter.ViewHolder>(options) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowerDetailAdapter.ViewHolder {
        return FollowerDetailAdapter.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_foll_list, parent, false)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: FollListModel) {
        viewModel.getFollowerList(model.userid, holder)
        Log.d(Util.TAG, "didalem adapter ${model.userid}")

        viewModel.follower().observe(lifecycleOwner) {
//            it.holder.itemView.iv_photo.setBackgroundColor(it.color)
//            Glide.with(it.holder.itemView.context).load(it.color).circleCrop()
//                .into(it.holder.itemView.iv_photo)
            it.holder.itemView.iv_photo.setBackgroundColor(it.color)
            it.holder.itemView.iv_photo_text.text = it.userid.substring(0, 1).toUpperCase()
            it.holder.itemView.tv_userid.text = it.userid
            Log.d(Util.TAG, "didalem adapter observe ${it.userid}")
            it.holder.itemView.tv_username.text = it.username
        }

        holder.itemView.layout.setOnClickListener {
            if (viewModel.getCurrentUserUID() == model.userid) {
                val action =
                    FollDetailFragmentDirections.actionFollDetailFragmentToMyProfileFragment()
                view.findNavController().navigate(action)
            } else {
                val action =
                    FollDetailFragmentDirections.actionFollDetailFragmentToOtherProfileFragment(
                        model.userid
                    )
                view.findNavController().navigate(action)
            }
        }
    }


}