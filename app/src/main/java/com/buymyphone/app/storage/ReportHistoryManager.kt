package com.buymyphone.app.storage

import android.content.Context
import com.buymyphone.app.model.SavedReport
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ReportHistoryManager {

    private const val PREFS_NAME = "report_history_prefs"
    private const val KEY_REPORTS = "saved_reports"
    private const val MAX_REPORTS = 30

    fun saveReport(context: Context, title: String, content: String) {
        val reports = getReports(context).toMutableList()
        reports.add(
            0,
            SavedReport(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )
        )

        val trimmed = reports.take(MAX_REPORTS)
        val json = Gson().toJson(trimmed)

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_REPORTS, json)
            .apply()
    }

    fun getReports(context: Context): List<SavedReport> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_REPORTS, null) ?: return emptyList()

        val type = object : TypeToken<List<SavedReport>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}
