package com.buymyphone.app.model

data class DeepAnalysisResult(
    val usedPhoneWarnings: List<String>,
    val buyReasons: List<String>,
    val avoidReasons: List<String>,
    val displaySuspicion: String,
    val batteryVerdict: String,
    val hardwareStarterVerdict: String,
    val sensorStarterVerdict: String,
    val finalVerdict: String
)
