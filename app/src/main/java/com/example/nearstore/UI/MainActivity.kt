package com.example.nearstore.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.nearstore.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnavigationview)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> loadFragment(HomeFragment())
                R.id.accountFragment -> loadFragment(AccountFragment())
            }
            true
        }


    }

private fun loadFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit()
}

}