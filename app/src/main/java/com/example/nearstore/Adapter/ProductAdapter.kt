package com.example.nearstore.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nearstore.Data.ProductData
import com.example.nearstore.Data.ProductModal
import com.example.nearstore.R
import com.example.nearstore.StoreModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProductAdapter(val context: Context, var datalist : ArrayList<ProductModal>) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    val sharedPref = context.getSharedPreferences("userdetails", Context.MODE_PRIVATE)
    val uid = sharedPref.getString("userid", "haha")
  var  databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid.toString())

    var onItemClick  : ((ProductModal) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_product, parent, false)
        return MyViewHolder(view)

    }




    override fun getItemCount(): Int {
        return datalist.size
    }

    fun searchDataList(searchList: ArrayList<ProductModal>) {
        datalist = searchList
        notifyDataSetChanged()
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.productnameIV.text = datalist.get(position).productname
        holder.productquantityIV.text = datalist.get(position).productquantity
        holder.productpriceIV.text = "₹" +datalist.get(position).productprice.toString()



        holder.llIV.visibility = View.GONE


        val imagelink = datalist.get(position).productpic
        Glide.with(context)
            .load(imagelink)
            .into(holder.productimageIv)


        holder.addIV.setOnClickListener {

            val a = datalist.get(position)

            val productData = ProductData(  a.productid,a.productpic,a.productprice,a.productquantity,a.productname,1)
            databaseReference.child("cart").child(datalist.get(position).productid.toString()).setValue(productData)
            holder.addIV.visibility = View.GONE
            holder.llIV.visibility = View.VISIBLE

            holder.numberIV.text = "1"

        }

        val a = datalist.get(position).productid.toString()

     databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var number =snapshot.child("cart").child(a).child("productnumber").getValue<Int>()

                holder.numberIV.text = number.toString()


                // additon
                holder.incIV.setOnClickListener {

                    number = number!! + 1
                    databaseReference.child("cart").child(a).child("productnumber").setValue(number)

                }


                //removing
                holder.decIV.setOnClickListener {

                    if(number!! >1){
                        number = number!! -1
                        databaseReference.child("cart").child(a).child("productnumber").setValue(number)
                    }else{
                        holder.addIV.visibility = View.VISIBLE
                        holder.llIV.visibility = View.GONE
                        databaseReference.child("cart").child(a).removeValue()

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
        val productimageIv = itemview.findViewById<ImageView>(R.id.iv_productimage)
        val productpriceIV = itemview.findViewById<TextView>(R.id.tv_productprice)

        val incIV = itemview.findViewById<ImageButton>(R.id.iv_plus)
        val decIV = itemview.findViewById<ImageButton>(R.id.iv_minus)
        val numberIV = itemview.findViewById<TextView>(R.id.tv_productnumber)
        val addIV = itemview.findViewById<Button>(R.id.btn_add)
        val llIV = itemview.findViewById<LinearLayout>(R.id.ll_buttons)



    }
}