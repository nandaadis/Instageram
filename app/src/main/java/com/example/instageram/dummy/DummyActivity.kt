package com.example.instageram.dummy

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.instageram.R
import com.example.instageram.utils.Util
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_dummy.*
import kotlinx.android.synthetic.main.item_photo_thumbnail.view.*
import kotlinx.coroutines.*


class DummyActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var userCollectionRef: CollectionReference
    lateinit var db: FirebaseFirestore
    lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        db = FirebaseFirestore.getInstance()

        firebaseAuth = FirebaseAuth.getInstance()

        userCollectionRef = db.collection("user")

        storageRef = Firebase.storage.reference



        texttext.text = firebaseAuth.currentUser!!.uid

        btn.setOnClickListener {
//            firebaseAuth.signOut()
            coba()
        }

        logout.setOnClickListener {
            firebaseAuth.signOut()
        }

        btnbtn.setOnClickListener {
            val data = dummymodel3(
                "cekceekcek",
                "Keedwa"
            )

            userCollectionRef.document(firebaseAuth.uid!!).collection("coba").document("lol").set(
                data
            )
                .addOnSuccessListener { documentReference ->
                    Log.d(Util.TAG, "DocumentSnapshot written with ID: ${documentReference}")
                }
                .addOnFailureListener { e ->
                    Log.w(Util.TAG, "Error adding document", e)
                }
        }


    }

    fun coba() {
        val firestore =
            userCollectionRef.document(firebaseAuth.currentUser!!.uid).get()
                .addOnCompleteListener(
                    OnCompleteListener<DocumentSnapshot> { task ->
                        if (task.isSuccessful) {
                            val document = task.result?.toObject(dummymodel2::class.java)
//                        val document = task.result
                            if (document != null) {
                                texttext.text =
                                    "${task.result?.id} \n \n ${task.result?.data} \n ${document.photopath} \n ${document.username}"




                                loadimage(document.photopath.toString())
//                            Glide
//                                .with(this@DummyActivity)
//                                .load(document.photoUrl)
//                                .centerCrop()
//                                .into(photophoto)

                            } else {
                                texttext.text = "No such document"
                            }
                        } else {
                            texttext.text = "get failed with  ${task.exception}"
                        }
                    })


    }

    fun loadimage(image: String) {
        val FOUR_MEGABYTE = (4 * 1024 * 1024).toLong()
        val ONE_MEGABYTE: Long = 1024 * 1024
        val storage = Firebase.storage
        val storageRef = storage.reference
        storageRef.child(image).getBytes(FOUR_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            Glide
                .with(this@DummyActivity)
                .load(bmp)
                .into(photophoto)
        }
            .addOnFailureListener {

                Toast.makeText(this@DummyActivity, it.message, Toast.LENGTH_LONG).show()
            }

//                Glide
//                    .with(this@DummyActivity)
//                    .load(storageRef.child(image))
//                    .into(photophoto)

//            Glide
//                .with(this@DummyActivity)
//                .load(storage.getReferenceFromUrl(image.toUri()))
//                .into(photophoto)


//        val f = File(mImageDirectory + mImageName)
//        if (f.exists()) {
//            f.delete()
//        }
//
//        val MAX_IMAGE_SIZE = 1000 * 1024
//        var streamLength = MAX_IMAGE_SIZE
//        var compressQuality = 105
//        val bmpStream = ByteArrayOutputStream()
//        while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
//            try {
//                bmpStream.flush() //to avoid out of memory error
//                bmpStream.reset()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            compressQuality -= 5
//            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
//            val bmpPicByteArray: ByteArray = bmpStream.toByteArray()
//            streamLength = bmpPicByteArray.size
//            if (BuildConfig.DEBUG) {
//                Log.d("test upload", "Quality: $compressQuality")
//                Log.d("test upload", "Size: $streamLength")
//            }
//        }
//
//        val fo: FileOutputStream
//
//        try {
//            fo = FileOutputStream(f)
//            fo.write(bmpStream.toByteArray())
//            fo.flush()
//            fo.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }
}