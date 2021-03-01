package com.example.instageram.auth.ui.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.instageram.main.ui.view.MainActivity
import com.example.instageram.R
import com.example.instageram.auth.data.AuthRepository
import com.example.instageram.auth.data.FirebaseAuthSource
import com.example.instageram.auth.ui.viewmodel.AuthViewModel
import com.example.instageram.auth.ui.viewmodel.AuthViewModelFactory
import com.example.instageram.camerax.CameraActivity
import com.example.instageram.databinding.FragmentLoginUsernamePhotoBinding
import com.example.instageram.utils.Util
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [loginUsernamePhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginUsernamePhotoFragment : Fragment(), AuthListener, CheckListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginUsernamePhotoBinding? = null
    private val binding get() = _binding!!

    private val FirebaseAuthSource = FirebaseAuthSource()
    private val AuthRepository =  AuthRepository(FirebaseAuthSource)
    private val factory =  AuthViewModelFactory(AuthRepository)

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.authListener = this
        viewModel.checkListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginUsernamePhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkUserId()

        binding.btnTakepic.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
            this.startActivityForResult(intent, 21)
        }

        binding.btnNext.setOnClickListener {
            if (statPhoto == true && binding.etUserid.text.toString().isNotEmpty()) {
                viewModel.uploadImageUser(picUri , binding.etUserid.text.toString())
            } else {
                Toast.makeText(activity, "Di isi semua ya gaes tolong", Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                binding.btnTakepic.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_outline_camera_alt_24,
                    0,
                    R.drawable.ic_outline_check_circle_24_green,
                    0
                )
                data?.getStringExtra("result")?.let { picUri = it.toUri() }
                statPhoto = true
            }
        }
    }

    companion object {

        private lateinit var picUri : Uri
        private var statPhoto : Boolean = false
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment loginUsernamePhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            loginUsernamePhotoFragment().apply {
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
        picUri?.let{
            val fdelete = File(it.path)
            fdelete.delete()
        }
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun UserYes() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun UserNo() {
        //Nothing to do, all iz well
    }
}