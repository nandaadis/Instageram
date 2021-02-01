package com.example.instageram.main.ui.view.myprofile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.instageram.camerax.CameraActivity
import com.example.instageram.databinding.FragmentMyProfilEditProfilBinding
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfilEditProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfilEditProfilFragment : Fragment(), MyProfileEditProfilListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMyProfilEditProfilBinding? = null
    private val binding get() = _binding!!

    private val myProfileSource = MyProfileSource()
    private val myProfileRepository = MyProfileRepository(myProfileSource)
    private val factory = MyProfileModelFactory(myProfileRepository)
    private lateinit var viewModel: MyProfileViewModel

    lateinit private var photoUri: Uri
    lateinit private var photoPathOld: String
    private var photoChanged: Boolean = false

//    val args: MyProfilEditProfilFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(MyProfileViewModel::class.java)
        viewModel.myProfileEditProfilListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfilEditProfilBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMyProfileEdit()

        viewModel.retreiveMyProfile().observe(viewLifecycleOwner, Observer {
            viewModel.getPhotoProfileEdit(it.photopath)
            photoPathOld = it.photopath

            binding.etName.setText(it.username)
            binding.etUserId.setText(it.userid)
            binding.etTitle.setText(it.title)
            binding.etBio.setText(it.bio)
        })

        binding.ivClose.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvChangePhotoProfile.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
            this.startActivityForResult(intent, 21)
        }

        binding.ivPhotoProfile.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
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
                    activity,
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfilEditProfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfilEditProfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
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
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun setEditProfileSuccess() {
        binding.progressbar.visibility = View.GONE
        if(photoChanged == true){
            photoUri?.let{
                val fdelete = File(it.path)
                fdelete.delete()
            }
        }
        activity?.finish()

    }

    override fun setEditProfileFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}