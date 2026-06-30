package com.example.musicfest.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.domain.model.MusicGenre
import com.example.musicfest.ui.home.FestivalListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val festivalProvider: FestivalProvider): ViewModel() {
    private var _state = MutableStateFlow<FestivalListState>(FestivalListState.Loading)
    val state: StateFlow<FestivalListState> = _state
    
    private val selectedGenres = mutableSetOf<String>()
    
    fun onSelectedGenre(genre: MusicGenre, selected: Boolean) {
        if (selected) {
            selectedGenres.add(genre.name)
        } else {
            selectedGenres.remove(genre.name)
        }
        
        applyFilters()
    }

    fun applyFilters() {
        viewModelScope.launch {
            _state.value = FestivalListState.Loading

            val result = withContext(Dispatchers.IO) {
                val allFestivals = festivalProvider.getInitialSearchResults()

                if (selectedGenres.isEmpty()) {
                    allFestivals
                } else {
                    allFestivals.filter { festival ->
                        selectedGenres.all { selected ->
                            festival.genres.contains(selected)
                        }
                    }
                }
            }

            if (result.isNotEmpty()) {
                _state.value = FestivalListState.Success(result)
            } else {
                _state.value = FestivalListState.Success(emptyList())
            }
        }
    }
    
    fun loadInitialResults() {
        viewModelScope.launch { 
            _state.value = FestivalListState.Loading
            val result = withContext(Dispatchers.IO) {
                festivalProvider.getInitialSearchResults()
            }
            
            if(result.isNotEmpty()) {
                _state.value = FestivalListState.Success(result)
            } else {
                _state.value = FestivalListState.Error
            }
        }
    }
    
    fun search(query: String) {
        viewModelScope.launch { 
            _state.value = FestivalListState.Loading
            val result = withContext(Dispatchers.IO) {
                festivalProvider.search(query)
            }
            _state.value = FestivalListState.Success(result)
        }
    }
}