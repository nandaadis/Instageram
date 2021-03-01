package com.example.instageram.main.ui.view.myprofile

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.dummy.dummyadapter
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.item_photo_thumbnail.view.*

class MyProfileMyPhotoAdapter(
    private val viewModel: MyProfileViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val view: View,
    private val options: FirestorePagingOptions<PostModel>,
    private val swipeRefreshLayout: SwipeRefreshLayout
) : FirestorePagingAdapter<PostModel, MyProfileMyPhotoAdapter.ViewHolder>(options) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo_thumbnail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, post: PostModel) {
        holder.itemView.shimmer_view_container.startShimmerAnimation()

        holder.itemView.iv_thumbnail.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToDetailPostFragment(post.postid)
            view.findNavController().navigate(action)
        }

        viewModel.thumbnail().observe(lifecycleOwner) {
            if (it.photo.isNotEmpty()) {
                it.holder.itemView.shimmer_view_container.visibility = View.GONE
                val bmp = BitmapFactory.decodeByteArray(it.photo, 0, it.photo.size)
                Glide
                    .with(it.holder.itemView.context)
                    .load(bmp)
                    .centerCrop()
                    .into(it.holder.itemView.iv_thumbnail)
            }
        }

        viewModel.getThumbnail2(post.photopath[0], holder)
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


}