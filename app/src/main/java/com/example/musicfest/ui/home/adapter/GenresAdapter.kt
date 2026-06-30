package com.example.musicfest.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.R
import com.example.musicfest.domain.model.MusicGenre

class GenresAdapter (private var genres: List<MusicGenre> = emptyList()): RecyclerView.Adapter<GenresViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenresViewHolder {
        return GenresViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_genre_badge, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: GenresViewHolder,
        position: Int
    ) {
        holder.render(genres[position])
    }

    override fun getItemCount(): Int {
        return genres.size
    }

}