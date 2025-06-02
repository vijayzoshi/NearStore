package com.example.nearstore

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {

   // private lateinit var googleSignInClient: GoogleSignInClient
   private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // From Firebase
            .requestEmail()
            .build()

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
            val intent = Intent(requireContext(), EnterOtpActivity::class.java)
            startActivity(intent)

            // Optional: navigate to login screen or update UI
        }
    }
}