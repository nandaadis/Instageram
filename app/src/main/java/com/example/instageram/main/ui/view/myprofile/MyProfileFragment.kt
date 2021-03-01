package com.example.instageram.main.ui.view.myprofile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.SplashScreenActivity
import com.example.instageram.camerax.CameraActivity
import com.example.instageram.databinding.FragmentMyProfileBinding
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.ui.view.MainActivity
import com.example.instageram.main.ui.view.postdetail.LoveDetailFragmentDirections
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import com.example.instageram.utils.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.bottom_sheet_my_profile_add.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment(), GetMyProfileListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!


    private val myProfileSource = MyProfileSource()
    private val myProfileRepository = MyProfileRepository(myProfileSource)
    private val factory = MyProfileModelFactory(myProfileRepository)
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(MyProfileViewModel::class.java)
        viewModel.getMyProfileListener = this

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
        Log.d(Util.TAG, "My Profile onCreate View")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.disposables.dispose()
        Log.d(Util.TAG, "My Profile onDestroy View")

    }

//    override fun onResume() {
//        super.onResume()
//
//        Log.d(Util.TAG,"onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        Log.d(Util.TAG,"onPause")
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        Log.d(Util.TAG,"onStop")
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMyProfile()

        viewModel.profile().observe(viewLifecycleOwner, Observer  {
            viewModel.getPhotoProfile(it.photopath)

            Log.d(Util.TAG, "Apakah disini eerornya? Observer MyProfile${it.username}")

            binding.tvUserId.text = it.userid
            binding.tvUsername.text = it.username
            binding.tvFollower.text = it.follower.size.toString()
            binding.tvFollowing.text = it.following.size.toString()
            binding.tvPost.text = it.post.size.toString()

            if (it.title.isNotEmpty()) {
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.text = it.title
            } else {
                binding.tvTitle.visibility = View.GONE
            }

            if (it.bio.isNotEmpty()) {
                binding.tvDesc.visibility = View.VISIBLE
                binding.tvDesc.text = it.bio
            } else {
                binding.tvDesc.visibility = View.GONE
            }
        })

        binding.viewPager.adapter = MyProfileAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_grid)
        binding.tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_tag_photo)

        binding.ivAdd.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.bottom_sheet_my_profile_add, null)
            val dialog = BottomSheetDialog(activity as MainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(view)
            dialog.show()

            view.tv_add_post.setOnClickListener {
                val intent = Intent(activity, CameraActivity::class.java)
                this.startActivityForResult(intent, 21)
                dialog.dismiss()
            }

        }


        binding.btnEditprofile.setOnClickListener {
//            val action = MyProfileFragmentDirections.actionMyProfileFragmentToMyProfilEditProfilFragment(
//                viewModel.getCurrentUser()
//            )
//            Navigation.findNavController(view).navigate(R.id.action_myProfileFragment_to_myProfilEditProfilFragment)
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivMenu.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(context, binding.ivMenu)
            popupMenu.menuInflater.inflate(R.menu.logout_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(activity, SplashScreenActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
                true
            })
            popupMenu.show()
        }

        binding.tvFollower.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToFollDetailFragment(viewModel.getCurrentUser())
            view.findNavController().navigate(action)
        }

        binding.tvFollowing.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToFollDetailFragment(viewModel.getCurrentUser())
            view.findNavController().navigate(action)
        }

        binding.fixedFollower.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToFollDetailFragment(viewModel.getCurrentUser())
            view.findNavController().navigate(action)
        }

        binding.fixedFollowing.setOnClickListener {
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToFollDetailFragment(viewModel.getCurrentUser())
            view.findNavController().navigate(action)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyProfile()
        Log.d(Util.TAG, "Ini onResume di MyProfile")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                var photoUri = ""
                data?.getStringExtra("result")?.let { photoUri = it }

                val intent = Intent(activity, MakePostActivity::class.java)
                val bundle = Bundle()
                bundle.putString("photoUri", photoUri)
                intent.putExtras(bundle)
                startActivity(intent)
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
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
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
        binding?.let {
            binding.progressbar.visibility = View.GONE
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
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
}