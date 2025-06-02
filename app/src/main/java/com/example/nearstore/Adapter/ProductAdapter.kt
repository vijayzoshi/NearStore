package com.example.nearstore.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nearstore.Data.ProductData
import com.example.nearstore.Data.ProductModal
import com.example.nearstore.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProductAdapter(val context: Context, var datalist : ArrayList<ProductModal>) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

  var  databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child("1")

    var onItemClick  : ((ProductModal) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_product, parent, false)
        return MyViewHolder(view)

    }




    override fun getItemCount(): Int {
        return datalist.size
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.productnameIV.text = datalist.get(position).productname
        holder.productquantityIV.text = datalist.get(position).productquantity
        holder.productpriceIV.text = datalist.get(position).productprice.toString()



        holder.addIV.setOnClickListener {

            val a = datalist.get(position)

            val productData = ProductData(  a.productid,0,a.productprice,a.productquantity,a.productname,1)
            databaseReference.child("cart").child(datalist.get(position).productid.toString()).setValue(productData)

        }

        val a = datalist.get(position).productid.toString()

     databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var number =snapshot.child("cart").child(a).child("productnumber").getValue<Int>()
                holder.numberIV.text = number.toString()


                holder.incIV.setOnClickListener {

                    number = number!! + 1
                    databaseReference.child("cart").child(a).child("productnumber").setValue(number)

                }


                holder.decIV.setOnClickListener {

                    if(number!! >0){
                        number = number!! -1
                        databaseReference.child("cart").child(a).child("productnumber").setValue(number)
                    }


                }




            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }



        )




        holder.itemView.setOnClickListener {
            onItemClick?.invoke(datalist.get(position))
        }





    }


    class MyViewHolder(itemview : View ) : RecyclerView.ViewHolder(itemview){
        val productnameIV = itemview.findViewById<TextView>(R.id.tv_productname)
        val productquantityIV = itemview.findViewById<TextView>(R.id.tv_productquantity)
        val productiamgeIv = itemview.findViewById<ImageView>(R.id.iv_productimage)
        val productpriceIV = itemview.findViewById<TextView>(R.id.tv_productprice)

        val incIV = itemview.findViewById<ImageButton>(R.id.iv_plus)
        val decIV = itemview.findViewById<ImageButton>(R.id.iv_minus)
        val numberIV = itemview.findViewById<TextView>(R.id.tv_productnumber)
        val addIV = itemview.findViewById<Button>(R.id.btn_add)



    }
}