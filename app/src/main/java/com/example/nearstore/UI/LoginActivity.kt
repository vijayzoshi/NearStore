package com.example.nearstore.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nearstore.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    val databaseReference = FirebaseDatabase.getInstance().getReference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Check if user is already signed in
        if (auth.currentUser != null) {
            // User is already signed in, send to main activity
            startActivity(Intent(this, NameAddressActivity::class.java))
            finish()
            return
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<SignInButton>(R.id.btnVerifyOtp).setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser

                    val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("userid", user?.uid)

                    editor.apply()


              //      startActivity(Intent(this@LoginActivity, NameAddressActivity::class.java))
                //    finish()




                    databaseReference.child("users").child(auth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild("username")) {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()                            }
                            else {
                                startActivity(Intent(this@LoginActivity, NameAddressActivity::class.java))
                                finish()                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

         /*
                    if (user != null) {
                        databaseReference.child("users").child(user.uid)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {


                                    val username = snapshot.child("username").getValue(String::class.java).toString()
                                    if(username.isEmpty()){
                                        startActivity(Intent(this@LoginActivity, NameAddressActivity::class.java))
                                        finish()

                                    }else{
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish()
                                    }


                                }


                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")

                                }
                            }

                            )
                    }
*/

                } else {
                    Toast.makeText(this, "Firebase Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



}