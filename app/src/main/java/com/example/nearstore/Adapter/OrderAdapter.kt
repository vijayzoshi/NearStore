package com.example.nearstore.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nearstore.Data.OrderModal
import com.example.nearstore.UI.OrderDetailsActivity
import com.example.nearstore.R
import com.google.android.material.button.MaterialButton


class OrderAdapter(val context: Context, var dataList: ArrayList<OrderModal>) :
    RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_orders, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList.get(position)
        holder.storenameTV.text = data.storeName
        if (data.orderstatus == "delivered") {
            holder.cancelledTv.visibility = View.GONE
        } else {
            holder.completedTv.visibility = View.GONE
        }

        holder.orderdatetimeIV.text = data.ordertime
        holder.ordertotalIV.text = "₹" + data.grandtotal.toString()
        holder.storenameTV.text = data.storeName
        holder.storelocationIV.text = data.storeLocation
        val imagelink = data.storeimage
        Glide.with(context)
            .load(imagelink)
            .into(holder.storepicIV)

        holder.detailsIV.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderid", data.orderid)
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = dataList.size
    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val storenameTV: TextView = itemview.findViewById(R.id.tv_storename)
        val storelocationIV = itemview.findViewById<TextView>(R.id.tv_storelocation)
        val storepicIV = itemview.findViewById<ImageView>(R.id.iv_storepic)
        val cancelledTv = itemview.findViewById<TextView>(R.id.tv_cancelled)
        val completedTv = itemview.findViewById<TextView>(R.id.tv_delivered)
        val orderdatetimeIV = itemview.findViewById<TextView>(R.id.tv_orderdatetime)
        val ordertotalIV = itemview.findViewById<TextView>(R.id.tv_ordertotal)
        val detailsIV = itemview.findViewById<MaterialButton>(R.id.btn_details)
    }

}

