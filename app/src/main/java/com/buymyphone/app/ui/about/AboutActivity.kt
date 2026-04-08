package com.buymyphone.app.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMailDeveloper.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:rabbihossainltd@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "BuyMyPhone App")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFacebookDeveloper.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.facebook.com/rabbihossainltd")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to open Facebook", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackHome.setOnClickListener {
            finish()
        }
    }
}
