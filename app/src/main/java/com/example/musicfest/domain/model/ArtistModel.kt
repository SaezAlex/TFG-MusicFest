package com.example.musicfest.domain.model

import com.google.firebase.firestore.PropertyName

data class ArtistModel(
    val name: String = "",
    val bio: String = "",
    val genre: String = "",
    // Usamos PropertyName porque en Kotlin se suele usar camelCase y en tu BD tienes snake_case
    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = ""
)