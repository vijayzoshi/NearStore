package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.StoreAdapter
import com.example.nearstore.Data.StoreModal
import com.example.nearstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var storeAapter: StoreAdapter
    private lateinit var storeArrayList: ArrayList<StoreModal>
    private lateinit var storeSv: SearchView



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


        setupRecyclerview()
        setSearchView()
        setsearchquery()



    }

    private fun setSearchView() {
        storeSv = findViewById(R.id.sv_store)
        storeSv.requestFocus()
        storeSv.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(storeSv.findFocus(), InputMethodManager.SHOW_IMPLICIT)
        }, 200)
        storeSv.queryHint
    }

    private fun setsearchquery() {
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

    private fun setupRecyclerview() {
        storeArrayList = ArrayList<StoreModal>()
        storeRecyclerView = findViewById(R.id.rv_product)
        storeRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
        databaseReference = FirebaseDatabase.getInstance().getReference("stores")
        storeAapter = StoreAdapter(this@SearchActivity, storeArrayList)
        storeRecyclerView.adapter = storeAapter
    }


    fun search(word: String) {
        if (word.isEmpty()) {
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