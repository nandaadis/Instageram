package com.example.instageram.main.ui.view.postdetail

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.databinding.FragmentDetailCommentBinding
import com.example.instageram.databinding.FragmentLoveDetailBinding
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.DetailSource
import com.example.instageram.main.data.model.CaptionModel
import com.example.instageram.main.ui.viewmodel.DetailModelFactory
import com.example.instageram.main.ui.viewmodel.DetailViewModel
import com.example.instageram.utils.Util

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailCommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailCommentFragment : Fragment(), DetailListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentDetailCommentBinding? = null
    private val binding get() = _binding!!

    private val detailSource = DetailSource()
    private val detailRepository = DetailRepository(detailSource)
    private val factory = DetailModelFactory(detailRepository)
    private lateinit var viewModel: DetailViewModel

    val args: DetailCommentFragmentArgs by navArgs()
    lateinit var adapter: DetailCommentAdapter

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
        _binding = FragmentDetailCommentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPostUserComment(args.postID)
        viewModel.getPostDetailComment(args.postID)
        viewModel.getCurrentUserProfile()
        var caption : CaptionModel = CaptionModel("","")
        var PostOwnerUID = ""
        val UserUID = viewModel.getCurrentUserUID()
        var token = ""
        var username = ""

        viewModel.profile().observe(viewLifecycleOwner, Observer {
            caption = CaptionModel(username = it.username)
//            Glide
//                .with(this)
//                .circleCrop()
//                .into(binding.ivOwnerPhoto)
            binding.ivOwnerPhoto.setBackgroundColor(it.color)
            viewModel.makeDesc(caption)
            token = it.token
        })

        viewModel.post().observe(viewLifecycleOwner, Observer {
            PostOwnerUID = it.postowner
            var timestamp =
                android.text.format.DateFormat.format("dd MMM", it.timestamp.toLong()).toString()

            binding.tvOwnerDate.text = timestamp

            caption = CaptionModel(desc = it.desc)
            viewModel.makeDesc(caption)

            viewModel.getUserProfile(it.postowner)
        })

        viewModel.currentuserprofile().observe(viewLifecycleOwner, Observer {
//            Glide
//                .with(this)
//                .load(it.color)
//
//                .circleCrop()
//                .into(binding.ivPhotoProfile)
            binding.ivPhotoProfile.setBackgroundColor(it.color)
            username = it.username
        })

        viewModel.caption().observe(viewLifecycleOwner, Observer {
            //Desc
            val name = it.username
            val text = name + it.desc
            val startName = text.indexOf(name)
            val endName = startName + name.length
            val startComment = text.indexOf(it.desc)
            val endComment = text.length
            val spannableString = SpannableString(text)
            val nameClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (UserUID == PostOwnerUID) {
                        val action = DetailCommentFragmentDirections.actionDetailCommentFragmentToMyProfileFragment()
                        view.findNavController().navigate(action)
                    } else {
                        val action = DetailCommentFragmentDirections.actionDetailCommentFragmentToOtherProfileFragment(PostOwnerUID)
                        view.findNavController().navigate(action)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }
            val commentClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(
                nameClickableSpan,
                startName,
                endName,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                commentClickableSpan,
                startComment,
                endComment,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.tvOwnerDesc.text = spannableString
            binding.tvOwnerDesc.movementMethod = LinkMovementMethod.getInstance()
        })

        binding.ivClose.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.rvComment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = DetailCommentAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            viewModel.CommentListConfig(this, args.postID),
            binding.swipeRefreshLayout,
            UserUID
        )

        binding.rvComment.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        binding.tvPost.setOnClickListener {
            if (binding.etComment.text.isNotEmpty()) {
                viewModel.sendComment(args.postID, binding.etComment.text.toString(), token, username)
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                binding.etComment.setText("")
            } else {
                Toast.makeText(activity, "komentar tidak boleh kosong", Toast.LENGTH_LONG).show()
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
         * @return A new instance of fragment DetailCommentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailCommentFragment().apply {
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
        adapter.refresh()
    }

    override fun onSuccessPhotoProfile() {

    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}