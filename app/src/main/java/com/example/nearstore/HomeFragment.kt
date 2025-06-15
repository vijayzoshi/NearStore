package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.StoreAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.checkerframework.checker.units.qual.A


class HomeFragment : Fragment() {

    lateinit var username : TextView
    val databaseReference = FirebaseDatabase.getInstance().getReference()
    lateinit var storeRecyclerView: RecyclerView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var storeAapter: StoreAdapter
    lateinit var storeArrayList: ArrayList<StoreModal>

    lateinit var addressLv : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")

        Toast.makeText(requireContext(),uid, Toast.LENGTH_LONG).show()

        val myaddressTv : TextView  = view.findViewById(R.id.tv_address)
        val totalbillTv : TextView  = view.findViewById(R.id.tv_totalbill)

        val arrivingTv : TextView  = view.findViewById(R.id.tv_arriving)



        if (uid != null) {
            databaseReference.child("users").child(uid).child("myaddress")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        // myaddressTv.text = snapshot.getValue(String::class.java).toString()

                        val myaddress = snapshot.getValue(String::class.java).toString()
                        val shortaddress = if (myaddress.length > 6) {
                            myaddress.substring(0, 12) + "..."
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



        addressLv = view.findViewById(R.id.lv_address)
        addressLv.setOnClickListener{

            val bottomSheet = AddressBottomsheet()
            bottomSheet.show(parentFragmentManager, AddressBottomsheet.TAG)

        }
        // Search
        val expertEt: EditText = view.findViewById(R.id.et_search)
        expertEt.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }


        storeArrayList = ArrayList<StoreModal>()
        storeRecyclerView = view.findViewById(R.id.rv_store)
        storeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        storeAapter = StoreAdapter(requireContext(), storeArrayList)

        getFirebaseData()



        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        storeRecyclerView.visibility = View.GONE
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
        storeRecyclerView.adapter = storeAapter


        val extendedfab = view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        extendedfab.hide()

        val orderstatus = view.findViewById<ConstraintLayout>(R.id.cl_orderstatus)
        orderstatus.visibility = View.GONE


        databaseReference.child("users").child(uid.toString()).child("cart")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

              if (snapshot.exists()) {
                        extendedfab.show()
                        val itemsadded =  snapshot.childrenCount.toString()+ " " + "items added"
                        extendedfab.text = itemsadded
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        extendedfab.setOnClickListener {

            startActivity(Intent(requireContext(), CartActivity::class.java))
        }





        val viewBtn = requireActivity().findViewById<Button>(R.id.btn_view)
        viewBtn.setOnClickListener {
            startActivity(Intent(requireContext(), OrderStatusActivity::class.java))
        }


        databaseReference.child("users").child(uid.toString()  ).child("orders").child("orderongoing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        orderstatus.visibility = View.VISIBLE


                        if(snapshot.child("orderstatus").getValue(String::class.java)=="ongoing") {
                            viewBtn.setText("View")

                            arrivingTv.text = snapshot.child("timing").getValue(String :: class.java)
                            totalbillTv.text = "Rs" + snapshot.child("grandtotal").getValue(Int::class.java).toString()

                            viewBtn.setOnClickListener {
                                startActivity(Intent(requireContext(), OrderStatusActivity::class.java))
                            }

                        }else{
                            viewBtn.setText("Okay")
                            arrivingTv.text = "Order Delivered!"
                            totalbillTv.text = "Rs" +snapshot.child("grandtotal").getValue(Int::class.java).toString()


                            viewBtn.setOnClickListener {
                                orderstatus.visibility = View.GONE

                            }

                        }
                    }else{
                        orderstatus.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



    }


   fun getFirebaseData(){

        databaseReference.child("stores").addValueEventListener(object : ValueEventListener {
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