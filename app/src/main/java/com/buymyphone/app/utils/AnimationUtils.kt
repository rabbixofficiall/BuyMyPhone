package com.buymyphone.app.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import com.buymyphone.app.R

object AnimationUtils {

    fun fadeIn(context: Context, view: View) {
        val anim = loadAnimation(context, R.anim.fade_in)
        view.startAnimation(anim)
    }

    fun slideUp(context: Context, view: View) {
        val anim = loadAnimation(context, R.anim.slide_up)
        view.startAnimation(anim)
    }

    fun pulse(context: Context, view: View) {
        val anim: Animation = loadAnimation(context, R.anim.pulse)
        view.startAnimation(anim)
    }
}
