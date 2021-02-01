package com.example.instageram.main.ui.view.myprofile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.instageram.R
import kotlinx.android.synthetic.main.fragment_my_profile.view.*

class MyProfileAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    // sebuah list yang menampung objek Fragment
    private val pages = listOf(
        MyProfileMyPhotoFragment(),
        MyProfileTagPhotoFragment()
    )

    // menentukan fragment yang akan dibuka pada posisi tertentu
    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }


}