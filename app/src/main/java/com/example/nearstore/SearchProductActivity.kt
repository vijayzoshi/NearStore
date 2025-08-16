package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.ProductAdapter
import com.example.nearstore.Data.ProductModal
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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


        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")


        storeSv = findViewById(R.id.sv_store)


        storeid = intent.getIntExtra("storeid", 0).toString()


        productArrayList = ArrayList<ProductModal>()
        productRecyclerView = findViewById(R.id.rv_product)
        productRecyclerView.layoutManager = GridLayoutManager(this,2 )
        productAdapter = ProductAdapter(this@SearchProductActivity, productArrayList)
        productRecyclerView.adapter = productAdapter


        databaseReference.child(storeid).child("storename").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storename  = snapshot.getValue(String::class.java)
                storeSv.queryHint = "Search in " +storename

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
                if (!newText.isNullOrEmpty()) {
                    firebasedata()
                    search(newText)
                }else{
                    productRecyclerView.visibility = View.GONE

                }


                return true
            }

        })

        /*

        val extendedfab : ExtendedFloatingActionButton = findViewById(R.id.extended_fab)
        extendedfab.hide()


        databaseReference.child("users").child(uid.toString()).child("cart")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        extendedfab.show()
                        val itemsadded =  snapshot.childrenCount.toString()+ " " + "items added"
                        extendedfab.text = itemsadded
                    }else{
                        extendedfab.hide()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        extendedfab.setOnClickListener {

            val intent: Intent = Intent(this, CartActivity::class.java)
            intent.putExtra("storeid", storeid)

            startActivity(intent)
        }
*/

    }

    fun search(word: String) {

        if (word.isEmpty()) {
            productRecyclerView.visibility = View.GONE

        }else{
            productRecyclerView.visibility  = View.VISIBLE

        }
        val searchList = ArrayList<ProductModal>()
        for (x in productArrayList) {
            if (x.productname.lowercase().contains(word.lowercase(Locale.getDefault()))
            ) {
                searchList.add(x)
            }
        }

        productAdapter.searchDataList(searchList)
/*
        if (word.isEmpty() || word.isBlank()) {
            productRecyclerView.visibility = View.GONE
        } else {
            productRecyclerView.visibility = View.VISIBLE

        }


 */




    }



    fun firebasedata() {

        databaseReference.child(storeid).child("allproduct")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productArrayList.clear()

                        for (datasnapshot in snapshot.children) {
                            val data = datasnapshot.getValue(ProductModal::class.java)
                            productArrayList.add(data!!)
                        }
                        productRecyclerView.visibility = View.VISIBLE

                        productAdapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }



}