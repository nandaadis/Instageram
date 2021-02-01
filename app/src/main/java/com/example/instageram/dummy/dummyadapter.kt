package com.example.instageram.dummy

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instageram.R
import com.example.instageram.auth.ui.view.AuthListener
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_photo_thumbnail.view.*
import java.util.*

class dummyadapter(val list:List<dummymodel>) : RecyclerView.Adapter<dummyadapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo_thumbnail, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.shimmer_view_container.startShimmerAnimation()

        fun coba() {
            Glide
                .with(holder.itemView.context)
                .load(list[position].bitmap)
                .centerCrop()
                .into(holder.itemView.iv_thumbnail)

        }

    }
}