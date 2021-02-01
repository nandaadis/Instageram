package com.example.instageram.main.ui.view.myprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instageram.R
import com.example.instageram.databinding.FragmentMyProfileBinding
import com.example.instageram.databinding.FragmentMyProfileMyPhotoBinding
import com.example.instageram.dummy.dummyadapter
import com.example.instageram.dummy.dummymodel
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.MyProfileSource
import com.example.instageram.main.ui.viewmodel.MyProfileModelFactory
import com.example.instageram.main.ui.viewmodel.MyProfileViewModel
import com.example.instageram.utils.Util
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

        viewModel.getMyPhoto()


        binding.rvPhotoThumbnail.layoutManager = GridLayoutManager(context, 3)
        val adapter = MyProfileMyPhotoAdapter(listOf(), viewModel, viewLifecycleOwner, view)

        binding.rvPhotoThumbnail.adapter = adapter

        viewModel.photoList().observe(viewLifecycleOwner, Observer {
            adapter.list = it
            Log.d(Util.TAG, "haii $it")
            adapter.notifyDataSetChanged()
        })

    }

    override fun onResume() {
        super.onResume()

        viewModel.getMyPhoto()
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