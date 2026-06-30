package com.example.musicfest.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.R
import com.example.musicfest.domain.model.MusicGenre

class SearchGenresAdapter (private var genres: List<MusicGenre> = emptyList()): RecyclerView.Adapter<SearchGenresViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchGenresViewHolder {
        return SearchGenresViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_genre_badge, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: SearchGenresViewHolder,
        position: Int
    ) {
        holder.render(genres[position])
    }

    override fun getItemCount(): Int {
        return genres.size
    }

}