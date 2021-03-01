package com.example.instageram.main.ui.view.search

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instageram.databinding.FragmentDetailPostBinding
import com.example.instageram.databinding.FragmentSearchBinding
import com.example.instageram.main.data.PostRepository
import com.example.instageram.main.data.PostSource
import com.example.instageram.main.ui.view.postdetail.DetailPostFragmentDirections
import com.example.instageram.main.ui.viewmodel.PostModelFactory
import com.example.instageram.main.ui.viewmodel.PostViewModel
import com.example.instageram.utils.Util
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), PostListener, NavListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val postSource = PostSource()
    private val postRepository = PostRepository(postSource)
    private val factory = PostModelFactory(postRepository)
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProviders.of(this, factory).get(PostViewModel::class.java)
        viewModel.postListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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

        binding.rvSearch.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val adapter = PostAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.SearchListConfig(this),
            binding.swipeRefreshLayout,
            this,
            activity
        )

        binding.rvSearch.adapter = adapter

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
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
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
        Log.d(Util.TAG,"error euy $message")
    }

    override fun goToMyProfile() {
        val action = SearchFragmentDirections.actionSearchFragmentToMyProfileFragment()
        view?.findNavController()?.navigate(action)
    }

    override fun goToOtherProfile(postOwnerUID: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToOtherProfileFragment(postOwnerUID)
        view?.findNavController()?.navigate(action)
    }

    override fun goToLoveDetail(postID: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToLoveDetailFragment(postID)
        view?.findNavController()?.navigate(action)
    }

    override fun goToCommentDetail(postID: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailCommentFragment(postID)
        view?.findNavController()?.navigate(action)
    }

}