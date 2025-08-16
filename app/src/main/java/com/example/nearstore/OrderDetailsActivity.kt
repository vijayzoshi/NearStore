package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.PlacedOrderAdapter
import com.example.nearstore.Data.Product
import com.example.nearstore.databinding.ActivityOrderDetailsBinding
import com.example.nearstore.databinding.ActivityOrderStatusBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: PlacedOrderAdapter
    val database = FirebaseDatabase.getInstance().getReference()
    private lateinit var binding: ActivityOrderDetailsBinding  // Binding class auto-generated

    lateinit var uid: String
     var orderid: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        orderid = intent.getIntExtra("orderid",0)

        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", "haha").toString()

        binding.myToolbar.title = "Order ID : " + orderid
        binding.myToolbar.setNavigationOnClickListener {
            finish()
        }
        setSupportActionBar(binding.myToolbar)
        binding.tvHelp.setOnClickListener {

            val intent = Intent(this, HelpActivity :: class.java)
            startActivity(intent)

        }

        if (orderid != null) {
            database.child("users").child(uid.toString()).child("orders").child("orderhistory").child(orderid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.tvStorename.text = snapshot.child("storeName").getValue(String::class.java).toString()
                          binding.tvItemtotal.text = "₹" +snapshot.child("itemtotal").getValue(Int::class.java).toString()
                         binding.tvDeliveryfee.text = "₹" +snapshot.child("deliveryfee").getValue(Int::class.java).toString()
                        binding.tvOrderplaced.text = "Order placed on "+snapshot.child("ordertime").getValue(String::class.java).toString()
                        binding.tvStorelocation.text = snapshot.child("storeLocation").getValue(String::class.java).toString()
                        binding.tvMyaddress.text = snapshot.child("useraddress").getValue(String::class.java).toString()
                        binding.tvGrandtotal.text = "₹" +snapshot.child("grandtotal").getValue(Int::class.java).toString()


                        if(snapshot.child("orderstatus").getValue(String::class.java).toString() == "delivered"){
                            binding.tvCancelled.visibility = View.GONE
                        }else{

                            binding.tvDelivered.visibility = View.GONE

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        recyclerView = findViewById(R.id.rv_cart1)
        adapter = PlacedOrderAdapter(this@OrderDetailsActivity, productArraylist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()
    }


    private fun fetchStudentsFromFirebase() {


        database.child("users").child(uid.toString()).child("orders").child("orderhistory").child(orderid.toString()).child("itemsList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productArraylist.clear()

                for (child in snapshot.children) {
                    val student = child.getValue(Product::class.java)
                    student?.let {
                        productArraylist.add(it)

                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}