package com.example.fetchdata.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.fetchdata.data.model.FavouriteMovie
import com.example.fetchdata.data.model.MovieDetail
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import com.example.fetchdata.ui.viewmodel.FavouriteViewModel
import com.example.fetchdata.ui.viewmodel.MovieViewModel

class MovieDetailFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieDetailScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get imdbId from arguments and fetch movie details
        val imdbId = arguments?.getString("imdbId") ?: run {
            Toast.makeText(requireContext(), "Invalid movie ID", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        viewModel.fetchMovieDetails(imdbId)
    }

    @Composable
    fun MovieDetailScreen() {
        val movieDetail by viewModel.movieDetail.observeAsState()
        val isLoading by viewModel.isLoading.observeAsState(false)
        val error by viewModel.error.observeAsState()
        val allFavourites by favouriteViewModel.allFavourites.observeAsState(emptyList())
        val currentUser by authViewModel.currentUser.observeAsState()

        // Track favorite state with proper recomposition
        val isFavourite by remember {
            derivedStateOf {
                movieDetail?.let { movie ->
                    allFavourites.any { it.imdbID == movie.imdbID }
                } ?: false
            }
        }

        MaterialTheme(
            colorScheme = darkColorScheme(
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                primary = Color(0xFFFFD700),
                onBackground = Color.White,
                onSurface = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(top = 30.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFFD700)
                        )
                    }
                    error != null -> {
                        Text(
                            text = error ?: "Unknown error",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                    movieDetail != null -> {
                        val movie = movieDetail!!
                        MovieDetailContent(
                            movie = movie,
                            isFavourite = isFavourite,
                            onFavouriteClick = {
                                handleFavouriteClick(movie, isFavourite, currentUser?.email)
                            }
                        )
                    }
                }

                // Back button overlay
                IconButton(
                    onClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun MovieDetailContent(
        movie: MovieDetail,
        isFavourite: Boolean,
        onFavouriteClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Backdrop and Poster Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                // Backdrop image with reduced opacity
                AsyncImage(
                    model = movie.Poster ?: "",
                    contentDescription = "Backdrop Poster",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.3f),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF121212).copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Main poster card
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .height(270.dp)
                        .align(Alignment.Center)
                        .padding(top = 40.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    AsyncImage(
                        model = movie.Poster ?: "",
                        contentDescription = "Movie Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Favourite Button
            OutlinedButton(
                onClick = onFavouriteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFavourite) "Remove from Favorites" else "Add to Favorites",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Title and Basic Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = movie.Title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Year, Rated, Runtime Row
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.Year,
                        fontSize = 14.sp,
                        color = Color(0xFFBBBBBB)
                    )
                    if (!movie.Rated.isNullOrEmpty() && movie.Rated != "N/A") {
                        Text(
                            text = " • ",
                            fontSize = 14.sp,
                            color = Color(0xFFBBBBBB)
                        )
                        Text(
                            text = movie.Rated,
                            fontSize = 14.sp,
                            color = Color(0xFFBBBBBB)
                        )
                    }
                    if (!movie.Runtime.isNullOrEmpty() && movie.Runtime != "N/A") {
                        Text(
                            text = " • ",
                            fontSize = 14.sp,
                            color = Color(0xFFBBBBBB)
                        )
                        Text(
                            text = movie.Runtime,
                            fontSize = 14.sp,
                            color = Color(0xFFBBBBBB)
                        )
                    }
                }

                // Genre
                if (!movie.Genre.isNullOrEmpty() && movie.Genre != "N/A") {
                    Text(
                        text = movie.Genre,
                        fontSize = 14.sp,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(top = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Plot Section
            if (!movie.Plot.isNullOrEmpty() && movie.Plot != "N/A") {
                DetailSection(
                    title = "📖 Plot",
                    content = movie.Plot
                )
            }

            // Cast & Crew Section
            if (!movie.Director.isNullOrEmpty() || !movie.Writer.isNullOrEmpty() || !movie.Actors.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "🎬 Cast & Crew",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (!movie.Director.isNullOrEmpty() && movie.Director != "N/A") {
                        Text(
                            text = "Director: ${movie.Director}",
                            fontSize = 14.sp,
                            color = Color(0xFFCCCCCC),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (!movie.Writer.isNullOrEmpty() && movie.Writer != "N/A") {
                        Text(
                            text = "Writer: ${movie.Writer}",
                            fontSize = 14.sp,
                            color = Color(0xFFCCCCCC),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (!movie.Actors.isNullOrEmpty() && movie.Actors != "N/A") {
                        Text(
                            text = "Actors: ${movie.Actors}",
                            fontSize = 14.sp,
                            color = Color(0xFFCCCCCC),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Release Info Section
            if (!movie.Released.isNullOrEmpty() && movie.Released != "N/A") {
                DetailSection(
                    title = "📅 Release Info",
                    content = movie.Released
                )
            }

            // Ratings Section
            if (!movie.Ratings.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "⭐ Ratings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    val ratingsText = movie.Ratings.joinToString("\n") {
                        "• ${it.Source ?: "Unknown"}: ${it.Value ?: "N/A"}"
                    }
                    Text(
                        text = ratingsText,
                        fontSize = 14.sp,
                        color = Color(0xFFCCCCCC),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Box Office Section
            if (!movie.BoxOffice.isNullOrEmpty() && movie.BoxOffice != "N/A") {
                DetailSection(
                    title = "💰 Box Office",
                    content = movie.BoxOffice
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    @Composable
    fun DetailSection(title: String, content: String) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = content,
                fontSize = 15.sp,
                color = Color(0xFFCCCCCC),
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 22.sp
            )
        }
    }

    private fun handleFavouriteClick(movie: MovieDetail, isFavourite: Boolean, userEmail: String?) {
        android.util.Log.d("MovieDetailFragment", "Favourite button clicked: isFavourite=$isFavourite")

        if (isFavourite) {
            android.util.Log.d("MovieDetailFragment", "Removing from favorites: ${movie.imdbID}")
            favouriteViewModel.removeFavourite(movie.imdbID)
            Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            if (userEmail == null) {
                android.util.Log.d("MovieDetailFragment", "User not logged in")
                Toast.makeText(requireContext(), "Please sign in to add favourites", Toast.LENGTH_SHORT).show()
                return
            }
            android.util.Log.d("MovieDetailFragment", "Adding to favorites: ${movie.Title}")
            val favMovie = FavouriteMovie(
                imdbID = movie.imdbID,
                userEmail = userEmail,
                title = movie.Title,
                year = movie.Year,
                poster = movie.Poster ?: "",
                type = movie.Type
            )
            favouriteViewModel.addFavourite(favMovie)
            Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
        }
    }
}

