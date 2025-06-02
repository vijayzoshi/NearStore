package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.StoreAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    lateinit var username : TextView
    lateinit var databaseReference: DatabaseReference
    lateinit var storeRecyclerView: RecyclerView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var storeAapter: StoreAdapter
    lateinit var storeArrayList: ArrayList<StoreModal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        /*      username = view.findViewById(R.id.textView7)
      databaseReference = FirebaseDatabase.getInstance().getReference("users")
              databaseReference.addValueEventListener(object : ValueEventListener {
                  override fun onDataChange(snapshot: DataSnapshot) {
                      username.text = snapshot.child("1").child("username").getValue<String>().toString()
                  }

                  override fun onCancelled(error: DatabaseError) {
                      TODO("Not yet implemented")
                  }
              }
              )
      */

        storeArrayList = ArrayList<StoreModal>()
        storeRecyclerView = view.findViewById(R.id.rv_store)
        storeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        databaseReference = FirebaseDatabase.getInstance().getReference("stores")
        storeAapter = StoreAdapter(requireContext(), storeArrayList)

        getFirebaseData()



        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        storeRecyclerView.visibility = View.GONE
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
        storeRecyclerView.adapter = storeAapter




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

                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility= View.GONE
                    storeRecyclerView.visibility = View.VISIBLE


                    storeAapter.onItemClick = {
                        val intent = Intent(requireContext(), StoreActivity::class.java)
                        intent.putExtra("storeid", it.storeid)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}