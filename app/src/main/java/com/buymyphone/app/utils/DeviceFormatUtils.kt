package com.buymyphone.app.utils

import java.util.Locale

object DeviceFormatUtils {

    fun formatDouble(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }
}
