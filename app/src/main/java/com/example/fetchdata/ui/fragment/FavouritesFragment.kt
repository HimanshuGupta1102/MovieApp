package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchdata.R
import com.example.fetchdata.data.model.MovieSearch
import com.example.fetchdata.ui.adapter.MovieAdapter
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel

class FavouritesFragment : Fragment() {

    private val favouriteViewModel: FavouriteViewModel by activityViewModels()

    private lateinit var adapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView


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
            if (favourites.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val moviesList = favourites.map { fav ->
                    MovieSearch(
                        Title = fav.title,
                        Year = fav.year,
                        imdbID = fav.imdbID,
                        Type = fav.type,
                        Poster = fav.poster
                    )
                }

                adapter.clearMovies()
                adapter.addMovies(moviesList)
            }
        }
    }

    fun performSearch(query: String) {
        // Searching is not needed since observeViewModel already shows all favourites
        // If you want to implement search, do it client-side by filtering the adapter
    }

    fun resetToDefault() {
        // Nothing to do - observeViewModel already handles showing all favourites
    }
}

