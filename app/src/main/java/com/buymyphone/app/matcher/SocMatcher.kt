package com.buymyphone.app.matcher

import android.content.Context
import com.buymyphone.app.model.SocScoreModel
import com.buymyphone.app.repository.AssetLoader

object SocMatcher {

    fun findBestSocMatch(context: Context, detectedSocName: String): SocScoreModel? {
        if (detectedSocName.isBlank() || detectedSocName.equals("Unknown", ignoreCase = true)) {
            return null
        }

        val socList = AssetLoader.loadSocScores(context)
        val normalizedDetected = normalize(detectedSocName)

        return socList.firstOrNull { item ->
            normalizedDetected.contains(normalize(item.name)) ||
                    normalize(item.name).contains(normalizedDetected)
        }
    }

    private fun normalize(text: String): String {
        return text
            .lowercase()
            .replace("-", " ")
            .replace("_", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
