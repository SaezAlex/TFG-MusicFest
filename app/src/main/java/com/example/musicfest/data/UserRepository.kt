package com.example.musicfest.data

import com.example.musicfest.domain.model.UserModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // obtener los datos del usuario autenticado por su UID
    suspend fun getUserProfile(uid: String): UserModel? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                // extraert los campos tal y como están en  Firestore
                val name = document.getString("name") ?: ""
                val surname = document.getString("surname") ?: ""
                val imageUrl = document.getString("image_url") ?: ""

                // mapeamos la lista de referencias/strings de los festivales favs
                val likedFestivals = document.get("liked_festivals") as? List<String> ?: emptyList()

                UserModel(uid, name, surname, imageUrl, likedFestivals)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserById(uid: String): UserModel? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()

            if (document.exists()) {
                val name = document.getString("name") ?: ""
                val surname = document.getString("surname") ?: ""
                val imageUrl = document.getString("image_url") ?: ""

                // Obtener como lista genérica para evitar el ClassCastException
                val rawList = document.get("liked_festivals") as? List<*> ?: emptyList<Any>()

                // Convertir de forma segura a List<String>
                val likedFestivals = rawList.mapNotNull {
                    when (it) {
                        is String -> it
                        is DocumentReference -> it.id
                        else -> null
                    }
                }

                UserModel(uid, name, surname, imageUrl, likedFestivals)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserFavorite(festivalName: String, userUid: String, isFavorite: Boolean) {
        try {
            // Referencia al documento del usuario
            val userRef = firestore.collection("users").document(userUid)

            if (isFavorite) {
                // "Mete el festival en el array" si no existe ya
                userRef.update(
                    "liked_festivals",
                    com.google.firebase.firestore.FieldValue.arrayUnion(festivalName)
                ).await()
            } else {
                // "Saca el festival del array"
                userRef.update(
                    "liked_festivals",
                    com.google.firebase.firestore.FieldValue.arrayRemove(festivalName)
                ).await()
            }
        } catch (e: Exception) {
            // Loguear el error para saber si falla la conexión o los permisos
            e.printStackTrace()
        }
    }
}