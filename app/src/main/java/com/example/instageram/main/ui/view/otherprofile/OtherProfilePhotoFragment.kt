package com.example.instageram.main.ui.view.otherprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instageram.R
import com.example.instageram.databinding.FragmentOtherProfileBinding
import com.example.instageram.databinding.FragmentOtherProfilePhotoBinding
import com.example.instageram.main.data.OtherProfileRepository
import com.example.instageram.main.data.OtherProfileSource
import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.viewmodel.OtherProfileModelFactory
import com.example.instageram.main.ui.viewmodel.OtherProfileViewModel
import com.example.instageram.utils.Util
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherProfilePhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherProfilePhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentOtherProfilePhotoBinding? = null
    private val binding get() = _binding!!

    private val otherProfileSource = OtherProfileSource()
    private val otherProfileRepository = OtherProfileRepository(otherProfileSource)
    private val factory = OtherProfileModelFactory(otherProfileRepository)
    private lateinit var viewModel: OtherProfileViewModel

    private lateinit var adapter: FirestorePagingAdapter<PostModel, OtherProfilePhotoAdapter.ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProviders.of(this, factory).get(OtherProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentOtherProfilePhotoBinding.inflate(inflater, container, false)
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

        val args = arguments
        val userid = args!!.getString("UserID")

        binding.rvPhotoThumbnail.layoutManager = GridLayoutManager(context, 3)

        adapter = OtherProfilePhotoAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.OtherProfilePhotoConfig(this, userid!!),
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OtherProfilePhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtherProfilePhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}