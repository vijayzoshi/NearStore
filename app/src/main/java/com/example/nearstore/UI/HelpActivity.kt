package com.example.nearstore.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {



    private lateinit var binding: ActivityHelpBinding

    companion object {
        private const val PHONE_NUMBER = "+918377055197"
        private const val WHATSAPP_URL = "https://wa.me/918377055197"
        private const val WHATSAPP_INTRO = "Hello Nearstore team, I need help regarding.."
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()

        setupClickListeners()


    }

    private fun setupClickListeners() {
        binding.cdCall.setOnClickListener {
            dialPhoneNumber(PHONE_NUMBER)
        }

        binding.cdWp.setOnClickListener {
            openWhatsapp(WHATSAPP_URL, WHATSAPP_INTRO)
        }
    }



        private fun dialPhoneNumber(phoneNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }


    private fun openWhatsapp(url: String, message: String) {
        val webpage: Uri = Uri.parse("$url?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(intent)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    }














