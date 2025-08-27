package com.example.nearstore.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nearstore.Data.Product
import com.example.nearstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class CartAdapter(val context: Context, private val dataList: List<Product>) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    val sharedPref = context.getSharedPreferences("userdetails", Context.MODE_PRIVATE)
    val uid = sharedPref.getString("userid", "haha")
    var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users").child(uid.toString())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList.get(position)

        holder.productnameTV.text = data.productname

        val imagelink = data.productpic
        Glide.with(context)
            .load(imagelink)
            .into(holder.productimageIv)

        holder.productnumberIV.text = data.productnumber.toString()

        val finalprice = data.productprice * data.productnumber
        holder.productpriceIV.text = "₹" + finalprice.toString()
        holder.productquantityIV.text = data.productquantity

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                var number = snapshot.child("cart").child(data.productid.toString()).child("productnumber").getValue<Int>()
                holder.incIV.setOnClickListener {
                    number = number!! + 1
                    databaseReference.child("cart").child(data.productid.toString()).child("productnumber").setValue(number)
                }


                holder.decIV.setOnClickListener {
                    if (number!! > 1) {
                        number = number!! - 1
                        databaseReference.child("cart").child(data.productid.toString())
                            .child("productnumber").setValue(number)
                    } else {
                        databaseReference.child("cart").child(data.productid.toString())
                            .removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )
    }

    override fun getItemCount() = dataList.size

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val productnameTV: TextView = itemview.findViewById(R.id.tv_productname)
        val productnumberIV = itemview.findViewById<TextView>(R.id.tv_productnumber)
        val productquantityIV = itemview.findViewById<TextView>(R.id.tv_productquantity)
        val productimageIv = itemview.findViewById<ImageView>(R.id.iv_productimage)
        val productpriceIV = itemview.findViewById<TextView>(R.id.tv_productprice)
        val incIV = itemview.findViewById<ImageButton>(R.id.iv_plus)
        val decIV = itemview.findViewById<ImageButton>(R.id.iv_minus)
    }

}

