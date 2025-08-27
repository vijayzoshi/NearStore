package com.example.nearstore.UI

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.OrderAdapter
import com.example.nearstore.Data.OrderModal
import com.example.nearstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersActivity : AppCompatActivity() {



    private lateinit var recyclerView: RecyclerView
     private var orderArraylist = ArrayList<OrderModal>()
     private lateinit var adapter: OrderAdapter
     private val dbRef =  FirebaseDatabase.getInstance().getReference("users")


     lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)

        getinsets()
        getUserId()
        getrecyclerView()
        fetchStudentsFromFirebase()

    }


    private fun getinsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun getUserId() {
        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", null).toString()
    }


    private fun getrecyclerView() {
        recyclerView = findViewById(R.id.rv_order)
        adapter = OrderAdapter(this@OrdersActivity,orderArraylist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun fetchStudentsFromFirebase() {
        dbRef.child(uid).child("orders").child("orderhistory").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderArraylist.clear()
                for (child in snapshot.children) {
                    val student = child.getValue(OrderModal::class.java)
                    if (student != null  && student.orderstatus != "ongoing") {
                        orderArraylist.add(student)
                    }
                }

                orderArraylist.sortByDescending { it.timestamp }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    }



