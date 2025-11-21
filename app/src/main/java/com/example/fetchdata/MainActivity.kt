package com.example.fetchdata

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fetchdata.ui.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (savedInstanceState == null) {
            authViewModel.checkIfUserLoggedIn()
        }

        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (navController.currentDestination?.id) {
                    R.id.homeFragment, R.id.signInFragment, R.id.signUpFragment -> {
                        // If we're on home or sign-in screen, close the app
                        finish()
                    }
                    else -> {
                        // For other screens, use default navigation back
                        if (!navController.popBackStack()) {
                            finish()
                        }
                    }
                }
            }
        })
    }
}
