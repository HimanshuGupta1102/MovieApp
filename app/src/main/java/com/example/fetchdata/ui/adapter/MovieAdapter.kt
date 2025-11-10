package com.example.fetchdata.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fetchdata.R
import com.example.fetchdata.data.model.MovieSearch

class MovieAdapter(
    private val movies: MutableList<MovieSearch>,
    private val onMovieClick: (String) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.ivMoviePoster)
        val title: TextView = view.findViewById(R.id.tvMovieTitle)
        val year: TextView = view.findViewById(R.id.tvMovieYear)

        fun bind(movie: MovieSearch) {
            title.text = movie.Title
            year.text = movie.Year

            Glide.with(itemView.context)
                .load(movie.Poster)
                .placeholder(R.drawable.ic_launcher_background)
                .into(poster)

            itemView.setOnClickListener {
                onMovieClick(movie.imdbID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    fun addMovies(newMovies: List<MovieSearch>) {
        val startPosition = movies.size
        movies.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun clearMovies() {
        movies.clear()
        notifyDataSetChanged()
    }
}

