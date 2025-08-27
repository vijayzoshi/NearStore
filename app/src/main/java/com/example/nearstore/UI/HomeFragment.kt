package com.example.nearstore.UI

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Adapter.StoreAdapter
import com.example.nearstore.Data.StoreModal
import com.example.nearstore.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var nestedScroll: NestedScrollView

    private lateinit var storeAapter: StoreAdapter
    private lateinit var storeArrayList: ArrayList<StoreModal>
    private lateinit var addressLl: LinearLayout
    private lateinit var totalbillTv: TextView
    private lateinit var arrivingTv: TextView
    private lateinit var orderstatusCl: ConstraintLayout
    private lateinit var uid : String
    private lateinit var viewBtn: Button
    private lateinit var expertEt: EditText
    private lateinit var myaddressTv: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userid", "haha").toString()

        getViews(view)

        getListeners()

        getRecylcerView()

        getUserAddress()

        getFirebaseData()

        getOrderStatus()

    //    getExtendedFab()


    }


    private fun getListeners() {
        addressLl.setOnClickListener {
            val bottomSheet = AddressBottomsheet()
            bottomSheet.show(parentFragmentManager, AddressBottomsheet.TAG)
        }

        viewBtn.setOnClickListener {
            startActivity(Intent(requireContext(), OrderStatusActivity::class.java))
        }


        expertEt.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getRecylcerView() {

        storeArrayList = ArrayList<StoreModal>()
        storeAapter = StoreAdapter(requireContext(), storeArrayList)
        storeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        storeRecyclerView.adapter = storeAapter
        storeAapter.onItemClick = { store, time ->
            val intent = Intent(requireContext(), StoreActivity::class.java)
            intent.putExtra("storeid", store.storeid)
            intent.putExtra("deliverytime", time)
            startActivity(intent)
        }

    }

    private fun getViews(view: View) {


        //views
        viewBtn = view.findViewById(R.id.btn_view)
        addressLl = view.findViewById(R.id.lv_address)
        myaddressTv = view.findViewById(R.id.tv_address)
        totalbillTv  = view.findViewById(R.id.tv_totalbill)
        arrivingTv= view.findViewById(R.id.tv_arriving)
        storeRecyclerView = view.findViewById(R.id.rv_store)
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        nestedScroll = view.findViewById(R.id.nestedScroll)
        orderstatusCl = view.findViewById(R.id.cl_orderstatus)
        expertEt = view.findViewById(R.id.et_search)

        //visibility
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
        orderstatusCl.visibility = View.GONE
        nestedScroll.visibility = View.GONE



    }


    private fun getUserAddress() {

        databaseReference.child("users").child(uid).child("useraddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val myaddress = snapshot.getValue(String::class.java).toString()
                    val shortaddress = if (myaddress.length > 20) {
                        myaddress.substring(0, 20) + "..."
                    } else {
                        myaddress
                    }
                    myaddressTv.text = shortaddress

                }
                override fun onCancelled(error: DatabaseError) {

                }
            }

            )
    }

    private fun getOrderStatus() {

        databaseReference.child("users").child(uid).child("orders").child("orderongoing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        orderstatusCl.visibility = View.VISIBLE


                        val orderStatus =snapshot.child("orderstatus").getValue(String::class.java)
                        val grandTotal = snapshot.child("grandtotal").getValue(Int::class.java).toString()

                        if ( orderStatus == "ongoing") {
                            viewBtn.text = "View"
                            arrivingTv.text = snapshot.child("timing").getValue(String::class.java)


                            totalbillTv.text = "₹$grandTotal"

                            viewBtn.setOnClickListener {

                                startActivity(Intent(requireContext(), OrderStatusActivity::class.java)

                                )
                            }

                        } else {
                            viewBtn.text = "Okay"
                            arrivingTv.text = "Order Delivered!"

                            totalbillTv.text = "₹$grandTotal"
                            viewBtn.setOnClickListener {
                                databaseReference.child("users").child(uid).child("orders").child("orderongoing").removeValue()
                                orderstatusCl.visibility = View.GONE

                            }


                        }



                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



    }


    fun getFirebaseData() {

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
                    shimmerFrameLayout.visibility = View.GONE
                    nestedScroll.visibility = View.VISIBLE




                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    /*  private fun getExtendedFab() {
           val extendedfab = view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
              extendedfab.hide()
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
      }*/

}