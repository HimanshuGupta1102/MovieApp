package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fetchdata.R
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmailSignIn)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPasswordSignIn)
        val btnSignIn = view.findViewById<MaterialButton>(R.id.btnSignIn)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarSignIn)
        val tvGoToSignUp = view.findViewById<TextView>(R.id.tvGoToSignUp)
        // Auto-login or post-login navigation centralized here to avoid double navigation
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Only navigate if we're still on the SignInFragment (prevents duplicate navigate on config changes)
                if (findNavController().currentDestination?.id == R.id.signInFragment) {
                    val action = SignInFragmentDirections.actionSignInToHome(user.firstName)
                    findNavController().navigate(action)
                }
            }
        }

        btnSignIn.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()
            authViewModel.signIn(email, password)
        }

        tvGoToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signUp)
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnSignIn.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    // We no longer navigate here; currentUser observer handles it
                    progressBar.visibility = View.GONE
                    btnSignIn.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                is AuthViewModel.AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnSignIn.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
