package com.example.musicfest.domain.model

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val surname: String = "",
    val image_url: String = "",
    val likedFestivals: List<String> = emptyList()
)