package com.example.musicfest.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.data.UserRepository
import com.example.musicfest.domain.model.FestivalModel
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Definición del estado siguiendo el patrón de Home
sealed class FavoritesState {
    data object Loading : FavoritesState()
    data class Success(val favorites: List<FestivalModel>) : FavoritesState()
    data object Error : FavoritesState()
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val festivalProvider: FestivalProvider
) : ViewModel() {

    private var _state = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val state: StateFlow<FavoritesState> = _state

    fun loadFavorites(userUid: String) {
        viewModelScope.launch {
            _state.value = FavoritesState.Loading

            val result = withContext(Dispatchers.IO) {
                try {
                    val user = userRepository.getUserById(userUid)
                    val favoriteList = mutableListOf<FestivalModel>()

                    user?.likedFestivals?.forEach { item ->
                        val festivalId = when (item) {
                            is String -> item
                            is DocumentReference -> item.id
                            else -> null
                        }

                        if (festivalId != null) {
                            val festival = festivalProvider.getFestival(festivalId)
                            festival?.let { favoriteList.add(it) }
                        }
                    }
                    favoriteList
                } catch (e: Exception) {
                    null
                }
            }

            if (result != null) {
                _state.value = FavoritesState.Success(result)
            } else {
                _state.value = FavoritesState.Error
            }
        }
    }
}