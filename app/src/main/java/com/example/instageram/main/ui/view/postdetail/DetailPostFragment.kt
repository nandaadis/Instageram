package com.example.instageram.main.ui.view.postdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.example.instageram.auth.ui.view.AuthActivity
import com.example.instageram.databinding.FragmentDetailPostBinding
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.DetailSource
import com.example.instageram.main.ui.view.MainActivity
import com.example.instageram.main.ui.viewmodel.DetailModelFactory
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.pushnotification.NotifDataModel
import com.example.instageram.pushnotification.PushNotifModel
import com.example.instageram.pushnotification.RetrofitInstance
import com.example.instageram.utils.Util
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailPostFragment : Fragment(), DetailListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!

    private val detailSource = DetailSource()
    private val detailRepository = DetailRepository(detailSource)
    private val factory = DetailModelFactory(detailRepository)
    private lateinit var viewModel: DetailViewModel

    val args: DetailPostFragmentArgs by navArgs()

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
        _binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val UserUID = viewModel.getCurrentUserUID()
        var isLove = false
        var token = ""
        var username = ""

        args.postID.let {
            viewModel.getPostDetail(args.postID)
            viewModel.getPostUser(args.postID)
        }

        viewModel.profile().observe(viewLifecycleOwner, Observer {
            binding.tvUserid.text = it.userid
            token = it.token
            username = it.username
        })

        viewModel.post().observe(viewLifecycleOwner, Observer {
            binding.tvDesc.text = it.desc

            if (it.loveby.contains(UserUID)) {
                isLove = true
                binding.ivLove.visibility = View.INVISIBLE
                binding.ivLoved.visibility = View.VISIBLE
            } else {
                isLove = false
                binding.ivLove.visibility = View.VISIBLE
                binding.ivLoved.visibility = View.INVISIBLE
            }

            binding.tvLoveBy.text = "Disukai sebanyak ${it.loveby.size} manusia"
        })

        binding.ivLove.setOnClickListener {
            viewModel.sendLove(args.postID, token, username)
            isLove = true
        }

        binding.ivLoved.setOnClickListener {
            viewModel.sendUnlove(args.postID)
            isLove = true
        }

        viewModel.photoProfile().observe(viewLifecycleOwner, Observer {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)

            Glide
                .with(this)
                .load(bmp)
                .circleCrop()
                .into(binding.ivPhotoProfile)
        })

        viewModel.photo().observe(viewLifecycleOwner, Observer {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)

            Glide
                .with(this)
                .load(bmp)
                .centerCrop()
                .into(binding.ivPhoto)
        })

        val builder: Zoomy.Builder = Zoomy.Builder(activity).target(binding.ivPhoto)
        builder.register()

        binding.viewPhoto.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                if (isLove == false)
                {
                    viewModel.sendLove(args.postID, token, username)

                    val message = PushNotifModel(
                        NotifDataModel("Instageram Memanggil", "$username menyukai Postingan anda"),
                        token
                    )



                    isLove = true
                    binding.lottieLove.visibility = View.VISIBLE
                    binding.lottieLove.playAnimation()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.lottieLove.visibility = View.INVISIBLE
                    }, 800)
                }

                else {
                    viewModel.sendUnlove(args.postID)
                    isLove = false
                }
            }
        })



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailPostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    abstract class DoubleClickListener : View.OnClickListener {
        val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds

        private var lastClickTime: Long = 0
        override final fun onClick(v: View) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
                lastClickTime = 0
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View)

    }

    override fun onLoading() {
        binding.progressbar.visibility = View.VISIBLE
        binding.shimmerPhotoProfile.startShimmerAnimation()
        binding.shimmerPhoto.startShimmerAnimation()
    }

    override fun onSuccess() {
        binding.progressbar.visibility = View.GONE
    }

    override fun onSuccessPhoto() {
        binding.shimmerPhoto.visibility = View.GONE
    }

    override fun onSuccessPhotoProfile() {
        binding.shimmerPhotoProfile.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}