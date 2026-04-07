package com.buymyphone.app.export

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.IOException

object PdfReportExporter {

    fun export(context: Context, uri: Uri, title: String, reportText: String): Boolean {
        val document = PdfDocument()

        return try {
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40
            val lineHeight = 20f

            val paint = Paint().apply {
                textSize = 12f
                isAntiAlias = true
            }

            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                isAntiAlias = true
            }

            val lines = reportText.split("\n")
            var pageNumber = 1
            var lineIndex = 0

            while (lineIndex < lines.size) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas

                var y = margin.toFloat()

                canvas.drawText(title, margin.toFloat(), y, titlePaint)
                y += 30f

                while (lineIndex < lines.size && y < pageHeight - margin) {
                    val line = lines[lineIndex]
                    canvas.drawText(line, margin.toFloat(), y, paint)
                    y += lineHeight
                    lineIndex++
                }

                document.finishPage(page)
                pageNumber++
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                document.writeTo(outputStream)
            } ?: return false

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            document.close()
        }
    }
}
