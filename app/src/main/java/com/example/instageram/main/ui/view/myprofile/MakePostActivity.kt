package com.example.instageram.main.ui.view.myprofile

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.databinding.ActivityEditProfileBinding
import com.example.instageram.databinding.ActivityMakePostBinding
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import java.io.File

class MakePostActivity : AppCompatActivity(), MakePostListener {
    private lateinit var bundle: Bundle
    private lateinit var binding: ActivityMakePostBinding
    private lateinit var photoUri: Uri

    private val myProfileSource = MyProfileSource()
    private val myProfileRepository = MyProfileRepository(myProfileSource)
    private val factory = MyProfileModelFactory(myProfileRepository)
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_post)
        binding = ActivityMakePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProviders.of(this, factory).get(MyProfileViewModel::class.java)
        viewModel.makePostListener = this

        intent?.extras?.let { bundle = it }
        bundle.getString("photoUri")?.let { photoUri = it.toUri() }

        Glide
            .with(this)
            .load(photoUri)
            .centerCrop()
            .into(binding.ivThumbnail)

        binding.ivClose.setOnClickListener {
            val fdelete = File(photoUri.path)
            fdelete.delete()
            finish()
        }

        binding.ivDone.setOnClickListener {
            viewModel.makePost(photoUri, binding.etDesc.text.toString())
        }


    }

    override fun onLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun makePostSuccess() {
        binding.progressbar.visibility = View.GONE
        finish()
        val fdelete = File(photoUri.path)
        fdelete.delete()
    }

    override fun makePostFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }
}