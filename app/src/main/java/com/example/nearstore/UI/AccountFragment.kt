package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nearstore.R
import com.example.nearstore.databinding.FragmentAccountBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AccountFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var sharedPref: SharedPreferences
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", null)

        if (!uid.isNullOrEmpty()) {
            databaseReference.child(uid)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.tvUsername.text = snapshot.child("username").getValue(String::class.java) ?: "Unknown"
                        binding.tvUsernumber.text = snapshot.child("userphoneno").getValue(String::class.java) ?: "N/A"
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                )
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        binding.ivEdit.setOnClickListener {
            NameBottomSheet().show(parentFragmentManager, NameBottomSheet.TAG)
        }

        binding.cdOrderhistory.setOnClickListener {
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        binding.cdMyaddress.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        binding.cdHelp.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        binding.cdLogout.setOnClickListener {
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
            requireActivity().finish()
        }
    }
}