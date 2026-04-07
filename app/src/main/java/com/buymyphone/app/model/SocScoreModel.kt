package com.buymyphone.app.model

data class SocScoreModel(
    val name: String,
    val cpuScore: Int,
    val gpuScore: Int,
    val cameraScore: Int,
    val batteryScore: Int,
    val dailyScore: Int
)
