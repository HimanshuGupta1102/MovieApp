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

class SignUpFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName = view.findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = view.findViewById<TextInputEditText>(R.id.etLastName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmailSignUp)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPasswordSignUp)
        val btnSignUp = view.findViewById<MaterialButton>(R.id.btnSignUp)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarSignUp)
        val tvGoToSignIn = view.findViewById<TextView>(R.id.tvGoToSignIn)

        btnSignUp.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            authViewModel.signUp(firstName, lastName, email, password)
        }

        tvGoToSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUp_to_signIn)
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnSignUp.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnSignUp.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Navigate to sign in after successful signup
                    findNavController().navigate(R.id.action_signUp_to_signIn)
                }
                is AuthViewModel.AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnSignUp.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

