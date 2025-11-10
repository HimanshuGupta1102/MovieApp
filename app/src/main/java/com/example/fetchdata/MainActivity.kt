package com.example.fetchdata

import android.os.Bundle
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

        // Check if user is already logged in on app startup
        authViewModel.checkIfUserLoggedIn()
        authViewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // User is logged in, navigate to home if not already there
                val currentDestination = navController.currentDestination?.id
                if (currentDestination == R.id.signInFragment || currentDestination == R.id.signUpFragment) {
                    navController.navigate(R.id.action_signIn_to_home)
                }
            }
        }
    }
}
