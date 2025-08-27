package com.example.nearstore.UI

import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.R
import com.example.nearstore.databinding.ActivityNameAddressBinding

import com.google.firebase.database.FirebaseDatabase

class NameAddressActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNameAddressBinding
    private val databaseReference = FirebaseDatabase.getInstance().getReference()
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNameAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadUserId()

        binding.btnContinue.setOnClickListener {
            saveUserData()
        }

    }

    private fun saveUserData() {
        if (uid != null) {
            databaseReference.child("users").child(uid!!).child("username").setValue(binding.tfUsername.editText?.text.toString())
            databaseReference.child("users").child(uid!!).child("userphoneno").setValue(binding.tfPhonenumber.editText?.text.toString())
        }

        val intent = Intent(this, EditAddressActivity::class.java)
        intent.putExtra("source", "name")
        startActivity(intent)
        finish()
    }

    private fun loadUserId() {
        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", null)
        if (uid == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

}