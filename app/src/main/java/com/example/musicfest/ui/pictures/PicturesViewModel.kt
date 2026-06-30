package com.example.musicfest.ui.pictures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.domain.model.ArtistModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class ArtistsState {
    object Loading : ArtistsState()
    data class Success(val artists: List<ArtistModel>) : ArtistsState()
    object Empty : ArtistsState()
    data class Error(val message: String) : ArtistsState()
}

@HiltViewModel
class PicturesViewModel @Inject constructor(
    private val repository: FestivalProvider
) : ViewModel() {

    private val _state = MutableStateFlow<ArtistsState>(ArtistsState.Loading)
    val state: StateFlow<ArtistsState> = _state

    init {
        // nada más instanciarse el ViewModel bajamos los artitas
        getArtists()
    }

    private fun getArtists() {
        viewModelScope.launch {
            _state.value = ArtistsState.Loading
            try {
                val artists = withContext(Dispatchers.IO) { repository.getArtists() }
                if (artists.isEmpty()) {
                    _state.value = ArtistsState.Empty
                } else {
                    _state.value = ArtistsState.Success(artists)
                }
            } catch (e: Exception) {
                _state.value = ArtistsState.Error(e.message ?: "Error al cargar artistas")
            }
        }
    }
}