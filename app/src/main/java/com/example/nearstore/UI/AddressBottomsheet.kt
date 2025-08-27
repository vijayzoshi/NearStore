package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.nearstore.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressBottomsheet : BottomSheetDialogFragment() {


    val database = FirebaseDatabase.getInstance().getReference()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_address_bottomsheet, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")
        val useraddressTf = view.findViewById<TextView>(R.id.tf_address)

        database.child("users").child(uid.toString()).child("useraddress")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    useraddressTf.setText(snapshot.getValue(String::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        val confirmBtn = view.findViewById<Button>(R.id.btn_confirm)
        confirmBtn.setOnClickListener {
            val intent = Intent(requireContext(), EditAddressActivity::class.java)
            intent.putExtra("source", "bottomsheet")
            startActivity(intent)
            dismiss()
        }


    }

    companion object {
        const val TAG = "AddressBottomSheet"
    }
}