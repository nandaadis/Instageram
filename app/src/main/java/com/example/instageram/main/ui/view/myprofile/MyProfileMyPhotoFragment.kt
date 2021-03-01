package com.example.instageram.main.ui.view.myprofile

import android.content.Intent
import android.icu.lang.UCharacter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instageram.auth.ui.view.AuthActivity
import com.example.instageram.databinding.FragmentMyProfileMyPhotoBinding
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_my_profile_my_photo.*
import kotlinx.android.synthetic.main.item_photo_thumbnail.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileMyPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileMyPhotoFragment : Fragment(), MyProfileListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMyProfileMyPhotoBinding? = null
    private val binding get() = _binding!!

    private val myProfileSource = MyProfileSource()
    private val myProfileRepository = MyProfileRepository(myProfileSource)
    private val factory = MyProfileModelFactory(myProfileRepository)
    private lateinit var viewModel: MyProfileViewModel
    private lateinit var adapter: FirestorePagingAdapter<PostModel, MyProfileMyPhotoAdapter.ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(MyProfileViewModel::class.java)
        viewModel.myProfileListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileMyPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPhotoThumbnail.layoutManager = GridLayoutManager(context, 3)

       adapter = MyProfileMyPhotoAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.MyProfileMyphotoConfig(this),
            binding.swipeRefreshLayout
        )

        binding.rvPhotoThumbnail.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
        Log.d(Util.TAG, "Ini onResume di TabLayout")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileMyPhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileMyPhotoFragment().apply {
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
}