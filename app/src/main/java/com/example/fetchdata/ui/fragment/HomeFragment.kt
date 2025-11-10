package com.example.fetchdata.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.fetchdata.R
import com.example.fetchdata.ui.adapter.HomeTabAdapter
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var searchView: SearchView
    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: MaterialButton

    private val authViewModel: AuthViewModel by activityViewModels()
    private val allMoviesFragment = AllMoviesFragment()
    private val favouritesFragment = FavouritesFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupViewPager()
        setupSearchView()
    }

    private fun setupViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        searchView = view.findViewById(R.id.searchView)
        welcomeTextView = view.findViewById(R.id.tvWelcomeHome)
        logoutButton = view.findViewById(R.id.btnLogout)

        // Get firstName from navigation arguments
        val firstName = arguments?.getString("firstName") ?: "User"
        welcomeTextView.text = "Welcome $firstName!"

        // Setup logout button
        logoutButton.setOnClickListener {
            authViewModel.logout()
            // Navigate back to sign in screen
            findNavController().navigate(R.id.signInFragment)
        }

        // Style SearchView for visibility
        val searchEditText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.setTextColor(android.graphics.Color.WHITE)
        searchEditText?.setHintTextColor(android.graphics.Color.GRAY)

        val searchIcon = searchView.findViewById<android.widget.ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon?.setColorFilter(android.graphics.Color.WHITE)

        val closeIcon = searchView.findViewById<android.widget.ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeIcon?.setColorFilter(android.graphics.Color.WHITE)
    }

    private fun setupViewPager() {
        val fragments = listOf(allMoviesFragment, favouritesFragment)
        val adapter = HomeTabAdapter(this, fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.all_movies)
                1 -> getString(R.string.favourites)
                else -> ""
            }
        }.attach()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.trim().isNotEmpty()) {
                        performSearch(it.trim())
                    } else {
                        // If empty, reset to original state
                        resetToOriginalState()
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Real-time search implementation
                val query = newText?.trim() ?: ""

                if (query.isEmpty()) {
                    // Reset to original state when search is cleared
                    resetToOriginalState()
                } else if (query.length >= 2) {
                    // Only search if query is at least 2 characters
                    performSearch(query)
                }
                return true
            }
        })
    }

    private fun resetToOriginalState() {
        when (viewPager.currentItem) {
            0 -> {
                // Reset "All" tab to show default movies
                allMoviesFragment.resetToDefault()
            }
            1 -> {
                // Reset "Favourites" tab to show all favourites
                favouritesFragment.resetToDefault()
            }
        }
    }

    private fun performSearch(query: String) {
        when (viewPager.currentItem) {
            0 -> allMoviesFragment.performSearch(query)
            1 -> favouritesFragment.performSearch(query)
        }
    }
}

