package com.example.instageram.main.ui.view.otherprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.instageram.main.ui.view.postdetail.FollowerDetailFragment
import com.example.instageram.main.ui.view.postdetail.FollowingDetailFragment

class OtherProfileAdapter(fm: FragmentManager, val bundle: Bundle) : FragmentStatePagerAdapter(fm) {
    // sebuah list yang menampung objek Fragment
    private val pages = listOf(
        OtherProfilePhotoFragment(),
        OtherProfileTagPhotoFragment()
    )

    // menentukan fragment yang akan dibuka pada posisi tertentu
    override fun getItem(position: Int): Fragment {
        pages[position].arguments = bundle
        pages[position]
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }
}