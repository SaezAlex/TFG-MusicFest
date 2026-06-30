package com.example.musicfest.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.musicfest.R
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.domain.model.FestivalModel

class HomeAdapter(
    private var festivals: List<FestivalModel> = emptyList(),
    private val onItemSelected: (FestivalModel) -> Unit
) : RecyclerView.Adapter<HomeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.festival_card, parent, false)
        )
    }
    
    override fun onBindViewHolder(
        holder: HomeViewHolder,
        position: Int
    ) {
        holder.render(festivals[position], onItemSelected)
    }

    override fun getItemCount(): Int {
        return festivals.size
    }

}