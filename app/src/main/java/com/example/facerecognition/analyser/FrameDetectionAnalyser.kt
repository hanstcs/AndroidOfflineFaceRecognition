package com.example.facerecognition.analyser

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.facerecognition.customview.OverlayView
import com.example.facerecognition.customview.Prediction
import com.example.facerecognition.env.ImageUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class FrameDetectionAnalyser(
    private var boundingBoxOverlay: OverlayView,
) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()
    private val detector = FaceDetection.getClient(realTimeOpts)

    private var isProcessing = AtomicBoolean(false)

    override fun analyze(image: ImageProxy) {
        val bitmap = ImageUtils.toBitmap(image)

        if (isProcessing.get()) {
            return
        } else {
            // Declare that the current frame is being processed.
            isProcessing.set(true)
            // Perform face detection
            val inputImage = InputImage.fromByteArray(
                ImageUtils.bitmapToNv21(bitmap),
                640,
                480,
                image.imageInfo.rotationDegrees,
                InputImage.IMAGE_FORMAT_NV21
            )
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    CoroutineScope(Dispatchers.IO).launch {
                        runModel(faces)
                    }
                }
                .addOnFailureListener { e ->
                    e.message?.let { Log.e("Model", it) }
                }
        }
        image.close()
    }

    private suspend fun runModel(faces: List<Face>) {
        withContext(Dispatchers.Default) {
            val predictions = ArrayList<Prediction>()
            for (face in faces) {
                try {
                    predictions.add(
                        Prediction(boundingBox = face.boundingBox, score = 1.1, drawBox = false)
                    )
                } catch (e: Exception) {
                    Log.e("Model", "Exception in FrameAnalyser : ${e.message}")
                    continue
                }
            }
            withContext(Dispatchers.Main) {
                boundingBoxOverlay.faceBoundingBoxes = predictions
                boundingBoxOverlay.invalidate()

                isProcessing.set(false)
            }
        }
    }
}
