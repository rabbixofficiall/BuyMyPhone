package com.buymyphone.app.ui.hardware

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityDeadPixelTestBinding

class DeadPixelTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeadPixelTestBinding
    private var colorIndex = 0

    private val colors = listOf(
        Color.BLACK,
        Color.WHITE,
        Color.RED,
        Color.GREEN,
        Color.BLUE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeadPixelTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rootDeadPixel.setOnClickListener {
            colorIndex = (colorIndex + 1) % colors.size
            binding.rootDeadPixel.setBackgroundColor(colors[colorIndex])
        }

        binding.btnBackDeadPixel.setOnClickListener {
            finish()
        }
    }
}
