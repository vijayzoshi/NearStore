package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.CartAdapter
import com.example.nearstore.Data.OrderData
import com.example.nearstore.Data.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: CartAdapter
    val database = FirebaseDatabase.getInstance().getReference()
    var itemtotal = 1
    var grandtotal = 1

    lateinit var itemtotalTv: TextView
    lateinit var grandtotalTv : TextView

    var uid = ""

    lateinit var storeid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val myToolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        myToolbar.title = "Session ID"
        myToolbar.setNavigationOnClickListener {
            finish()
        }
        setSupportActionBar(myToolbar)


        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", "haha").toString()
        var storename = ""
        var storepic = ""
        var storelocation = ""
        var myaddress = ""
        val myaddressTv: TextView = findViewById(R.id.tv_myaddress)

        itemtotalTv = findViewById(R.id.tv_itemtotal)
        grandtotalTv = findViewById(R.id.tv_grandtotal)


        storeid = intent.getStringExtra("storeid").toString()


        val editTv: TextView = findViewById(R.id.tv_edit)
        editTv.setOnClickListener {

            val intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("cart", "address")

            startActivity(intent)

           // val bottomSheet = AddressBottomsheet()
           // bottomSheet.show(supportFragmentManager, AddressBottomsheet.TAG)

        }



                var deliveryfeeTv : TextView = findViewById(R.id.tv_deliveryfee)
                deliveryfeeTv.text = "₹"+"30"











        database.child("stores").child(storeid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    storename = snapshot.child("storename").getValue(String::class.java).toString()
                    myToolbar.title = storename
                    storelocation = snapshot.child("storelocation").getValue(String::class.java).toString()
                    storepic = snapshot.child("storeimage").getValue(String::class.java).toString()


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        if (uid != null) {
            database.child("users").child(uid).child("myaddress")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        // myaddressTv.text = snapshot.getValue(String::class.java).toString()

                        myaddress = snapshot.getValue(String::class.java).toString()
                        val shortaddress = if (myaddress.length > 20) {
                            myaddress.substring(0, 20) + "..."
                        } else {
                            myaddress
                        }
                        myaddressTv.text = shortaddress

                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")

                    }
                }

                )
        }


        val itemlist = productArraylist


        val orderid = (100000..999999).random()


        val istZone = ZoneId.of("Asia/Kolkata")
        val currentISTTime = ZonedDateTime.now(istZone)
        val ordertime = currentISTTime.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")).toString()


        val timestamp = System.currentTimeMillis()



        val confirmBtn: Button = findViewById(R.id.btn_confirm)
        confirmBtn.setOnClickListener {


            val deliveryfee = 30
            val order = OrderData(
                storeName = storename,
                storeLocation = storelocation,
                orderid = orderid,
                storeimage = storepic,
                ordertime = ordertime,
                itemtotal = itemtotal,
                grandtotal = grandtotal,
                deliveryfee = deliveryfee,
                useraddress = myaddress,
                itemsList = itemlist,
                timestamp = timestamp,

            )

            database.child("users").child(uid.toString()).child("orders").child("orderongoing")
                .setValue(order)
            database.child("users").child(uid.toString()).child("orders").child("orderhistory")
                .child(orderid.toString()).setValue(order)

            val intent: Intent = Intent(this, OrderPlacedActivity::class.java)
            startActivity(intent)
            finish()

        }

        recyclerView = findViewById(R.id.rv_cart1)
        adapter = CartAdapter(this@CartActivity, productArraylist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()
        //itemtotal = productArraylist.sumOf { it.productprice }

        //   itemtotalTv.text = itemtotal.toString()


    }


    private fun fetchStudentsFromFirebase() {


        database.child("users").child(uid.toString()).child("cart")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productArraylist.clear()

                    for (child in snapshot.children) {
                        val student = child.getValue(Product::class.java)
                        student?.let {
                            productArraylist.add(it)

                            //itemtotal += it.productprice


                        }
                    }
                    adapter.notifyDataSetChanged()

                    itemtotal = productArraylist.sumOf { it.productprice*it.productnumber }

                    itemtotalTv.text ="₹"+ itemtotal.toString()

                    val deliverfee = 30

                     grandtotal = itemtotal + deliverfee
                    grandtotalTv.text = "₹"+ grandtotal.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}