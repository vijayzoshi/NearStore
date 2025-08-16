package com.example.nearstore

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AccountFragment : Fragment() {

   // private lateinit var googleSignInClient: GoogleSignInClient
   private lateinit var googleSignInClient: GoogleSignInClient
    val databaseReference = FirebaseDatabase.getInstance().getReference()

    private lateinit var sharedPref : SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameTv : TextView  = view.findViewById(R.id.tv_username)
        val usernumberTv : TextView  = view.findViewById(R.id.tv_usernumber)
sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", "haha")


        if (uid != null) {
            databaseReference.child("users").child(uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {



                        usernameTv.text = snapshot.child("username").getValue(String::class.java).toString()
                        usernumberTv.text = snapshot.child("userphoneno").getValue(String::class.java).toString()



                    }



                    override fun onCancelled(error: DatabaseError) {

                    }
                }

                )
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // From Firebase
            .requestEmail()
            .build()

        val edit = view.findViewById<TextView>(R.id.iv_edit)
        edit.setOnClickListener {

            val bottomSheet = NameBottomSheet()
            bottomSheet.show(parentFragmentManager, NameBottomSheet.TAG)

        }
        val orderhistory = view.findViewById<CardView>(R.id.cd_orderhistory)
        orderhistory.setOnClickListener {

            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        val myaddress = view.findViewById<CardView>(R.id.cd_myaddress)
        myaddress.setOnClickListener {

            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        val help = view.findViewById<CardView>(R.id.cd_help)
        help.setOnClickListener {

            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val btnSignOut = view.findViewById<CardView>(R.id.cd_logout)
        btnSignOut.setOnClickListener {
            signOut()
        }
    }





    private fun signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
            sharedPref.edit().remove("userid").apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)



            // Optional: navigate to login screen or update UI
        }
    }
}