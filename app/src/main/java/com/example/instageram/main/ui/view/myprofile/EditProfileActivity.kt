package com.example.instageram.main.ui.view.myprofile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.camerax.CameraActivity
import com.example.instageram.databinding.ActivityEditProfileBinding
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import java.io.File

class EditProfileActivity : AppCompatActivity(), MyProfileEditProfilListener {
    private lateinit var binding: ActivityEditProfileBinding

    private val myProfileSource = MyProfileSource()
    private val myProfileRepository = MyProfileRepository(myProfileSource)
    private val factory = MyProfileModelFactory(myProfileRepository)
    private lateinit var viewModel: MyProfileViewModel

    lateinit private var photoUri: Uri
    lateinit private var photoPathOld: String
    private var photoChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProviders.of(this, factory).get(MyProfileViewModel::class.java)
        viewModel.myProfileEditProfilListener = this

        viewModel.getMyProfileEdit()

        viewModel.retreiveMyProfile().observe(this, Observer {
            viewModel.getPhotoProfileEdit(it.photopath)
            photoPathOld = it.photopath
            binding.etName.setText(it.username)
            binding.etUserId.setText(it.userid)
            binding.etTitle.setText(it.title)
            binding.etBio.setText(it.bio)
        })

        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.tvChangePhotoProfile.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            this.startActivityForResult(intent, 21)
        }

        binding.ivPhotoProfile.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            this.startActivityForResult(intent, 21)
        }

        binding.ivDone.setOnClickListener {
            if (binding.etUserId.text.isNotEmpty() && binding.etName.text.isNotEmpty()) {
                if (photoChanged == true) {
                    viewModel.uploadImage(
                        photoUri,
                        photoPathOld,
                        binding.etUserId.text.toString(),
                        binding.etName.text.toString(),
                        binding.etTitle.text.toString(),
                        binding.etBio.text.toString()
                    )
                } else {
                    viewModel.setEditProfile(
                        photoPathOld,
                        binding.etUserId.text.toString(),
                        binding.etName.text.toString(),
                        binding.etTitle.text.toString(),
                        binding.etBio.text.toString()
                    )
                }
            } else {
                Toast.makeText(
                    this,
                    "User ID sama Username nggak boleh kosong ya",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra("result")?.let { photoUri = it.toUri() }
                photoChanged = true

                Glide
                    .with(this)
                    .load(photoUri)
                    .circleCrop()
                    .into(binding.ivPhotoProfile)
            }
        }
    }

    override fun onLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        binding.progressbar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoadingPhotoProfile() {
        binding.shimmerPhotoProfile.startShimmerAnimation()
    }

    override fun onSuccessPhotoProfile(byteArray: ByteArray) {
        binding.shimmerPhotoProfile.visibility = View.GONE

        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        Glide
            .with(this)
            .load(bmp)
            .circleCrop()
            .into(binding.ivPhotoProfile)
    }

    override fun uploadImageFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun setEditProfileSuccess() {
        binding.progressbar.visibility = View.GONE
        this?.onBackPressed()
        if(photoChanged == true){
            photoUri?.let{
                val fdelete = File(it.path)
                fdelete.delete()
            }
        }
    }

    override fun setEditProfileFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}