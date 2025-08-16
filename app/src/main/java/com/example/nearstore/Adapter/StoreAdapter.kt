package com.example.nearstore.Adapter

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nearstore.R
import com.example.nearstore.StoreModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoreAdapter(val context: Context, var datalist : ArrayList<StoreModal>) : RecyclerView.Adapter<StoreAdapter.MyViewHolder>() {


    val database = FirebaseDatabase.getInstance().getReference()


var onItemClick  : ((StoreModal, Int) -> Unit)? = null
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

     //   val rating = datalist.get(position).storerating.toString()
       // holder.storerating.text = rating


        holder.noofrating.text = datalist.get(position).noofrating.toString()
        holder.storerating.text = datalist.get(position).storerating.toString()+ "(" + datalist.get(position).noofrating + ")"



       val  endLat: Double = datalist.get(position).storelat
        val  endLng: Double = datalist.get(position).storelong

        val sharedPref = context.getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        var uid = sharedPref.getString("userid", "haha").toString()

      //  var userlat = sharedPref.getString("userlat", "haha")?.toDouble()
      //  var userlong = sharedPref.getString("userlat", "haha")?.toDouble()


        var time =0
        database.child("users").child(uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                   val  userlat = snapshot.child("userlat").getValue(String::class.java)!!.toDouble()
                   val  userlong = snapshot.child("userlong").getValue(String::class.java)!!.toDouble()



             //       val startLat: Double = 28.5950416
                //    val startLng: Double = 77.0846924
                    val distance = calculateDistanceInKm(userlat, userlong, endLat, endLng)
                    val shortdistance = String.format("%.1f", distance)

                    holder.storedistance.text = shortdistance + " " + "km"


                    val pickingtime =distance/15*60

                     time = pickingtime.toInt() + 20

                    holder.deliverytime.text = time.toString() + " " + "min"

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })




        val imagelink = datalist.get(position).storeimage
        Glide.with(context)
            .load(imagelink)
            .into(holder.storeimage)


        holder.itemView.setOnClickListener {
            onItemClick?.invoke( datalist.get(position), time)
        }


    }


    class MyViewHolder(itemview : View ) : RecyclerView.ViewHolder(itemview){
        val storename = itemview.findViewById<TextView>(R.id.tv_storename)
        val storelocation = itemview.findViewById<TextView>(R.id.tv_storelocation)
        val storerating = itemview.findViewById<TextView>(R.id.tv_storerating)
        val noofrating = itemview.findViewById<TextView>(R.id.tv_noofrating)
        val deliverytime = itemview.findViewById<TextView>(R.id.tv_deliverytime)

        val storedistance = itemview.findViewById<TextView>(R.id.tv_storedistance)
        val storeimage = itemview.findViewById<ImageView>(R.id.iv_storeimage)
    }

    fun calculateDistanceInKm(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double
    ): Float {
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

        return distanceInKm


    }

}