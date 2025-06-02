package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.CartAdapter
import com.example.nearstore.Data.Product
import com.example.nearstore.Data.ProductData
import com.example.nearstore.Data.ProductModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {



    private lateinit var recyclerView: RecyclerView
    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btn : Button = findViewById(R.id.payButton)
        btn.setOnClickListener {

            val intent : Intent = Intent(this, OrderStatusActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.rv_cart1)
        adapter = CartAdapter(productArraylist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()




    }


    private fun fetchStudentsFromFirebase() {
        val dbRef =  FirebaseDatabase.getInstance().getReference("users").child("1").child("cart")


        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productArraylist.clear()
                for (child in snapshot.children) {
                    val student = child.getValue(Product::class.java)
                    student?.let { productArraylist.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}