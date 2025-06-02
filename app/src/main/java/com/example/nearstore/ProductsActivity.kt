package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
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
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductsActivity : AppCompatActivity() {


    private lateinit var searchIv: ImageView
    var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("stores")
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

        categorytype = intent.getStringExtra("categorytype").toString()

        storeid = intent.getIntExtra("storeid", 0).toString()


        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(categorytype)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchIv = findViewById(R.id.iv_search)
        searchIv.setOnClickListener {
            val intent: Intent = Intent(this, CartActivity::class.java)
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
            "skincare" -> {
                // Do something for apple
                getnavigationrail(
                    "Facewash",
                    "Cream",
                    "Suncreen",
                    "Serum",
                    "Bodylotion",
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash
                )
            }

            "bathbody" -> {
                // Do something for banana
                getnavigationrail(
                    "Soap",
                    "Bodywash",
                    "Handwash",
                    "Powder",
                    "Bodyscrub",
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash,
                    R.drawable.facewash
                )            }

            "cherry" -> {
                // Do something for cherry
            }

            "date" -> {
                // Do something for date
            }

            "elderberry" -> {
                // Do something for elderberry
            }

            else -> {
            }
        }

    }


    fun firebasedata(a: String) {

        databaseReference.child(storeid).child(categorytype).child(a)
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


                        productAdapter.onItemClick = {
                            val intent = Intent(this@ProductsActivity, StoreActivity::class.java)
                            intent.putExtra("storeid", it.productname)
                            startActivity(intent)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    fun getnavigationrail(
        itemone: String,
        itemtwo: String,
        itemthree: String,
        itemfour: String,
        itemfive: String,
        iconone: Int,
        icontwo: Int,
        iconthree: Int,
        iconfour: Int,
        iconfive: Int
    ) {

        // Example: Dynamic items
        val items = listOf(itemone, itemtwo, itemthree, itemfour, itemfive)
        val icons = listOf(iconone, icontwo, iconthree, iconfour, iconfive)


        // Add items to the rail dynamically
        /*      items.forEachIndexed { index, label ->
              navigationRail.menu.add(Menu.NONE, index, Menu.NONE, label).apply {
                  setIcon(icons[index])
              }
          }

     */


        // Add items to the rail dynamically
        items.forEachIndexed { index, label ->
            val item = navigationRail.menu.add(Menu.NONE, index, Menu.NONE, label)
            item.icon = ContextCompat.getDrawable(this, icons[index])

        }
        navigationRail.menu.getItem(0).isChecked = true
        firebasedata(itemone.lowercase())

        navigationRail.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {

                    firebasedata(itemone.lowercase())
                    true
                }

                1 -> {

                    firebasedata(itemtwo.lowercase())
                    true
                }

                2 -> {
                    firebasedata(itemthree.lowercase())
                    true
                }

                3 -> {

                    firebasedata(itemfour.lowercase())
                    true
                }

                4 -> {
                    firebasedata(itemfive.lowercase())
                    true
                }

                else -> false
            }
        }


    }


}