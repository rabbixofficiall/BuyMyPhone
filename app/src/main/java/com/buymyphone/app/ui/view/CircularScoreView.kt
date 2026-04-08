package com.buymyphone.app.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularScoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var score = 0f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = Color.parseColor("#26303D")
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#00E676")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 80f
        isFakeBoldText = true
    }

    fun setScoreAnimated(target: Int) {
        val animator = ValueAnimator.ofFloat(0f, target.toFloat())
        animator.duration = 1200
        animator.addUpdateListener {
            score = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val radius = size / 2f - 30f
        val cx = width / 2f
        val cy = height / 2f

        canvas.drawCircle(cx, cy, radius, bgPaint)

        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
        canvas.drawArc(rect, -90f, (score / 100f) * 360f, false, progressPaint)

        canvas.drawText(score.toInt().toString(), cx, cy + 25f, textPaint)
    }
}
