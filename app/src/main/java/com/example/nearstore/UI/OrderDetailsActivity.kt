package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nearstore.Adapter.PlacedOrderAdapter
import com.example.nearstore.Data.Product
import com.example.nearstore.R
import com.example.nearstore.databinding.ActivityOrderDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsActivity : AppCompatActivity() {


    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: PlacedOrderAdapter
    private val database = FirebaseDatabase.getInstance().getReference()
    private lateinit var binding: ActivityOrderDetailsBinding

    private lateinit var uid: String
    private var orderid: Int = 0

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

        binding.tvHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)

        }

        getUserId()
        getOrderId()
        setupToolbar()
        getOrderDetails()
        setupRecyclerview()
        fetchStudentsFromFirebase()

    }

    private fun setupRecyclerview() {
        adapter = PlacedOrderAdapter(this, productArraylist)
        binding.rvCart1.layoutManager = LinearLayoutManager(this)
        binding.rvCart1.adapter = adapter
    }

    private fun getOrderDetails() {
        if (orderid != null) {
            database.child("users").child(uid.toString()).child("orders").child("orderhistory")
                .child(orderid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.tvStorename.text =
                            snapshot.child("storeName").getValue(String::class.java).toString()
                        binding.tvItemtotal.text =
                            "₹${snapshot.child("itemtotal").getValue(Int::class.java).toString()}"
                        binding.tvDeliveryfee.text =
                            "₹${snapshot.child("deliveryfee").getValue(Int::class.java).toString()}"
                        binding.tvOrderplaced.text =
                            "Order placed on " + snapshot.child("ordertime").getValue(String::class.java).toString()
                        binding.tvStorelocation.text =
                            snapshot.child("storeLocation").getValue(String::class.java).toString()
                        binding.tvMyaddress.text =
                            snapshot.child("useraddress").getValue(String::class.java).toString()
                        binding.tvGrandtotal.text =
                            "₹${snapshot.child("grandtotal").getValue(Int::class.java).toString()}"

                        if (snapshot.child("orderstatus").getValue(String::class.java)
                                .toString() == "delivered"
                        ) {
                            binding.tvCancelled.visibility = View.GONE
                        } else {
                            binding.tvDelivered.visibility = View.GONE
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })


        }
    }


    private fun setupToolbar() {
        binding.myToolbar.title = "Order ID : " + orderid
        binding.myToolbar.setNavigationOnClickListener {
            finish()
        }
        setSupportActionBar(binding.myToolbar)
    }


    private fun getOrderId() {
        orderid = intent.getIntExtra("orderid", 0)
    }


    private fun getUserId() {

        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", null).toString()
    }


    private fun fetchStudentsFromFirebase() {

        database.child("users").child(uid.toString()).child("orders").child("orderhistory")
            .child(orderid.toString()).child("itemsList")
            .addValueEventListener(object : ValueEventListener {
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