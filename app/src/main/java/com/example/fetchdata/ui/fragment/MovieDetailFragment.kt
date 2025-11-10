package com.example.fetchdata.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.fetchdata.R
import com.example.fetchdata.data.local.FavouriteMovie
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel
import com.example.fetchdata.ui.viewmodel.MovieViewModel
import com.google.android.material.button.MaterialButton

class MovieDetailFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by activityViewModels()

    private var currentImdbId: String? = null
    private var isFavourite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get imdbId from arguments
        val imdbId = arguments?.getString("imdbId") ?: run {
            Toast.makeText(requireContext(), "Invalid movie ID", Toast.LENGTH_SHORT).show()
            return
        }

        currentImdbId = imdbId

        val imgPoster = view.findViewById<ImageView>(R.id.imgPoster)
        val imgBackdropPoster = view.findViewById<ImageView>(R.id.imgBackdropPoster)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvYear = view.findViewById<TextView>(R.id.tvYear)
        val tvRated = view.findViewById<TextView>(R.id.tvRated)
        val tvReleased = view.findViewById<TextView>(R.id.tvReleased)
        val tvRuntime = view.findViewById<TextView>(R.id.tvRuntime)
        val tvGenre = view.findViewById<TextView>(R.id.tvGenre)
        val tvDirector = view.findViewById<TextView>(R.id.tvDirector)
        val tvWriter = view.findViewById<TextView>(R.id.tvWriter)
        val tvActors = view.findViewById<TextView>(R.id.tvActors)
        val tvPlot = view.findViewById<TextView>(R.id.tvPlot)
        val tvRatings = view.findViewById<TextView>(R.id.tvRatings)
        val tvBoxOffice = view.findViewById<TextView>(R.id.tvBoxOffice)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarDetail)
        val errorTextView = view.findViewById<TextView>(R.id.tvErrorDetail)
        val contentLayout = view.findViewById<View>(R.id.contentLayout)
        val btnFavourite = view.findViewById<MaterialButton>(R.id.btnFavourite)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        // Handle back button click
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Check if movie is already in favorites
        favouriteViewModel.allFavourites.observe(viewLifecycleOwner) { favourites ->
            isFavourite = favourites.any { it.imdbID == imdbId }
            updateFavouriteButton(btnFavourite)
        }

        // Handle favorite button click
        btnFavourite.setOnClickListener {
            viewModel.movieDetail.value?.let { movie ->
                if (isFavourite) {
                    // Remove from favorites
                    favouriteViewModel.removeFavourite(imdbId)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    // Add to favorites
                    val favMovie = FavouriteMovie(
                        imdbID = movie.imdbID,
                        title = movie.Title,
                        year = movie.Year,
                        poster = movie.Poster,
                        type = movie.Type
                    )
                    favouriteViewModel.addFavourite(favMovie)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.fetchMovieDetails(imdbId)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorTextView.text = error
                errorTextView.visibility = View.VISIBLE
                contentLayout.visibility = View.GONE
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            } else {
                errorTextView.visibility = View.GONE
            }
        }

        viewModel.movieDetail.observe(viewLifecycleOwner) { movie ->
            movie?.let {
                contentLayout.visibility = View.VISIBLE
                errorTextView.visibility = View.GONE

                tvTitle.text = it.Title
                tvYear.text = it.Year
                tvRated.text = it.Rated
                tvReleased.text = it.Released
                tvRuntime.text = it.Runtime
                tvGenre.text = it.Genre
                tvDirector.text = "Director: ${it.Director}"
                tvWriter.text = "Writer: ${it.Writer}"
                tvActors.text = "Actors: ${it.Actors}"
                tvPlot.text = it.Plot

                val ratings = it.Ratings.joinToString("\n") { rating ->
                    "• ${rating.Source}: ${rating.Value}"
                }
                tvRatings.text = ratings.ifEmpty { "No ratings available" }

                tvBoxOffice.text = it.BoxOffice.ifEmpty { "N/A" }

                // Load main poster with rounded corners
                Glide.with(this)
                    .load(it.Poster)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgPoster)

                // Load backdrop poster (same image, blurred effect via alpha)
                Glide.with(this)
                    .load(it.Poster)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgBackdropPoster)
            }
        }
    }

    private fun updateFavouriteButton(button: MaterialButton) {
        if (isFavourite) {
            button.text = "Remove from Favorites"
            button.setIconResource(R.drawable.ic_star_filled)
        } else {
            button.text = "Add to Favorites"
            button.setIconResource(R.drawable.ic_star_outline)
        }
    }
}

