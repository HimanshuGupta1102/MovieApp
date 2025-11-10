package com.example.fetchdata.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchdata.R
import com.example.fetchdata.ui.adapter.MovieAdapter
import com.example.fetchdata.ui.viewmodel.AllMoviesViewModel

class AllMoviesFragment : Fragment() {

    private val movieViewModel: AllMoviesViewModel by activityViewModels()

    private lateinit var adapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var paginationProgressBar: ProgressBar
    private lateinit var errorTextView: TextView

    private var isLoading = false
    private var canLoadMore = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView()
        observeViewModels()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        paginationProgressBar = view.findViewById(R.id.paginationProgressBar)
        errorTextView = view.findViewById(R.id.tvError)
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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (canLoadMore && !isLoading && totalItemCount > 0 &&
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {

                    if (isNetworkAvailable()) {
                        movieViewModel.loadMoreMovies()
                    } else {
                        Toast.makeText(requireContext(),
                            "No internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModels() {
        movieViewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty() && movieViewModel.isLoading.value == false) {
                errorTextView.text = "No movies found. Try a different search."
                errorTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else if (movies.isNotEmpty()) {
                errorTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter.clearMovies()
                adapter.addMovies(movies)
            }
        }

        movieViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            isLoading = loading

            if (adapter.itemCount == 0) {
                progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                paginationProgressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                paginationProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }

        movieViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Handle "No more results" as a toast instead of error text
                if (it.contains("No more results available", ignoreCase = true)) {
                    Toast.makeText(requireContext(), "No more movies to load", Toast.LENGTH_SHORT).show()
                    // Don't show error text for this case
                    if (adapter.itemCount > 0) {
                        errorTextView.visibility = View.GONE
                    }
                } else {
                    errorTextView.text = when {
                        it.contains("No movies found", ignoreCase = true) ->
                            "No movies found. Try a different search."
                        it.contains("Unable to resolve host", ignoreCase = true) ->
                            "No internet connection. Please check your network."
                        it.contains("timeout", ignoreCase = true) ->
                            "Request timed out. Please try again."
                        else -> "Error: $it"
                    }
                    errorTextView.visibility = View.VISIBLE

                    if (adapter.itemCount > 0) {
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.GONE
                    }
                }
            } ?: run {
                errorTextView.visibility = View.GONE
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun performSearch(query: String) {
        if (query.isNotBlank()) {
            adapter.clearMovies()
            canLoadMore = true
            movieViewModel.searchMovies(query)
        }
    }

    fun resetToDefault() {
        // Reset to default "movie" search
        adapter.clearMovies()
        canLoadMore = true
        movieViewModel.searchMovies("movie")
    }
}

