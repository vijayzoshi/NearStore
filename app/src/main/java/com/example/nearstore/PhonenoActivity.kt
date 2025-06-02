package com.example.nearstore

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class PhonenoActivity : AppCompatActivity() {



    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_phoneno)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                val lat2 = 28.5950292
                val long2 = 77.0456308


                Toast.makeText(this, calculateDistanceInKm(latitude, longitude, lat2, long2), Toast.LENGTH_SHORT).show()
               //Toast.makeText(this, "Lat: $latitude, Lng: $longitude", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateDistanceInKm(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double
    ): String {
        val startLocation = Location("start").apply {
            latitude = startLat
            longitude = startLng
        }

        val endLocation = Location("end").apply {
            latitude = endLat
            longitude = endLng
        }

        val distanceInMeters = startLocation.distanceTo(endLocation)
        val distanceInKm = distanceInMeters / 1000f
        return String.format("%.1f", distanceInKm)
    }


}