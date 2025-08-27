package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.R
import com.example.nearstore.databinding.ActivityEditAddressBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditAddressActivity : AppCompatActivity() {


    private val database = FirebaseDatabase.getInstance().getReference()
    private lateinit var binding: ActivityEditAddressBinding
    private var userlat: String? = null
    private var userlong: String? = null
    private var uid: String? = null
    private var source: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
        source = intent.getStringExtra("source")
        getUserId()
        getUserAddress()
        checkLocationPermission()

        binding.btnContinue.setOnClickListener {


            if (userlat == null || userlong == null) {
                Toast.makeText(this, "Fetching location, please wait...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendData()
            inputSharedprefData()
            navigate()


        }

    }

    private fun navigate() {

        when (source) {
            "address" -> {
                val intent = Intent(this, AddressActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }

            "cart" -> {
                val intent = Intent(this, CartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }

            "name" -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            "bottomsheet" -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            else -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        finish()
    }


    private fun inputSharedprefData() {
        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userlat", userlat)
        editor.putString("userlong", userlong)
        editor.apply()
    }


    private fun sendData() {
        database.child("users").child(uid.toString()).child("useraddress")
            .setValue(binding.tfAddress.editText?.text.toString())
        database.child("users").child(uid.toString()).child("userlat").setValue(userlat)
        database.child("users").child(uid.toString()).child("userlong").setValue(userlong)
    }


    private fun getUserId() {
        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", null).toString()
    }


    fun getUserAddress() {

        database.child("users").child(uid.toString()).child("useraddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tfAddress.editText?.setText(snapshot.getValue(String::class.java))

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            checkIfLocationIsEnabled()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkIfLocationIsEnabled()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfLocationIsEnabled() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getUserLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 2001)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001) {
            if (resultCode == RESULT_OK) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Location is required to continue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Try last known location first
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {


                    userlat = location.latitude.toString()
                    userlong = location.longitude.toString()

                } else {
                    // If last location is null, request new location
                    val locationRequest = LocationRequest.create().apply {
                        priority = Priority.PRIORITY_HIGH_ACCURACY
                        interval = 1000
                        numUpdates = 1
                    }

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val newLocation = locationResult.lastLocation
                            if (newLocation != null) {


                                userlat = newLocation.latitude.toString()
                                userlong = newLocation.longitude.toString()

                            } else {
                                Toast.makeText(
                                    this@EditAddressActivity,
                                    "Unable to get location",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}