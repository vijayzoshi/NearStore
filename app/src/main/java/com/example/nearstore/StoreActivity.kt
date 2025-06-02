package com.example.nearstore

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class StoreActivity : AppCompatActivity() {


    private lateinit var searchIv : ImageView
    private lateinit var oneLv : LinearLayout
    private lateinit var secondLv : LinearLayout









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_store)
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

        searchIv = findViewById(R.id.iv_search)
        searchIv.setOnClickListener {
            val intent : Intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


        val getdata = intent.getIntExtra("storeid", 0)



        oneLv = findViewById(R.id.lv_one)
        oneLv.setOnClickListener{
            val intent : Intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("storeid", getdata)
            intent.putExtra("categorytype", "bathbody")
            startActivity(intent)

        }

        secondLv = findViewById(R.id.lv_two)
        secondLv.setOnClickListener{
            val intent : Intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("storeid", getdata)
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