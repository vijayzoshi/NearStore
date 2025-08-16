package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.databinding.ActivityNameAddressBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import com.google.firebase.database.FirebaseDatabase

class NameAddressActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNameAddressBinding
    val databaseReference = FirebaseDatabase.getInstance().getReference()

  /*  private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST = 100
    private val GPS_REQUEST_CODE = 101

   */

    lateinit var uid : String

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


        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid","haha").toString()

   //    uid = "UeRS0MpHslWBDZEbqnPK2eT25K72"
    //   fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
     //  checkLocationPermissionAndProceed()




        binding.btnContinue.setOnClickListener {

            if (uid != null) {
                databaseReference.child("users").child(uid).child("username").setValue(binding.tfUsername.editText?.text.toString())
             //   databaseReference.child("users").child(uid).child("myaddress").setValue(binding.tfAddress.editText?.text.toString())
                databaseReference.child("users").child(uid).child("userphoneno").setValue(binding.tfPhonenumber.editText?.text.toString())

            }

      //   getLastLocation()
            val intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("source", "name")

            startActivity(intent)

            finish()



        }





    }
/*

    private fun checkLocationPermissionAndProceed() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            checkGPSEnabled()
        }
    }
    private fun checkGPSEnabled() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getLastLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, GPS_REQUEST_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this, "Failed to prompt GPS settings", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "GPS is required to use this feature", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    if (uid != null) {
                        databaseReference.child("users").child(uid).child("lat").setValue(latitude.toString())
                    }

                  //  binding.address.text = "Lat: $latitude, Lng: $longitude"
                    Toast.makeText(this, "Lat: $latitude, Lng: $longitude", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkGPSEnabled()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_REQUEST_CODE) {
            checkLocationPermissionAndProceed()
        }
    }



 */

}