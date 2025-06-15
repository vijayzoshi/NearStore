package com.example.nearstore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


        /*
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }



         */





        /*
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottomnavigationview)
        bottomNavigationView.setupWithNavController(navController)


         */



   //   val navController : NavController = findNavController(R.id.fragment_main)
    //   NavigationUI.setupWithNavC
        //   ontroller(R.id.bottomnavigationview, navController)




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