package com.buymyphone.app.ui.hardware

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityTouchscreenTestBinding

class TouchscreenTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTouchscreenTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouchscreenTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rootTouchTest.setOnTouchListener { _, _ ->
            binding.txtTouchGuide.text = "Touch detected. Move across all areas of the display to manually verify the panel."
            false
        }

        binding.btnBackTouchTest.setOnClickListener {
            finish()
        }
    }
}
