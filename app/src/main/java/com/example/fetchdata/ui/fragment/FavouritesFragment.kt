package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchdata.R
import com.example.fetchdata.ui.adapter.MovieAdapter
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private val favouriteViewModel: FavouriteViewModel by activityViewModels()

    private lateinit var adapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private var isSearchMode = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewFavourites)
        emptyTextView = view.findViewById(R.id.tvEmptyFavourites)
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            mutableListOf(),
            onMovieClick = { imdbId ->
                val bundle = Bundle().apply {
                    putString("imdbId", imdbId)
                }
                findNavController().navigate(R.id.action_home_to_movieDetail, bundle)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        favouriteViewModel.allFavourites.observe(viewLifecycleOwner) { favourites ->
            if (isSearchMode) return@observe // Don't update when in search mode

            if (favourites.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val moviesList = favouriteViewModel.transformToMovieSearch(favourites)
                adapter.clearMovies()
                adapter.addMovies(moviesList)
            }
        }

        favouriteViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (!isSearchMode) return@observe // Only update when in search mode

            if (searchResults.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                emptyTextView.text = getString(R.string.no_matching_favourites)
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val moviesList = favouriteViewModel.transformToMovieSearch(searchResults)
                adapter.clearMovies()
                adapter.addMovies(moviesList)
            }
        }
    }

    fun performSearch(query: String) {
        isSearchMode = true
        favouriteViewModel.searchFavourites(query)
    }

    fun resetToDefault() {
        isSearchMode = false
        emptyTextView.text = getString(R.string.no_favourites_yet)
        favouriteViewModel.allFavourites.value?.let { favourites ->
            if (favourites.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val moviesList = favouriteViewModel.transformToMovieSearch(favourites)
                adapter.clearMovies()
                adapter.addMovies(moviesList)
            }
        }
    }
}

