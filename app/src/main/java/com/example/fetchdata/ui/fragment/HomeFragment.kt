package com.example.fetchdata.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.fetchdata.R
import com.example.fetchdata.ui.adapter.HomeTabAdapter
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel
import com.example.fetchdata.ui.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var searchView: SearchView
    private lateinit var welcomeTextView: TextView
    private lateinit var profileIcon: ImageView

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val favouriteViewModel: FavouriteViewModel by activityViewModels()
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
        observeViewModel()

        // Initialize FavouriteViewModel with current user's email
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
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
        welcomeTextView.text = getString(R.string.welcome, firstName)

        // Setup profile icon to open bottom sheet
        profileIcon.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(parentFragmentManager, "ProfileBottomSheet")
        }

        // Style SearchView for visibility
        val searchEditText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.setTextColor(Color.WHITE)
        searchEditText?.setHintTextColor(Color.GRAY)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon?.setColorFilter(Color.WHITE)

        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeIcon?.setColorFilter(Color.WHITE)
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

        // Track tab changes in ViewModel
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.setCurrentTab(position)
            }
        })
    }
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { homeViewModel.onSearchQuerySubmitted(it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        // Observe search queries and perform search
        homeViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            when (viewPager.currentItem) {
                0 -> allMoviesFragment.performSearch(query)
                1 -> favouritesFragment.performSearch(query)
            }
        }

        // Observe reset to default state
        homeViewModel.shouldResetToDefault.observe(viewLifecycleOwner) { shouldReset ->
            if (shouldReset) {
                resetToOriginalState()
                homeViewModel.resetSearchState()
            }
        }
    }

    private fun resetToOriginalState() {
        when (viewPager.currentItem) {
            0 -> allMoviesFragment.resetToDefault()
            1 -> favouritesFragment.resetToDefault()
        }
    }
}

