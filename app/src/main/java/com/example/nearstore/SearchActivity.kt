package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.StoreAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    lateinit var username : TextView
    lateinit var databaseReference: DatabaseReference
    lateinit var storeRecyclerView: RecyclerView
 //   lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var storeAapter: StoreAdapter
    lateinit var storeArrayList: ArrayList<StoreModal>

    lateinit var storeSv: SearchView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        setSupportActionBar(toolbar)


        storeArrayList = ArrayList<StoreModal>()
        storeRecyclerView = findViewById(R.id.rv_product)
        storeRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
        databaseReference = FirebaseDatabase.getInstance().getReference("stores")
        storeAapter = StoreAdapter(this@SearchActivity, storeArrayList)
        storeRecyclerView.adapter = storeAapter



        storeSv = findViewById(R.id.sv_store)
        storeSv.queryHint
        storeSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (!newText.isNullOrEmpty()) {
                    getFirebaseData()

                    search(newText)
                }else{
                    storeRecyclerView.visibility = View.GONE

                }
                return true
            }

        })



    }



    fun search(word: String) {

        if (word.isEmpty()) {
            // If search box is empty, show no data
            storeRecyclerView.visibility = View.GONE

        }else{
            storeRecyclerView.visibility = View.VISIBLE

        }

        val searchList = ArrayList<StoreModal>()


        for (x in storeArrayList) {
            if (x.storename.lowercase().contains(word.lowercase(Locale.getDefault()))
            ) {
                searchList.add(x)
            }
        }

        storeAapter.searchDataList(searchList)

/*
        if (storeSv.query.isEmpty()) {
            storeRecyclerView.visibility = View.GONE
        } else {
            storeRecyclerView.visibility = View.VISIBLE

        }


 */

    }



    fun getFirebaseData(){

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    storeArrayList.clear()
                    for (datasnapshot in snapshot.children) {
                        val data = datasnapshot.getValue(StoreModal::class.java)
                        storeArrayList.add(data!!)
                    }


                    storeAapter.notifyDataSetChanged()

                   // shimmerFrameLayout.stopShimmer()
                    //shimmerFrameLayout.visibility= View.GONE
                    storeRecyclerView.visibility = View.VISIBLE


                    storeAapter.onItemClick = { store, time ->
                        val intent = Intent(this@SearchActivity, StoreActivity::class.java)
                        intent.putExtra("storeid", store.storeid)
                        intent.putExtra("deliverytime", time)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

}