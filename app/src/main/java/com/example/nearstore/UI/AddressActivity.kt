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
import com.example.nearstore.databinding.ActivityAddressBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddressBinding
    private val database = FirebaseDatabase.getInstance().getReference("users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        val uid = getUserId()


        if (!uid.isNullOrEmpty()) {
            loadUserAddress(uid)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }


        setupEditClick()


    }


    private fun setupEditClick() {
        binding.tvEdit.setOnClickListener {
            val intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("source", "address")
            startActivity(intent)
        }
    }

    private fun loadUserAddress(uid: String) {
        database.child(uid).child("useraddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvAddress.text = snapshot.getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AddressActivity,
                        "Failed to load address",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getUserId(): String? {
        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        return sharedPref.getString("userid", null)

    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}