package com.buymyphone.app.export

import android.content.Context
import android.net.Uri
import java.io.OutputStreamWriter

object TxtReportExporter {

    fun export(context: Context, uri: Uri, reportText: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(reportText)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
