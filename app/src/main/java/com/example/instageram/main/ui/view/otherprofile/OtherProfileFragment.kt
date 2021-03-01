package com.example.instageram.main.ui.view.otherprofile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.databinding.FragmentDetailPostBinding
import com.example.instageram.databinding.FragmentOtherProfileBinding
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.DetailSource
import com.example.instageram.main.data.OtherProfileRepository
import com.example.instageram.main.data.OtherProfileSource
import com.example.instageram.main.ui.view.myprofile.MyProfileAdapter
import com.example.instageram.main.ui.view.myprofile.MyProfileFragmentDirections
import com.example.instageram.main.ui.view.postdetail.FollDetailFragmentArgs
import com.example.instageram.main.ui.viewmodel.DetailModelFactory
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.main.ui.viewmodel.OtherProfileModelFactory
import com.example.instageram.main.ui.viewmodel.OtherProfileViewModel
import com.example.instageram.utils.Util
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherProfileFragment : Fragment(),OtherProfileListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentOtherProfileBinding? = null
    private val binding get() = _binding!!

    private val otherProfileSource = OtherProfileSource()
    private val otherProfileRepository = OtherProfileRepository(otherProfileSource)
    private val factory = OtherProfileModelFactory(otherProfileRepository)
    private lateinit var viewModel: OtherProfileViewModel

    val args: FollDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProviders.of(this, factory).get(OtherProfileViewModel::class.java)
        viewModel.otherProfileListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.disposables.dispose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val UserUID = args.UserID
        val CurrentUserUID = viewModel.getCurrentUser()
        var isFollow = false
        var token = ""
        var username = ""

        args.UserID.let {
            viewModel.getProfile(it)
        }
        viewModel.getCurrentUserProfile()

        viewModel.profileCurrentUser().observe(viewLifecycleOwner) {
            username = it.username
        }

        viewModel.profile().observe(viewLifecycleOwner, Observer {
            token = it.token
            viewModel.getPhotoProfile(it.photopath)

            Log.d(Util.TAG, "di Observer ${it.username}")

            binding.tvUserId.text = it.userid
            binding.tvUsername.text = it.username
            binding.tvFollower.text = it.follower.size.toString()
            binding.tvFollowing.text = it.following.size.toString()
            binding.tvPost.text = it.post.size.toString()

            if (it.follower.contains(CurrentUserUID)) {
                isFollow = true
            } else {
                isFollow = false
            }

            if (isFollow == true) {
                binding.btnFollow.visibility = View.INVISIBLE
                binding.btnUnfollow.visibility = View.VISIBLE
            } else {
                binding.btnFollow.visibility = View.VISIBLE
                binding.btnUnfollow.visibility = View.INVISIBLE
            }

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

        viewModel.photoProfile().observe(viewLifecycleOwner){
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)

            Glide
                .with(this)
                .load(bmp)
                .circleCrop()
                .into(binding.ivPhotoProfile)
        }

        var bundle = Bundle()
        bundle.putString("UserID", args.UserID)

        binding.viewPager.adapter = OtherProfileAdapter(childFragmentManager, bundle)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_grid)
        binding.tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_tag_photo)

        binding.btnFollow.setOnClickListener {
            viewModel.onFollow(UserUID, token, username)
            isFollow = true
//            viewModel.getProfile(args.UserID)
        }

        binding.btnUnfollow.setOnClickListener {
            viewModel.onUnFollow(UserUID)
            isFollow = false
//            viewModel.getProfile(args.UserID)
        }

        binding.tvFollower.setOnClickListener {
            val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFollDetailFragment(UserUID)
            view.findNavController().navigate(action)
        }

        binding.tvFollowing.setOnClickListener {
            val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFollDetailFragment(UserUID)
            view.findNavController().navigate(action)
        }

        binding.fixedFollower.setOnClickListener {
            val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFollDetailFragment(UserUID)
            view.findNavController().navigate(action)
        }

        binding.fixedFollowing.setOnClickListener {
            val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFollDetailFragment(UserUID)
            view.findNavController().navigate(action)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OtherProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtherProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onLoading() {
        binding.progressbar.visibility = View.VISIBLE
        binding.shimmerPhotoProfile.startShimmerAnimation()
    }

    override fun onSuccess() {
        binding.progressbar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onSuccessPhotoProfile() {
        binding.shimmerPhotoProfile.visibility = View.GONE
    }

    override fun onFollowSuccess() {
        binding.progressbar.visibility = View.GONE
        viewModel.getProfile(args.UserID)
    }

    override fun onUnFollowSuccess() {
        binding.progressbar.visibility = View.GONE
        viewModel.getProfile(args.UserID)
    }
}