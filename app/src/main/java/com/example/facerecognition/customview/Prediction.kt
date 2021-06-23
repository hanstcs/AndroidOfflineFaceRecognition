package com.example.facerecognition.customview

import android.graphics.Rect

data class Prediction(
    var boundingBox: Rect,
    var label: String = "",
    var score: Double,
    var drawBox: Boolean = true
)
