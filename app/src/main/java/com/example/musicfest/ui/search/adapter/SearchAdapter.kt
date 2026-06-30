package com.example.musicfest.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.musicfest.R
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.domain.model.FestivalModel

class SearchAdapter(
    private var festivals: List<FestivalModel> = emptyList(),
    private val onItemSelected: (FestivalModel) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.festival_card, parent, false)
        )
    }
    
    fun updateList(updatedList: List<FestivalModel>) {
        festivals = updatedList
        notifyDataSetChanged()
    }
    
    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int
    ) {
        holder.render(festivals[position], onItemSelected)
    }

    override fun getItemCount(): Int {
        return festivals.size
    }

}