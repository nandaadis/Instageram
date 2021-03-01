package com.example.instageram.main.ui.view.postdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instageram.R
import com.example.instageram.databinding.FragmentFollowerDetailBinding
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.DetailSource
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
 * Use the [FollowerDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowerDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentFollowerDetailBinding? = null
    private val binding get() = _binding!!

    private val detailSource = DetailSource()
    private val detailRepository = DetailRepository(detailSource)
    private val factory = DetailModelFactory(detailRepository)
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.disposables = CompositeDisposable()
        _binding = FragmentFollowerDetailBinding.inflate(inflater, container, false)
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

        binding.rvFollower.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val adapter = FollowerDetailAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.FollowerListConfig(this, userid!!),
            binding.swipeRefreshLayout
        )

        binding.rvFollower.adapter = adapter

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
         * @return A new instance of fragment FollowerDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowerDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}