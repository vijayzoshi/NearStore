package com.example.nearstore.UI

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.nearstore.R
import com.example.nearstore.databinding.ActivityStoreBinding

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoreActivity : AppCompatActivity() {


    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("stores")
    private lateinit var binding: ActivityStoreBinding
    private var storeid = 0


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
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        storeid = intent.getIntExtra("storeid", 0)
        val deliverytime = intent.getIntExtra("deliverytime", 0)
        binding.tvDeliverytime.text = deliverytime.toString() + " min"

        getStoreDetails()


        binding.ivSearch.setOnClickListener {
            val intent: Intent = Intent(this, SearchProductActivity::class.java)
            intent.putExtra("storeid", storeid)
            startActivity(intent)
        }
        navigate(binding.lvBpctwo, "Skincare", storeid)
        navigate(binding.lvGroceryone, "Aata, Rice & Dal", storeid)
        navigate(binding.lvGroceryeight, "Dairy & Breads", storeid)

    }

    private fun getStoreDetails() {
        databaseReference.child(storeid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvStorename.text =
                        snapshot.child("storename").getValue(String::class.java)
                    binding.tvStorelocation.text =
                        snapshot.child("storelocation").getValue(String::class.java)
                    binding.tvStoretiming.text =
                        snapshot.child("storetiming").getValue(String::class.java)
                    binding.tvRatings.text =
                        snapshot.child("storerating").getValue(Double::class.java)
                            .toString() + "(" + snapshot.child("noofrating")
                            .getValue(String::class.java) + ")"
                    var imagelink =
                        snapshot.child("storeimage").getValue(String::class.java).toString()
                    Glide.with(getApplicationContext())
                        .load(imagelink)
                        .into(binding.ivStoreimage)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }


    fun navigate(ll: LinearLayout, categorytype: String, storeid: Int) {
        ll.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("categorytype", categorytype)
            intent.putExtra("storeid", storeid)
            startActivity(intent)
        }
    }

}