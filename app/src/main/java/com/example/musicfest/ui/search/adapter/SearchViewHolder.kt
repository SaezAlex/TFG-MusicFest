package com.example.musicfest.ui.search.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.R
import com.example.musicfest.databinding.FestivalCardBinding
import com.example.musicfest.domain.model.FestivalModel
import java.text.SimpleDateFormat
import java.util.Locale

class SearchViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = FestivalCardBinding.bind(view)
    private val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    fun render(festival: FestivalModel, onItemSelected: (FestivalModel) -> Unit) {
        binding.ivImage.setImageResource(R.drawable.music_fest_default)
        binding.tvDates.text = "${simpleDateFormat.format(festival.dateStart)} - ${simpleDateFormat.format(festival.dateEnd)}"
        binding.tvFestivalName.text = festival.name
        binding.tvLocation.text = festival.city
        binding.tvPrice.text = "${festival.ticketTypes["general"].toString()}€"
        
        val genresAdapter = SearchGenresAdapter(festival.musicGenres)
        binding.rvGenres.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvGenres.adapter = genresAdapter
    }
}