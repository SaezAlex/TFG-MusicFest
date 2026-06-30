package com.example.musicfest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.data.UserRepository
import com.example.musicfest.domain.model.UserModel
import com.example.musicfest.ui.home.FestivalListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FestivalDetailViewModel @Inject constructor(
    private val Festivalrepository: FestivalProvider,
    private val Userrepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<FestivalListState>(FestivalListState.Loading)
    val state: StateFlow<FestivalListState> = _state

    private val _userState = MutableStateFlow<UserModel?>(null)
    val userState: StateFlow<UserModel?> = _userState

    fun getFestival(sign: String) {
        viewModelScope.launch {
            _state.value = FestivalListState.Loading
            val result = withContext(Dispatchers.IO) {
                Festivalrepository.getFestival(sign)
            }

            if (result != null) {
                _state.value = FestivalListState.SuccessDetail(result)
            } else {
                _state.value = FestivalListState.Error
            }
        }
    }
    sealed class PurchaseState {
        object Idle : PurchaseState()
        object Loading : PurchaseState()
        data class Success(val ticketId: String) : PurchaseState()
        data class Error(val message: String) : PurchaseState()
    }

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState

    fun purchaseTicket(festivalName: String, ticketType: String, userUid: String) {
        viewModelScope.launch {
            _purchaseState.value = PurchaseState.Loading

            val result = withContext(Dispatchers.IO) {
                // Llamamos a la transacción segura que creamos en el Provider
                Festivalrepository.buyTicket(festivalName, ticketType, userUid)
            }

            result.onSuccess { ticketId ->
                _purchaseState.value = PurchaseState.Success(ticketId)
            }.onFailure { exception ->
                _purchaseState.value = PurchaseState.Error(exception.message ?: "Error desconocido")
            }
        }
    }

    fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            // Usamos Dispatchers.IO porque es una consulta a base de datos
            val user = withContext(Dispatchers.IO) {
                Userrepository.getUserById(uid)
            }
            // Actualizamos el estado con el usuario encontrado
            _userState.value = user
        }
    }
    fun toggleFavorite(festivalName: String, userUid: String, isFavorite: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Userrepository.updateUserFavorite(festivalName, userUid, isFavorite)
            }
        }
    }
}