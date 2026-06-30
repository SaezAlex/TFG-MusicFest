package com.example.musicfest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.domain.model.FestivalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val festivalProvider: FestivalProvider): ViewModel() {
    private var _state = MutableStateFlow<FestivalListState>(FestivalListState.Loading)
    val state: StateFlow<FestivalListState> = _state
    
    init {
        viewModelScope.launch { 
            _state.value = FestivalListState.Loading
            val result = withContext(Dispatchers.IO) {
                festivalProvider.getFestivals()
            }
            
            if(result.isNotEmpty()) {
               _state.value = FestivalListState.Success(result)
            } else {
               _state.value = FestivalListState.Error
            }
        }
    }
}