package com.buymyphone.app.repository

import android.content.Context
import com.buymyphone.app.model.GpuScoreModel
import com.buymyphone.app.model.SocScoreModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AssetLoader {

    fun loadSocScores(context: Context): List<SocScoreModel> {
        return try {
            val json = context.assets.open("soc_scores.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<SocScoreModel>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadGpuScores(context: Context): List<GpuScoreModel> {
        return try {
            val json = context.assets.open("gpu_scores.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<GpuScoreModel>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
