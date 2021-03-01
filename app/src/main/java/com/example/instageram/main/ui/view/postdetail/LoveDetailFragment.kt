package com.example.instageram.main.ui.view.postdetail

import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instageram.R
import com.example.instageram.databinding.FragmentDetailPostBinding
import com.example.instageram.databinding.FragmentLoveDetailBinding
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.DetailSource
import com.example.instageram.main.data.model.CaptionModel
import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.viewmodel.DetailModelFactory
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.utils.Util
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoveDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoveDetailFragment : Fragment(), DetailListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoveDetailBinding? = null
    private val binding get() = _binding!!

    private val detailSource = DetailSource()
    private val detailRepository = DetailRepository(detailSource)
    private val factory = DetailModelFactory(detailRepository)
    private lateinit var viewModel: DetailViewModel

    val args: LoveDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel::class.java)
        viewModel.detailListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentLoveDetailBinding.inflate(inflater, container, false)
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

//        val UserUID = viewModel.getCurrentUserUID()
//        var PostOwnerUID = ""
//        var isLove = false
//        var token = ""
//        var username = ""
//        var caption : CaptionModel = CaptionModel("","")
//
//        args.postID.let {
//            viewModel.getPostDetail(args.postID)
//            viewModel.getPostUser(args.postID)
//        }

        binding.ivClose.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.rvLove.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val adapter = LoveDetailAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.LoveListConfig(this, args.postID),
            binding.swipeRefreshLayout
        )

        binding.rvLove.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoveDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoveDetailFragment().apply {
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

    override fun onSuccessPhoto() {

    }

    override fun onSuccessPhotoProfile() {

    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        Log.d(Util.TAG,"error euy $message")
    }
}