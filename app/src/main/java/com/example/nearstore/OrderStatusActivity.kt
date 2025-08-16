package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.CartAdapter
import com.example.nearstore.Adapter.PlacedOrderAdapter
import com.example.nearstore.Data.Product
import com.example.nearstore.databinding.ActivityOrderStatusBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class OrderStatusActivity : AppCompatActivity() {



    private lateinit var recyclerView: RecyclerView
    private val productArraylist = ArrayList<Product>()
    private lateinit var adapter: PlacedOrderAdapter
    val database = FirebaseDatabase.getInstance().getReference()
    private lateinit var binding: ActivityOrderStatusBinding  // Binding class auto-generated

    var uid = ""
    var deliveryguyno = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
         uid = sharedPref.getString("userid", "haha").toString()

        binding.myToolbar.setNavigationOnClickListener {
            finish()
        }

        setSupportActionBar(binding.myToolbar)

        binding.tvHelp.setOnClickListener {

            val intent = Intent(this, HelpActivity :: class.java)
            startActivity(intent)

        }

        database.child("users").child(uid.toString()).child("orders").child("orderongoing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvStorename.text = snapshot.child("storeName").getValue(String::class.java).toString()
                    //  binding.tvOrderid.text = snapshot.child("orderId").getValue(String::class.java).toString()
                    //  binding.tvOrderdate.text = snapshot.child("orderDate").getValue(String::class.java).toString()
                    binding.tvTiming.text = snapshot.child("timing").getValue(String::class.java).toString()
                    binding.tvStorelocation.text = snapshot.child("storeLocation").getValue(String::class.java).toString()
                    binding.tvMyaddress.text = snapshot.child("useraddress").getValue(String::class.java).toString()
                    binding.tvDeliveryguyname.text = snapshot.child("deliveryagentname").getValue(String::class.java).toString()
                    deliveryguyno = snapshot.child("deliveryagentphone").getValue(Int::class.java).toString()
                    binding.tvGrandtotal.text = "₹" + snapshot.child("grandtotal").getValue(Int::class.java).toString()

                    binding.myToolbar.title = "Order ID : " + snapshot.child("orderid").getValue(Int::class.java).toString()


                    val deliverguyname =
                         snapshot.child("deliveryagentname").getValue(String::class.java).toString()
                    deliveryguyno =
                        snapshot.child("deliveryagentphone").getValue(Int::class.java).toString()

                    if (deliverguyname == "" && deliveryguyno == "0") {
                        binding.clDeliveryguynotalot.visibility = View.VISIBLE
                        binding.clDeliveryguyalot.visibility = View.GONE

                    } else {
                        binding.clDeliveryguynotalot.visibility = View.GONE
                        binding.clDeliveryguyalot.visibility = View.VISIBLE

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
                )

        /*
        database.child("users").child(uid.toString()).child("orders").child("orderongoing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        */

        binding.ivCall.setOnClickListener {
            dialPhoneNumber(deliveryguyno)
        }


        binding.rvItemlist.visibility = View.GONE
        binding.tvClose.visibility = View.GONE
        binding.divider.visibility = View.GONE

        binding.tvView.setOnClickListener{
            binding.divider.visibility = View.VISIBLE

            binding.rvItemlist.visibility = View.VISIBLE
            binding.tvClose.visibility = View.VISIBLE
            binding.tvView.visibility =View.GONE

        }

        binding.tvClose.setOnClickListener{
            binding.divider.visibility = View.GONE

            binding.rvItemlist.visibility = View.GONE
            binding.tvClose.visibility = View.GONE
            binding.tvView.visibility =View.VISIBLE



        }


        binding.tvCancel.setOnClickListener{



            MaterialAlertDialogBuilder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Do you wanna cancel the order?")
                .setNegativeButton("No") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.cancel()

                    /*
                    databaseRefrence.child("users").child(uid.toString()).child("sessions").child("upcomingsesions").child(ss).removeValue()
                    notifyItemChanged(position)


                    val cancelledsessiondata = SessionData("Cancelled",datalist.get(position).timestamp,expertid, datalist.get(position).sessiondate,datalist.get(position).sessiontime,  datalist.get(position).sessionmode, datalist.get(position).sessionid)
                    databaseRefrence.child("users").child(uid.toString()).child("sessions").child("previoussessions").child(datalist.get(position).sessionid.toString()).setValue(cancelledsessiondata)




                    val intent = Intent(context, CancelConfirmationActivity::class.java)
                    context.startActivity(intent)

                     */

                    /*
                    snackbar =  Snackbar.make(it, "Session Deleted", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Ok") {

                            // Responds to click on the action
                            snackbar.dismiss()

                        }
                        .show()
*/

                }
                .show()

        }


        recyclerView = findViewById(R.id.rv_itemlist)
        adapter = PlacedOrderAdapter(this@OrderStatusActivity,productArraylist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()

    }






    private fun fetchStudentsFromFirebase() {


        database.child("users").child(uid.toString()).child("orders").child("orderongoing").child("itemsList").addValueEventListener(object : ValueEventListener {
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

    fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }


    }
}