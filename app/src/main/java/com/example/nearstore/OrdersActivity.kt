package com.example.nearstore

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.CartAdapter
import com.example.nearstore.Adapter.OrderAdapter
import com.example.nearstore.Data.OrderModal
import com.example.nearstore.Data.Product
import com.example.nearstore.Data.ProductModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class OrdersActivity : AppCompatActivity() {



     lateinit var recyclerView: RecyclerView
     var orderArraylist = ArrayList<OrderModal>()
     lateinit var adapter: OrderAdapter
    val dbRef =  FirebaseDatabase.getInstance().getReference("users").child("UeRS0MpHslWBDZEbqnPK2eT25K72").child("orders").child("orderhistory")


     lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", "haha").toString()




        recyclerView = findViewById(R.id.rv_order)
        adapter = OrderAdapter(this@OrdersActivity,orderArraylist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()
      //  filter()
    }

    private fun fetchStudentsFromFirebase() {


        dbRef.addValueEventListener(object : ValueEventListener {
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


    fun filter() {
        val allList = ArrayList<OrderModal>()
        for (x in orderArraylist) {
            if (x.orderstatus.lowercase().contains("ongoing")
            ) {
                allList.add(x)
            }
        }

      //  OrderAdapter.filterDataList()



    }



}