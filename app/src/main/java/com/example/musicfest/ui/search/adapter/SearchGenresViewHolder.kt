package com.example.musicfest.ui.search.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.R
import com.example.musicfest.domain.model.MusicGenre

class SearchGenresViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvGenre = view.findViewById<TextView>(R.id.tvGenre)

    fun render(genre: MusicGenre) {
        tvGenre.text = getString(itemView.context, genre.genre)
        tvGenre.background.setTint(getColor(itemView.context, genre.color))
    }
}