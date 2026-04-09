package com.buymyphone.app.ui.hardware

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivitySpeakerTestBinding

class SpeakerTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeakerTestBinding
    private var toneGenerator: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeakerTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        binding.btnPlaySpeakerTone.setOnClickListener {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1500)
            binding.txtSpeakerStatus.text = "Playing tone. If you hear sound, speaker is working."
        }

        binding.btnStopSpeakerTone.setOnClickListener {
            toneGenerator?.stopTone()
            binding.txtSpeakerStatus.text = "Tone stopped."
        }

        binding.btnBackSpeakerTest.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null
    }
}
