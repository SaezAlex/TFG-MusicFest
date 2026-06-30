package com.example.musicfest.ui.home.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.R
import com.example.musicfest.domain.model.MusicGenre
import kotlin.coroutines.coroutineContext

class GenresViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvGenre = view.findViewById<TextView>(R.id.tvGenre)

    fun render(genre: MusicGenre) {
        tvGenre.text = getString(itemView.context, genre.genre)
        tvGenre.background.setTint(getColor(itemView.context, genre.color))
    }
}