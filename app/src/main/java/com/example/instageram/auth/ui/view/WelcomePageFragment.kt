package com.example.instageram.auth.ui.view

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.instageram.R
import com.example.instageram.auth.data.AuthRepository
import com.example.instageram.auth.data.FirebaseAuthSource
import com.example.instageram.auth.ui.viewmodel.AuthViewModel
import com.example.instageram.auth.ui.viewmodel.AuthViewModelFactory
import com.example.instageram.databinding.FragmentWelcomePageBinding
import com.example.instageram.main.ui.view.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.safetynet.SafetyNet.getClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WelcomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WelcomePageFragment : Fragment(), AuthListener, CheckListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentWelcomePageBinding? = null
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
        _binding = com.example.instageram.databinding.FragmentWelcomePageBinding.inflate(inflater, container, false)
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

        binding.btnRegisterGoogle.setOnClickListener {
            val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(activity as AuthActivity, option)
            signInClient.signInIntent.also {
                startActivityForResult(it, 25)
            }
        }

        binding.tvRegisterEmail.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_welcomePageFragment_to_registerEmailFragment)
        }

        binding.tvLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_welcomePageFragment_to_loginEmailFragment)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 25) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                viewModel.registerGoogle(it)
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
         * @return A new instance of fragment WelcomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WelcomePageFragment().apply {
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
        view?.let { Navigation.findNavController(it).navigate(R.id.action_welcomePageFragment_to_loginUsernamePhotoFragment) }
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
        view?.let { Navigation.findNavController(it).navigate(R.id.action_welcomePageFragment_to_loginUsernamePhotoFragment) }
    }

}