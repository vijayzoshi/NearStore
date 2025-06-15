package com.example.nearstore.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Data.OrderModal
import com.example.nearstore.Data.Product
import com.example.nearstore.Data.ProductModal
import com.example.nearstore.MainActivity
import com.example.nearstore.OrderDetailsActivity
import com.example.nearstore.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class OrderAdapter(val context: Context,  var dataList: ArrayList<OrderModal>) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {


   // var  databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child("1")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_orders, parent, false)
        return MyViewHolder(view)
    }

  /*  fun filterDataList(filterList: ArrayList<OrderModal>) {
        dataList = filterList
        notifyDataSetChanged()
    }


   */

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList.get(position)
        holder.storenameTV.text = data.storeName
     //   holder.storelocationIV.text = data.storelocation

        holder.orderstatusIV.text = data.orderstatus
        holder.orderdatetimeIV.text = data.ordertime
        holder.ordertotalIV.text = data.grandtotal.toString()
        holder.storenameTV.text = data.storeName
        holder.storelocationIV.text = data.storeLocation





        holder.detailsIV.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity:: class.java)
            intent.putExtra("orderid", data.orderid)
            context.startActivity(intent)
        }



    }

    override fun getItemCount() = dataList.size


    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val storenameTV: TextView = itemview.findViewById(R.id.tv_storename)
        val storelocationIV = itemview.findViewById<TextView>(R.id.tv_storelocation)
        val orderstatusIV = itemview.findViewById<TextView>(R.id.tv_orderstatus)
        val orderdatetimeIV = itemview.findViewById<TextView>(R.id.tv_orderdatetime)
        val ordertotalIV = itemview.findViewById<TextView>(R.id.tv_ordertotal)


        val detailsIV = itemview.findViewById<MaterialButton>(R.id.btn_details)




    }




}

