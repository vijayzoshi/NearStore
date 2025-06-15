package com.example.nearstore.Adapter

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.R
import com.example.nearstore.StoreModal

class StoreAdapter(val context: Context, var datalist : ArrayList<StoreModal>) : RecyclerView.Adapter<StoreAdapter.MyViewHolder>() {


var onItemClick  : ((StoreModal) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_stores, parent, false)
        return MyViewHolder(view)

    }




    override fun getItemCount(): Int {
        return datalist.size
    }

    fun searchDataList(searchList: ArrayList<StoreModal>) {
        datalist = searchList
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.storename.text = datalist.get(position).storename
        holder.storelocation.text = datalist.get(position).storelocation

        val a =  datalist.get(position)
      holder.storerating.text = a.storerating.toString()


        holder.itemView.setOnClickListener {
            onItemClick?.invoke(a)
        }
       val  endLat: Double = datalist.get(position).storelat
        val  endLng: Double = datalist.get(position).storelong


        val startLat: Double = 28.704060
        val startLng: Double = 77.102493
        val distance = calculateDistanceInKm(startLat, startLng, endLat, endLng) + " " + "km"

        holder.storedistance.text = distance





    }


    class MyViewHolder(itemview : View ) : RecyclerView.ViewHolder(itemview){
        val storename = itemview.findViewById<TextView>(R.id.tv_storename)
        val storelocation = itemview.findViewById<TextView>(R.id.tv_storelocation)
        val storerating = itemview.findViewById<TextView>(R.id.tv_shoprating)
        val storedistance = itemview.findViewById<TextView>(R.id.tv_storedistance)




        // val expertpic = itemview.findViewById<ImageView>(R.id.iv_expertpic)
    }

    fun calculateDistanceInKm(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double
    ): String {
        val startLocation = Location("start").apply {
            latitude = startLat
            longitude = startLng
        }

        val endLocation = Location("end").apply {
            latitude = endLat
            longitude = endLng
        }

        val distanceInMeters = startLocation.distanceTo(endLocation)
        val distanceInKm = distanceInMeters / 1000f
        return String.format("%.1f", distanceInKm)
    }

}