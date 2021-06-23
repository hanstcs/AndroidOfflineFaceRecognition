package com.example.facerecognition.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class OverlayView(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    // DisplayMetrics for the current display
    private val displayMetrics = context.resources.displayMetrics

    // Width and height of the device screen in pixels.
    private val dpHeight = displayMetrics.heightPixels
    private val dpWidth = displayMetrics.widthPixels

    // Our boxes will be predicted on a 640 * 480 image. So, we need to scale the boxes to the device screen's width and
    // height
    private val xfactor = dpWidth.toFloat() / 480f
    private val yfactor = dpHeight.toFloat() / 640f

    // Create a Matrix for scaling the bbox coordinates ( for REAR camera )
    private val output2OverlayTransformRearLens = Matrix().apply {
        preScale(xfactor, yfactor)
    }

    // Create a Matrix for scaling the bbox coordinates ( for FRONT camera )
    // For the front camera, we need to have an additional postScale(), so as to avoid
    // mirror images of boxes.
    private val output2OverlayTransformFrontLens = Matrix().apply {
        preScale(xfactor, yfactor)
        postScale(-1f, 1f, dpWidth / 2f, dpHeight / 2f)
    }

    // This var is assigned in FrameAnalyser.kt
    var faceBoundingBoxes: ArrayList<Prediction>? = null

    // Defines a Paint object for the boxes.
    private val boxPaint = Paint().apply {
        color = Color.parseColor("#4D90caf9")
        style = Paint.Style.FILL
    }

    // Defines a Paint object for the text.
    private val textPaint = Paint().apply {
        strokeWidth = 2.0f
        textSize = 32f
        color = Color.WHITE
    }

    final val minScore: Double = 1.00

    // Determines which Matrix should be used for transformation.
    // See MainActivity.kt for its uses.
    var addPostScaleTransform = false

    public var recognitionListener: RecognitionListener? = null

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // NOT IMPLEMENTED
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // NOT IMPLEMENTED
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // NOT IMPLEMENTED
    }

    var detectedQty: Int = 0
    override fun onDraw(canvas: Canvas?) {
        if (faceBoundingBoxes != null) {
            if (faceBoundingBoxes!!.isEmpty()) {
                recognitionListener?.onFaceDetected(false)
                return
            }
            for (face in faceBoundingBoxes!!) {
                val processedBbox = processBBox(face.boundingBox)
                // Draw boxes and text

                var match = face.score >= minScore
                boxPaint.color = if (match) Color.parseColor("#4D00FF00")
                else Color.parseColor("#4Dff1313")
                if (face.drawBox) canvas?.drawRoundRect(processedBbox, 16f, 16f, boxPaint)
                canvas?.drawText(
                    if (match) face.label else "",
                    processedBbox.centerX(),
                    processedBbox.centerY(),
                    textPaint
                )
                detectedQty = if (match) detectedQty + 1 else 0

                Log.e("Info", "Rect received ${processedBbox.toShortString()}")
                recognitionListener?.onFaceDetected(match && detectedQty >= 5 && faceBoundingBoxes!!.size == 1)
            }
        }
    }

    // Apply the scale transform matrix to the boxes.
    private fun processBBox(bbox: Rect): RectF {
        val rectf = RectF(bbox)
        // Add suitable Matrix transform
        when (addPostScaleTransform) {
            true -> output2OverlayTransformFrontLens.mapRect(rectf)
            false -> output2OverlayTransformRearLens.mapRect(rectf)
        }

        return rectf
    }
}
