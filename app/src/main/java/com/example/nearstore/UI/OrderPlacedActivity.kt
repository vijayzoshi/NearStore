package com.example.nearstore.UI

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.nearstore.R
import com.google.firebase.database.FirebaseDatabase

class OrderPlacedActivity : AppCompatActivity() {


    private val database = FirebaseDatabase.getInstance().getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_placed)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("userdetails", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("userid", null).toString()


        val lottieView = findViewById<LottieAnimationView>(R.id.lottieView)
        val intent = Intent(this, MainActivity::class.java)

        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                println("Animation started!")
            }
            override fun onAnimationEnd(animation: Animator) {
                startActivity(intent)
                database.child("users").child(uid).child("cart").removeValue()
                finish()
            }
            override fun onAnimationCancel(animation: Animator) {
                println("Animation canceled!")
            }
            override fun onAnimationRepeat(animation: Animator) {
                println("Animation repeated!")
            }
        })

    }
}
