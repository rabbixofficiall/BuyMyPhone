package com.buymyphone.app.scoring

import com.buymyphone.app.model.CameraPreset
import com.buymyphone.app.model.DisplayPreset
import com.buymyphone.app.model.StoragePreset

object PresetRuleEngine {

    fun getDisplayPreset(width: Int, height: Int, refreshRate: Float): DisplayPreset {
        return when {
            (width >= 1440 || height >= 3200) && refreshRate >= 120f ->
                DisplayPreset("QHD+ 120Hz premium class", 92)

            (width >= 1080 || height >= 2400) && refreshRate >= 144f ->
                DisplayPreset("FHD+ 144Hz gaming class", 90)

            (width >= 1080 || height >= 2400) && refreshRate >= 120f ->
                DisplayPreset("FHD+ 120Hz premium class", 84)

            (width >= 1080 || height >= 2400) && refreshRate >= 90f ->
                DisplayPreset("FHD+ 90Hz good class", 76)

            (width >= 1080 || height >= 2400) ->
                DisplayPreset("FHD+ 60Hz good class", 70)

            (width >= 720 || height >= 1600) && refreshRate >= 90f ->
                DisplayPreset("HD+ 90Hz basic smooth class", 62)

            (width >= 720 || height >= 1600) ->
                DisplayPreset("HD+ standard class", 54)

            else -> DisplayPreset("Basic display class", 42)
        }
    }

    fun getStoragePreset(totalStorageGb: Double, socName: String): StoragePreset {
        return when {
            socName.contains("Snapdragon 8", true) || socName.contains("Dimensity 9", true) ->
                StoragePreset("Estimated UFS 4.0 / 3.1 flagship class", 92)

            socName.contains("Snapdragon 7", true) || socName.contains("Dimensity 8", true) ->
                StoragePreset("Estimated UFS 3.1 / 2.2 upper mid class", 82)

            totalStorageGb >= 512 ->
                StoragePreset("Estimated UFS 3.x high capacity class", 88)

            totalStorageGb >= 256 ->
                StoragePreset("Estimated UFS 2.2 / 3.x class", 78)

            totalStorageGb >= 128 ->
                StoragePreset("Estimated UFS 2.1 / 2.2 class", 68)

            totalStorageGb >= 64 ->
                StoragePreset("Estimated entry UFS / fast eMMC class", 56)

            else -> StoragePreset("Estimated eMMC basic class", 42)
        }
    }

    fun getCameraPreset(
        rearMp: Double,
        hasOis: Boolean,
        hasAutoFocus: Boolean,
        hasVideoStabilization: Boolean,
        supports4k: Boolean
    ): CameraPreset {
        var score = when {
            rearMp >= 200 -> 88
            rearMp >= 108 -> 82
            rearMp >= 64 -> 74
            rearMp >= 50 -> 68
            rearMp >= 48 -> 64
            rearMp >= 16 -> 54
            rearMp > 0 -> 42
            else -> 20
        }

        if (hasOis) score += 8
        if (hasAutoFocus) score += 4
        if (hasVideoStabilization) score += 4
        if (supports4k) score += 4

        score = score.coerceAtMost(100)

        val label = when {
            score >= 88 -> "High camera tier"
            score >= 76 -> "Upper mid camera tier"
            score >= 62 -> "Mid camera tier"
            score >= 45 -> "Basic good camera tier"
            else -> "Entry camera tier"
        }

        return CameraPreset(label, score)
    }
}
