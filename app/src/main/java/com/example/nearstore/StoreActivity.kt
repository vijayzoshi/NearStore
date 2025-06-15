package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.nearstore.databinding.ActivityOrderStatusBinding
import com.example.nearstore.databinding.ActivityStoreBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoreActivity : AppCompatActivity() {


    private lateinit var searchIv : ImageView
    private lateinit var oneLv : LinearLayout
    private lateinit var secondLv : LinearLayout
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("stores")

    private lateinit var binding: ActivityStoreBinding  // Binding class auto-generated









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



        val storeid = intent.getIntExtra("storeid", 0)


//var imagelink : String = ""

        databaseReference.child(storeid.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvStorename.text = snapshot.child("storename").getValue(String::class.java)
                    binding.tvStorelocation.text = snapshot.child("storelocation").getValue(String::class.java)
                    binding.tvStoretiming.text = snapshot.child("storetiming").getValue(String::class.java)
                    binding.tvRatings.text = snapshot.child("storerating").getValue(Int::class.java).toString()
                    var   imagelink = snapshot.child("storeimage").getValue(String::class.java).toString()
                    Glide.with(getApplicationContext())
                        .load(imagelink)
                        .into(binding.ivStoreimage)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        searchIv = findViewById(R.id.tv_help)
        searchIv.setOnClickListener {
            val intent : Intent = Intent(this, SearchProductActivity::class.java)
            intent.putExtra("storeid", storeid)

            startActivity(intent)
        }





       // oneLv = findViewById(R.id.lv_one)
        binding.lvOne.setOnClickListener{
            val intent : Intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("storeid", storeid)
            intent.putExtra("categorytype", "bathbody")
            startActivity(intent)

        }

       // secondLv = findViewById(R.id.lv_two)
        binding.lvTwo.setOnClickListener{
            val intent : Intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("storeid", storeid)
            intent.putExtra("categorytype", "skincare")
            startActivity(intent)

        }



    }


    fun navigate(ll: LinearLayout, concerntype: String, storeid : Int) {
        ll.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("concerntype", concerntype)
            startActivity(intent)
        }
    }



}