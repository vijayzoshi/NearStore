package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.CartAdapter
import com.example.nearstore.Data.OrderData
import com.example.nearstore.Data.Product
import com.example.nearstore.databinding.ActivityCartBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {



    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: CartAdapter
    private val database = FirebaseDatabase.getInstance().getReference()
    private var itemtotal = 1
    private var grandtotal = 1
    private lateinit var uid: String
    private lateinit var storeid: String
    private lateinit var binding: ActivityCartBinding
    private lateinit var storename: String
    private lateinit var storepic: String
    private lateinit var storelocation: String
    private lateinit var myaddress: String
    private lateinit var ordertime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getInset()


        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()
        binding.nestedScroll.visibility = View.GONE

        binding.myToolbar.setNavigationOnClickListener {
            finish()
        }


        getuserId()

        storeid = intent.getStringExtra("storeid").toString()

        binding.tvEdit.setOnClickListener {
            val intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra("source", "cart")
            startActivity(intent)
        }

        getstoreinfo()
        getUserAddress()
        binding.tvDeliveryfee.text = "₹30"
        binding.btnConfirm.setOnClickListener {
            placeOrder()
        }

        setRecyclerView()
        fetchStudentsFromFirebase()


    }


    private fun placeOrder() {
        val timestamp = System.currentTimeMillis()
        val itemlist = productArraylist
        getCurrentTime()

        val orderid = (100000..999999).random()


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


    private fun setRecyclerView() {

        adapter = CartAdapter(this@CartActivity, productArraylist)
        binding.rvCart1.layoutManager = LinearLayoutManager(this)
        binding.rvCart1.adapter = adapter
    }


    private fun getCurrentTime() {
        val istZone = ZoneId.of("Asia/Kolkata")
        val currentISTTime = ZonedDateTime.now(istZone)
        ordertime = currentISTTime.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")).toString()
    }

    private fun getUserAddress() {
        database.child("users").child(uid).child("useraddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    binding.tvMyaddress.text = snapshot.getValue(String::class.java).toString()
                    myaddress = snapshot.getValue(String::class.java).toString()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            }

            )
    }


    private fun getstoreinfo() {
        database.child("stores").child(storeid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    storename = snapshot.child("storename").getValue(String::class.java).toString()
                    binding.myToolbar.title = storename
                    storelocation =
                        snapshot.child("storelocation").getValue(String::class.java).toString()
                    storepic = snapshot.child("storeimage").getValue(String::class.java).toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun getuserId() {

        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", null).toString()
    }

    private fun getInset() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

                        }
                    }
                    adapter.notifyDataSetChanged()
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.nestedScroll.visibility = View.VISIBLE

                    itemtotal = productArraylist.sumOf { it.productprice * it.productnumber }

                    binding.tvItemtotal.text = "₹" + itemtotal.toString()

                    val deliverfee = 30

                    grandtotal = itemtotal + deliverfee
                    binding.tvGrandtotal.text = "₹" + grandtotal.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}