package com.example.musicfest.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicfest.R
import com.example.musicfest.databinding.ItemArtistBinding
import com.example.musicfest.domain.model.ArtistModel

class ArtistsAdapter(
    private var artistsList: List<ArtistModel> = emptyList()
) : RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder>() {

    fun updateArtists(newList: List<ArtistModel>) {
        artistsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artistsList[position])
    }

    override fun getItemCount(): Int = artistsList.size

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artist: ArtistModel) {
            binding.tvArtistName.text = artist.name
            binding.tvArtistGenre.text = artist.genre.uppercase()
            binding.tvArtistBio.text = artist.bio

            if (artist.imageUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(artist.imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop() //  redondear las fotos de los artistas
                    .into(binding.ivArtistPic)
            }
        }
    }
}