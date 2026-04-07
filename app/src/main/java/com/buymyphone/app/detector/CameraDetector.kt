package com.buymyphone.app.detector

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size

object CameraDetector {

    data class CameraInfo(
        val totalCameras: Int,
        val rearCameraCount: Int,
        val frontCameraCount: Int,
        val bestRearCameraMp: Double,
        val bestFrontCameraMp: Double,
        val hasFlash: Boolean,
        val hasOis: Boolean,
        val hasAutoFocus: Boolean,
        val hasVideoStabilization: Boolean,
        val supportsRaw: Boolean,
        val supports4k: Boolean,
        val cameraSummaryLines: List<String>
    )

    fun getCameraInfo(context: Context): CameraInfo {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val ids = cameraManager.cameraIdList

            var rearCount = 0
            var frontCount = 0
            var bestRearMp = 0.0
            var bestFrontMp = 0.0
            var hasFlash = false
            var hasOis = false
            var hasAutoFocus = false
            var hasVideoStabilization = false
            var supportsRaw = false
            var supports4k = false

            val summary = mutableListOf<String>()

            ids.forEachIndexed { index, id ->
                val ch = cameraManager.getCameraCharacteristics(id)

                val facing = ch.get(CameraCharacteristics.LENS_FACING)
                val pixelSize = ch.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
                val afModes = ch.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
                val oisModes = ch.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
                val videoModes = ch.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)
                val capabilities = ch.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                val flashAvailable = ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val configMap = ch.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                val mp = if (pixelSize != null) {
                    (pixelSize.width.toDouble() * pixelSize.height.toDouble()) / 1_000_000.0
                } else {
                    0.0
                }

                val facingText = when (facing) {
                    CameraCharacteristics.LENS_FACING_BACK -> "Rear"
                    CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
                    else -> "Unknown"
                }

                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    rearCount++
                    if (mp > bestRearMp) bestRearMp = mp
                } else if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCount++
                    if (mp > bestFrontMp) bestFrontMp = mp
                }

                if (flashAvailable) hasFlash = true

                if (afModes != null && afModes.any {
                        it == CameraCharacteristics.CONTROL_AF_MODE_AUTO ||
                                it == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE ||
                                it == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                    }) {
                    hasAutoFocus = true
                }

                if (oisModes != null && oisModes.any { it != CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF }) {
                    hasOis = true
                }

                if (videoModes != null && videoModes.any { it != CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_OFF }) {
                    hasVideoStabilization = true
                }

                if (capabilities != null && capabilities.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {
                    supportsRaw = true
                }

                val videoSizes: Array<Size>? = configMap?.getOutputSizes(android.media.MediaRecorder::class.java)
                if (videoSizes != null && videoSizes.any { it.width >= 3840 || it.height >= 2160 }) {
                    supports4k = true
                }

                summary.add(
                    "Camera ${index + 1}: $facingText | " +
                            (if (mp > 0) "${"%.2f".format(mp)} MP" else "MP Unknown") +
                            " | Flash: ${if (flashAvailable) "Yes" else "No"}"
                )
            }

            CameraInfo(
                totalCameras = ids.size,
                rearCameraCount = rearCount,
                frontCameraCount = frontCount,
                bestRearCameraMp = bestRearMp,
                bestFrontCameraMp = bestFrontMp,
                hasFlash = hasFlash,
                hasOis = hasOis,
                hasAutoFocus = hasAutoFocus,
                hasVideoStabilization = hasVideoStabilization,
                supportsRaw = supportsRaw,
                supports4k = supports4k,
                cameraSummaryLines = summary
            )
        } catch (e: Exception) {
            CameraInfo(
                totalCameras = 0,
                rearCameraCount = 0,
                frontCameraCount = 0,
                bestRearCameraMp = 0.0,
                bestFrontCameraMp = 0.0,
                hasFlash = false,
                hasOis = false,
                hasAutoFocus = false,
                hasVideoStabilization = false,
                supportsRaw = false,
                supports4k = false,
                cameraSummaryLines = listOf("Camera info unavailable")
            )
        }
    }

    private fun IntArray.contains(value: Int): Boolean {
        return any { it == value }
    }
}
