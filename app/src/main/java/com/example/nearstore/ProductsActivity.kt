package com.example.nearstore

import android.app.appsearch.SearchResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
import java.util.Locale

class ProductsActivity : AppCompatActivity() {


    private lateinit var searchIv: ImageView
    var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    lateinit var productRecyclerView: RecyclerView
    lateinit var productAdapter: ProductAdapter
    lateinit var productArrayList: ArrayList<ProductModal>

    lateinit var categorytype: String

    lateinit var storeid: String

    lateinit var navigationRail: NavigationRailView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_products)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")


        categorytype = intent.getStringExtra("categorytype").toString()
        storeid = intent.getIntExtra("storeid", 0).toString()


        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(categorytype)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchIv = findViewById(R.id.tv_help)
        searchIv.setOnClickListener {
            val intent: Intent = Intent(this, SearchProductActivity::class.java)
            intent.putExtra("storeid", storeid)

            startActivity(intent)
        }








        productArrayList = ArrayList<ProductModal>()
        productRecyclerView = findViewById(R.id.rv_store)
        productRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(this@ProductsActivity, productArrayList)
        productRecyclerView.adapter = productAdapter

        //  shimmerFrameLayout = findViewById(R.id.shimmerLayout)
        productRecyclerView.visibility = View.GONE
        //     shimmerFrameLayout.startShimmer()
        //      shimmerFrameLayout.visibility = View.VISIBLE


        navigationRail = findViewById(R.id.navigationRail)
        // Clear existing (optional safety)

        navigationRail.menu.clear()



        when (categorytype) {
            "Skincare" -> {
                // Do something for apple
                getnavigationrail("Facewash", "Cream", "Suncreen",  "Bodylotion", R.drawable.facewashtype, R.drawable.creamstype, R.drawable.suncreentype, R.drawable.bodylotiontype
                )
            }



            "Aata, Rice & Dal" -> {
                getnavigationrail("Aata", "Rice", "Dal", "Besan & Soji", R.drawable.riceee, R.drawable.ricetype, R.drawable.daltype, R.drawable.besantype
                )
            }

            "Dairy & Breads" -> {
            getnavigationrail("Breads", "Milk & Lassi", "Eggs & Curd", "Paneer", R.drawable.breadtype, R.drawable.milktype, R.drawable.eggstype, R.drawable.paneertype
            )
        }

            else -> {
            }
        }


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

            }




    fun firebasedata(a: String) {

        databaseReference.child("stores").child(storeid).child(categorytype.lowercase().replace(" ", "")).child(a)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productArrayList.clear()

                        for (datasnapshot in snapshot.children) {
                            val data = datasnapshot.getValue(ProductModal::class.java)
                            productArrayList.add(data!!)
                        }
                        productAdapter.notifyDataSetChanged()


                        // shimmerFrameLayout.stopShimmer()
                        //    shimmerFrameLayout.visibility= View.GONE
                        productRecyclerView.visibility = View.VISIBLE


                        /*productAdapter.onItemClick = {
                            val intent = Intent(this@ProductsActivity, StoreActivity::class.java)
                            intent.putExtra("storeid", it.productname)
                            startActivity(intent)
                        }

                         */
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    fun getnavigationrail(itemone: String, itemtwo: String, itemthree: String, itemfour: String, iconone: Int, icontwo: Int, iconthree: Int, iconfour: Int,
    ) {

        val items = listOf(itemone, itemtwo, itemthree, itemfour)
        val icons = listOf(iconone, icontwo, iconthree, iconfour)




        // Add items to the rail dynamically
        items.forEachIndexed { index, label ->
            val item = navigationRail.menu.add(Menu.NONE, index, Menu.NONE, label)
            item.icon = ContextCompat.getDrawable(this, icons[index])

        }
        navigationRail.itemIconTintList = null

        navigationRail.menu.getItem(0).isChecked = true

        firebasedata(itemone.lowercase().replace(" ", ""))

        navigationRail.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {

                    firebasedata(itemone.lowercase().replace(" ", ""))
                    true
                }

                1 -> {

                    firebasedata(itemtwo.lowercase().replace(" ", ""))
                    true
                }

                2 -> {
                    firebasedata(itemthree.lowercase().replace(" ", ""))
                    true
                }

                3 -> {

                    firebasedata(itemfour.lowercase().replace(" ", ""))
                    true
                }


                else -> false
            }
        }


    }


}


