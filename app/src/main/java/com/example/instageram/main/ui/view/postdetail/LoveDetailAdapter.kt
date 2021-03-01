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
import com.example.instageram.main.data.model.LoveListModel
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.view.myprofile.MyProfileFragmentDirections
import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.item_foll_list.view.*
import kotlinx.android.synthetic.main.item_love_list.view.*
import kotlinx.android.synthetic.main.item_love_list.view.iv_photo
import kotlinx.android.synthetic.main.item_love_list.view.iv_photo_text
import kotlinx.android.synthetic.main.item_love_list.view.layout
import kotlinx.android.synthetic.main.item_love_list.view.tv_userid
import kotlinx.android.synthetic.main.item_love_list.view.tv_username

class LoveDetailAdapter(
    private val viewModel: DetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val view: View,
    private val options: FirestorePagingOptions<LoveListModel>,
    private val swipeRefreshLayout: SwipeRefreshLayout
) :  FirestorePagingAdapter<LoveListModel, LoveDetailAdapter.ViewHolder>(options) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoveDetailAdapter.ViewHolder {
        return LoveDetailAdapter.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_love_list, parent, false)
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


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: LoveListModel) {
        viewModel.getLoveList(model.userid, holder)

        viewModel.love().observe(lifecycleOwner) {
//            it.holder.itemView.iv_photo.setBackgroundColor(it.color)
//            Glide.with(it.holder.itemView.context).load(it.color).circleCrop().into(it.holder.itemView.iv_photo)
            it.holder.itemView.iv_photo.setBackgroundColor(it.color)
            it.holder.itemView.iv_photo_text.text = it.userid.substring(0,1).toUpperCase()
            it.holder.itemView.tv_userid.text = it.userid
            it.holder.itemView.tv_username.text = it.username
        }

        holder.itemView.layout.setOnClickListener {
            if (viewModel.getCurrentUserUID() == model.userid){
                val action = LoveDetailFragmentDirections.actionLoveDetailFragmentToMyProfileFragment()
                view.findNavController().navigate(action)
            } else {
                val action = LoveDetailFragmentDirections.actionLoveDetailFragmentToOtherProfileFragment(model.userid)
                view.findNavController().navigate(action)
            }
        }
    }

}