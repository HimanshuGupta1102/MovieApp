package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fetchdata.R
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileName = view.findViewById<TextView>(R.id.tvProfileName)
        val profileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val logoutButton = view.findViewById<MaterialButton>(R.id.btnLogout)

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                profileName.text = "${it.firstName} ${it.lastName}"
                profileEmail.text = it.email
            }
        }

        logoutButton.setOnClickListener {
            authViewModel.logout()
            dismiss()
            findNavController().navigate(R.id.signInFragment)
        }
    }
}

