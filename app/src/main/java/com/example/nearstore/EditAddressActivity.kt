package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.databinding.ActivityEditAddressBinding
import com.example.nearstore.databinding.ActivityNameAddressBinding
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
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


    val database = FirebaseDatabase.getInstance().getReference()
    private lateinit var binding: ActivityEditAddressBinding

lateinit  var userlat : String
    lateinit  var userlong : String




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

        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        var uid = sharedPref.getString("userid", "haha").toString()

        val source = intent.getStringExtra("source")




        database.child("users").child(uid.toString()).child("myaddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    binding.tfAddress.editText?.setText(snapshot.getValue(String::class.java))

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        checkLocationPermission()

        binding.btnContinue.setOnClickListener {
            database.child("users").child(uid.toString()).child("myaddress").setValue(binding.tfAddress.editText?.text.toString())

            database.child("users").child(uid).child("userlat").setValue(userlat)
            database.child("users").child(uid).child("userlong").setValue(userlong)

           val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("userlat", userlat)
            editor.putString("userlong", userlong)
            editor.apply()



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



            }
            finish()

        }

    }






    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                Companion.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            checkIfLocationIsEnabled()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Companion.LOCATION_PERMISSION_REQUEST_CODE) {
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
                    Toast.makeText(
                        this,
                        "Lat: ${location.latitude}, Lng: ${location.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
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



                                Toast.makeText(
                                    this@EditAddressActivity,
                                    "Lat: ${newLocation.latitude}, Lng: ${newLocation.longitude}",
                                    Toast.LENGTH_LONG
                                ).show()
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