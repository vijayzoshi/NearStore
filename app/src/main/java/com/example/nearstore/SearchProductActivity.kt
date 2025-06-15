package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.ProductAdapter
import com.example.nearstore.Data.ProductModal
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.util.Locale

class SearchProductActivity : AppCompatActivity() {

    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("stores")
    lateinit var productRecyclerView: RecyclerView
    lateinit var productAdapter: ProductAdapter
    lateinit var productArrayList: ArrayList<ProductModal>

    lateinit var storeid: String
    lateinit var storeSv: SearchView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storeSv = findViewById(R.id.sv_store)


        storeid = intent.getIntExtra("storeid", 0).toString()


        productArrayList = ArrayList<ProductModal>()
        productRecyclerView = findViewById(R.id.rv_product)
        productRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(this@SearchProductActivity, productArrayList)
        productRecyclerView.adapter = productAdapter

/*
        val currentQuery = storeSv.query.toString()

        if (currentQuery.isEmpty() || currentQuery.isBlank()) {
            productRecyclerView.visibility = View.GONE
        } else {
            productRecyclerView.visibility = View.VISIBLE

        }


 */
        databaseReference.child(storeid).child("allproduct")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productArrayList.clear()

                        for (datasnapshot in snapshot.children) {
                            val data = datasnapshot.getValue(ProductModal::class.java)
                            productArrayList.add(data!!)
                        }
                        productAdapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        databaseReference.child(storeid).child("storename").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storename  = snapshot.getValue(String::class.java)
                storeSv.queryHint = storename

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )


        storeSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {



                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    search(newText)
                }


                return true
            }

        })




    }

    fun search(word: String) {
        val searchList = ArrayList<ProductModal>()
        for (x in productArrayList) {
            if (x.productname.lowercase().contains(word.lowercase(Locale.getDefault()))
            ) {
                searchList.add(x)
            }
        }

        productAdapter.searchDataList(searchList)
        if (word.isEmpty() || word.isBlank()) {
            productRecyclerView.visibility = View.GONE
        } else {
            productRecyclerView.visibility = View.VISIBLE

        }





    }



    fun firebasedata() {


    }



}