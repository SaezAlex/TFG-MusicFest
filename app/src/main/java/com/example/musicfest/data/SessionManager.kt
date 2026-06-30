package com.example.musicfest.data

import com.example.musicfest.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {
    // estado reactivo que contiene al usuario actual (null si no ha cargado o no está logueado)
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser

    // devuelve el UID de Firebase Auth dirrectamente si se necesita de forma síncrona
    val currentUid: String?
        get() = auth.currentUser?.uid

    // carga los datos desde Firestore y los guarda en memoria
    suspend fun fetchUserData(): UserModel? {
        val uid = currentUid ?: return null
        val profile = userRepository.getUserProfile(uid)
        _currentUser.value = profile
        return profile
    }

    // limpia la sesión al cerrar el perfil
    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}