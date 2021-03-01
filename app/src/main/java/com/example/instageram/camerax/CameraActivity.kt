package com.example.instageram.camerax

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.instageram.BuildConfig
import com.example.instageram.R
import com.example.instageram.auth.ui.view.loginUsernamePhotoFragment
import com.example.instageram.databinding.ActivityCameraBinding
import com.google.firebase.Timestamp
import id.zelory.cekrek.Cekrek
import id.zelory.cekrek.config.CanvasSize
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (allPermissionsGranted()) {
            startCamera(cameraSelect)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.ivFlipcamera.setOnClickListener {
            if (cameraSelect == "FRONT") {
                cameraSelect = "BACK"
                startCamera("BACK")
            } else if (cameraSelect == "BACK") {
                cameraSelect = "FRONT"
                startCamera("FRONT")
            }
        }

        binding.tvWithEyebrow.setOnClickListener {
            binding.tvWithoutEyebrow.typeface = Typeface.DEFAULT
            binding.tvWithEyebrow.typeface = Typeface.DEFAULT_BOLD
            binding.ivEyebrow.visibility = View.VISIBLE
        }

        binding.tvWithoutEyebrow.setOnClickListener {
            binding.tvWithoutEyebrow.typeface = Typeface.DEFAULT_BOLD
            binding.tvWithEyebrow.typeface = Typeface.DEFAULT
            binding.ivEyebrow.visibility = View.INVISIBLE
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnShutter.setOnClickListener {
            takePhoto()
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnOk.setOnClickListener { saveImage() }

        binding.btnRetake.setOnClickListener { retakePicture() }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(cameraSelect)
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return


        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    picURI = savedUri
                    reviewPicture()
                }
            })
    }

    private fun startCamera(cameraSelection: String) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewfinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select camera
            var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            if (cameraSelection == "FRONT") {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            } else if (cameraSelection == "BACK") {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 201
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private lateinit var picURI: Uri
        private var cameraSelect : String = "FRONT"
    }


    fun reviewPicture() {

        if (cameraSelect == "FRONT") {
            binding.ivReview.rotationY = 180.0F
        } else if (cameraSelect == "BACK") {
            binding.ivReview.rotationY = 0F
        }

        Glide
            .with(this@CameraActivity)
            .load(
                picURI
            )
            .centerCrop()
            .into(binding.ivReview)



        binding.viewfinder.visibility = View.INVISIBLE
        binding.btnBack.visibility = View.INVISIBLE
        binding.btnShutter.visibility = View.INVISIBLE
        binding.layoutEyebrow.visibility = View.INVISIBLE
        binding.ivFlipcamera.visibility = View.INVISIBLE

        binding.layoutPicreview.visibility = View.VISIBLE
        binding.ivReview.visibility = View.VISIBLE

    }

    fun retakePicture() {
        binding.viewfinder.visibility = View.VISIBLE
        binding.btnBack.visibility = VISIBLE
        binding.btnShutter.visibility = View.VISIBLE
        binding.layoutEyebrow.visibility = View.VISIBLE
        binding.ivFlipcamera.visibility = View.VISIBLE

        binding.layoutPicreview.visibility = View.INVISIBLE
        binding.ivReview.visibility = View.INVISIBLE

        picURI?.let{
//                val file = File(uri.path)
//                file.delete(applicationContext)
            val fdelete = File(it.path)
            fdelete.delete()
        }

    }

    fun saveImage() {
        binding.layoutPicreview.visibility = View.INVISIBLE
        val bitmap = Cekrek.toBitmap(binding.layout) {
            canvasConfig.width =
                CanvasSize.Specific(binding.layout.width)// set canvas size to 1280 px
        }

        picURI?.let{
//                val file = File(uri.path)
//                file.delete(applicationContext)
            val fdelete = File(it.path)
            fdelete.delete()
        }

        val dateNow = Calendar.getInstance().time

        val imageFileName = "PhotoProfile_$dateNow.jpg"
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()
        val file = File(cachePath, imageFileName)

//        val fileOutputStream: FileOutputStream
//        try {
//            fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 20, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        val myImageFileUri: Uri = FileProvider.getUriForFile(
//            this,
//            applicationContext.packageName + ".provider",
//            file
//        )
//
//
//        val returnIntent = Intent()
//        returnIntent.putExtra("result", myImageFileUri.toString())
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()

        //Compress image 1MB
        val MAX_IMAGE_SIZE = 500 * 1024
        var streamLength = MAX_IMAGE_SIZE
        var compressQuality = 105
        val bmpStream = ByteArrayOutputStream()
        while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
            try {
                bmpStream.flush() //to avoid out of memory error
                bmpStream.reset()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            compressQuality -= 5
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray: ByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            if (BuildConfig.DEBUG) {
                Log.d("test upload", "Quality: $compressQuality")
                Log.d("test upload", "Size: $streamLength")
            }
        }

        val fo: FileOutputStream

        try {
            fo = FileOutputStream(file)
            fo.write(bmpStream.toByteArray())
            fo.flush()
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val myImageFileUri: Uri = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            file
        )

        val returnIntent = Intent()
        returnIntent.putExtra("result", myImageFileUri.toString())
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

    }

}