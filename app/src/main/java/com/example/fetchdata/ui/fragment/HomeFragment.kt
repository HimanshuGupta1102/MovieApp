package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.fetchdata.R
import com.example.fetchdata.ui.adapter.HomeTabAdapter
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var searchView: SearchView
    private lateinit var welcomeTextView: TextView
    private lateinit var profileIcon: ImageView

    private val authViewModel: AuthViewModel by activityViewModels()
    private val favouriteViewModel: FavouriteViewModel by activityViewModels()
    private val allMoviesFragment = AllMoviesFragment()
    private val favouritesFragment = FavouritesFragment()

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DEBOUNCE_DELAY = 500L // 500ms delay for better UX
    private var lastSearchQuery = "" // Track last query to avoid duplicates

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

        // Initialize FavouriteViewModel with current user's email
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            android.util.Log.d("HomeFragment", "User changed: ${user?.email}")
            favouriteViewModel.setUserEmail(user?.email)
        }
    }

    private fun setupViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        searchView = view.findViewById(R.id.searchView)
        welcomeTextView = view.findViewById(R.id.tvWelcomeHome)
        profileIcon = view.findViewById(R.id.ivProfileIcon)

        // Get firstName from navigation arguments
        val firstName = arguments?.getString("firstName") ?: "User"
        welcomeTextView.text = "Welcome $firstName!"

        // Setup profile icon to open bottom sheet
        profileIcon.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(parentFragmentManager, "ProfileBottomSheet")
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


    override fun onDestroyView() {
        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
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
                // Real-time search implementation with debouncing
                val query = newText?.trim() ?: ""
                android.util.Log.d("HomeFragment", "Search query changed: '$query' (length: ${query.length})")

                // Cancel any pending search
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (query.isEmpty()) {
                    // Reset to original state when search is cleared
                    android.util.Log.d("HomeFragment", "Query empty, resetting to original state")
                    resetToOriginalState()
                } else if (query.length >= 2) {
                    // Only search if query is at least 2 characters, with debounce
                    android.util.Log.d("HomeFragment", "Query length >= 2, scheduling search")
                    searchRunnable = Runnable {
                        performSearch(query)
                    }
                    searchHandler.postDelayed(searchRunnable!!, 300) // 300ms debounce
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
        android.util.Log.d("HomeFragment", "performSearch called with query: '$query', current tab: ${viewPager.currentItem}")
        when (viewPager.currentItem) {
            0 -> allMoviesFragment.performSearch(query)
            1 -> favouritesFragment.performSearch(query)
        }
    }
}

