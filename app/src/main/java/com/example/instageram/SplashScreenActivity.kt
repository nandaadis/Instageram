package com.example.instageram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.instageram.auth.ui.view.AuthActivity
import com.example.instageram.databinding.ActivitySplashScreenBinding
import com.example.instageram.databinding.FragmentLoginUsernamePhotoBinding
import com.example.instageram.dummy.DummyActivity
import com.example.instageram.main.ui.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.text.setOnClickListener {
            auth.signOut()
        }

        Glide
            .with(this)
            .load(R.drawable.logo_instageram_negative)
            .into(binding.logo)

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (auth.currentUser == null) {
//                val intent = Intent(this, AuthActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
////                Firebase.firestore.collection("user").document(auth.uid!!).get()
////                    .addOnCompleteListener { task ->
////                        if (task.isSuccessful) {
////                            if (task.result!!.exists()) {
////                                val intent = Intent(this, MainActivity::class.java)
////                                startActivity(intent)
////                                finish()
////                            }
////
////                        } else {
////                            val intent = Intent(this, AuthActivity::class.java)
////                            startActivity(intent)
////                            finish()
////                        }
////                    }
////                    .addOnFailureListener {
////                        val intent = Intent(this, AuthActivity::class.java)
////                        startActivity(intent)
////                        finish()
////                    }
//
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }, 3000)

        if (auth.currentUser == null) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        } else {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener {
                    val map = mapOf("token" to it)
                    Firebase.firestore.collection("user").document(FirebaseAuth.getInstance().uid!!)
                        .set(map, SetOptions.merge())
                        .addOnSuccessListener {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                }

        }


    }
}