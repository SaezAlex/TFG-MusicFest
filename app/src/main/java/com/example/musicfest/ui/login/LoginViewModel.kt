package com.example.musicfest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//  posibles estados
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun loginUser(email: String, pass: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // llamada a Firebase de forma asíncrona
                auth.signInWithEmailAndPassword(email, pass).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                // Si falla (contraseña incorrecta,no existe....
                _loginState.value = LoginState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }
}