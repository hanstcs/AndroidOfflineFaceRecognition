package com.example.facerecognition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facerecognition.analyser.FrameDetectionAnalyser
import com.example.facerecognition.base.BaseActivity
import com.example.facerecognition.customview.RecognitionListener
import com.example.facerecognition.customview.gone
import com.example.facerecognition.customview.visible
import com.example.facerecognition.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    private val viewModel: CameraCaptureViewModel by viewModels()
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraSelector: CameraSelector
    private lateinit var faceDetectionAnalyser: FrameDetectionAnalyser

    override fun bindingInflater(layoutInflater: LayoutInflater) =
        ActivityCameraBinding.inflate(layoutInflater)

    override fun setupView() {
        initDataFromIntent()
        faceDetectionAnalyser = FrameDetectionAnalyser(binding.overlayView)
        if (isAllPermissionsGranted()) {
            startCamera(cameraSelector)
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        setupCameraButton()
        setupOverlayView()
    }

    private fun setupCameraButton() {
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }
        binding.btnSwitch.setOnClickListener {
            switchCamera()
        }
    }

    private fun setupOverlayView() {
        binding.overlayView.recognitionListener = object : RecognitionListener {
            override fun onFaceDetected(match: Boolean) {
                val message: String = if (match) getString(R.string.face_found)
                else getString(R.string.face_not_found)
                binding.logTextView.text = message
                Log.d(TAG, message)
            }
        }
        setOverlayAbovePreviewView()
    }

    override fun observeData() {
        viewModel.state.observe(this, { state ->
            when (state) {
                is CameraCaptureViewState.ShowLoading -> {
                    binding.progressBar.visible()
                }
                is CameraCaptureViewState.FailedSaveImage -> {
                    binding.progressBar.gone()
                    Toast.makeText(
                        baseContext,
                        "Failed to capture. ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is CameraCaptureViewState.SuccessSaveImage -> {
                    binding.progressBar.gone()
                    Toast.makeText(
                        baseContext,
                        "Success save ${state.imagePath}",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK, Intent())
                    finish()
                }
            }
        })
    }

    override fun loadData() {}

    private fun initDataFromIntent() {
        val isFrontCamera = intent?.getBooleanExtra(
            DetectionActivity.EXTRA_FRONT_CAMERA,
            true
        )
        cameraSelector = if (isFrontCamera!!) DEFAULT_FRONT_CAMERA else DEFAULT_BACK_CAMERA
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            val detectionAnalyser = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), faceDetectionAnalyser)
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    detectionAnalyser
                )
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchCamera() {
        cameraSelector =
            if (DEFAULT_BACK_CAMERA == cameraSelector) DEFAULT_FRONT_CAMERA
            else DEFAULT_BACK_CAMERA
        startCamera(cameraSelector)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val imageFileName = "IMG_" + System.currentTimeMillis()
        val imageDir = baseContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filePath = "$imageDir/$imageFileName.png"
        val photoFile = File(filePath)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModel.saveImageName(imageFileName)
                }

                override fun onError(exception: ImageCaptureException) {
                    viewModel.failedSaveImage(exception.message)
                    Log.e(TAG, exception.message, exception)
                }
            }
        )
    }

    private fun setOverlayAbovePreviewView() {
        binding.overlayView.setWillNotDraw(false)
        binding.overlayView.setZOrderOnTop(true)
    }

    private fun isAllPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val TAG = "CameraActivity"
        const val EXTRA_FRONT_CAMERA: String = "front_camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
