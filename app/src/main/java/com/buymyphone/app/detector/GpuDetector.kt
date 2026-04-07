package com.buymyphone.app.detector

import android.opengl.EGL14
import android.opengl.GLES20

object GpuDetector {

    data class GpuInfo(
        val renderer: String,
        val vendor: String,
        val version: String
    )

    fun getGpuInfo(): GpuInfo {
        return try {
            val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val versionArray = IntArray(2)
            EGL14.eglInitialize(display, versionArray, 0, versionArray, 1)

            val configAttribs = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_NONE
            )

            val configs = arrayOfNulls<android.opengl.EGLConfig>(1)
            val numConfig = IntArray(1)
            EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0, 1, numConfig, 0)

            val contextAttribs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )

            val context = EGL14.eglCreateContext(
                display,
                configs[0],
                EGL14.EGL_NO_CONTEXT,
                contextAttribs,
                0
            )

            val surfaceAttribs = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )

            val surface = EGL14.eglCreatePbufferSurface(
                display,
                configs[0],
                surfaceAttribs,
                0
            )

            EGL14.eglMakeCurrent(display, surface, surface, context)

            val renderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: "Unknown"
            val vendor = GLES20.glGetString(GLES20.GL_VENDOR) ?: "Unknown"
            val version = GLES20.glGetString(GLES20.GL_VERSION) ?: "Unknown"

            EGL14.eglMakeCurrent(
                display,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)

            GpuInfo(
                renderer = renderer,
                vendor = vendor,
                version = version
            )
        } catch (e: Exception) {
            GpuInfo(
                renderer = "Unknown",
                vendor = "Unknown",
                version = "Unknown"
            )
        }
    }
}
