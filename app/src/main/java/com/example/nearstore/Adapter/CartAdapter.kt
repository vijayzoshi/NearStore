package com.example.nearstore.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Data.Product
import com.example.nearstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class CartAdapter(private val dataList: ArrayList<Product>) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {


    var  databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child("1")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList.get(position)
            holder.productnameTV.text = data.productname
            holder.productnumberIV.text = data.productnumber.toString()

        holder.productpriceIV.text = data.productprice.toString()
            holder.productquantityIV.text = data.productquantity


            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var number =snapshot.child("cart").child(data.productid.toString()).child("productnumber").getValue<Int>()
                   // holder.productnumberIV.text = number.toString()


                    holder.incIV.setOnClickListener {

                        number = number!! + 1
                        databaseReference.child("cart").child(data.productid.toString()).child("productnumber").setValue(number)

                    }


                    holder.decIV.setOnClickListener {

                        if(number!! >0){
                            number = number!! -1
                            databaseReference.child("cart").child(data.productid.toString()).child("productnumber").setValue(number)
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
        val productiamgeIv = itemview.findViewById<ImageView>(R.id.iv_productimage)
        val productpriceIV = itemview.findViewById<TextView>(R.id.tv_productprice)


       val incIV = itemview.findViewById<ImageButton>(R.id.iv_plus)
        val decIV = itemview.findViewById<ImageButton>(R.id.iv_minus)




    }

    }

