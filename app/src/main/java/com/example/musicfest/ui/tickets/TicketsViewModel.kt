package com.example.musicfest.ui.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicfest.data.FestivalProvider
import com.example.musicfest.domain.model.OrderModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// definidos los estados de la pantalla de tickets
sealed class TicketsState {
    object Loading : TicketsState()
    data class Success(val tickets: List<OrderModel>) : TicketsState()
    object Empty : TicketsState()
    data class Error(val message: String) : TicketsState()
}

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val repository: FestivalProvider
) : ViewModel() {

    private val _state = MutableStateFlow<TicketsState>(TicketsState.Loading)
    val state: StateFlow<TicketsState> = _state

    fun getUserTickets(uid: String) {
        viewModelScope.launch {
            _state.value = TicketsState.Loading

            try {
                val tickets = withContext(Dispatchers.IO) {
                    repository.getUserOrders(uid)
                }

                if (tickets.isEmpty()) {
                    _state.value = TicketsState.Empty
                } else {
                    _state.value = TicketsState.Success(tickets)
                }
            } catch (e: Exception) {
                _state.value = TicketsState.Error(e.message ?: "Error al cargar entradas")
            }
        }
    }
}