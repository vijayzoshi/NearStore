package com.example.nearstore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NameBottomSheet : BottomSheetDialogFragment() {


    val database = FirebaseDatabase.getInstance().getReference()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_name_bottomsheet, container, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")


        val usernameTf = view.findViewById<TextInputLayout>(R.id.tf_username)
        usernameTf.editText?.setText("")


        val phonenumberTf = view.findViewById<TextInputLayout>(R.id.tf_phonenumber)
        phonenumberTf.editText?.setText("")

        database.child("users").child(uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    usernameTf.editText?.setText(snapshot.child("username").getValue(String::class.java))
                    phonenumberTf.editText?.setText(snapshot.child("userphoneno").getValue(String::class.java))


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        val confirmBtn = view.findViewById<Button>(R.id.btn_confirm)
        confirmBtn.setOnClickListener {


            database.child("users").child(uid.toString()).child("username").setValue(usernameTf.editText?.text.toString())
            database.child("users").child(uid.toString()).child("userphoneno").setValue(phonenumberTf.editText?.text.toString())

            dismiss()
        }


    }

    companion object {
        const val TAG = "NameBottomSheet"
    }
}