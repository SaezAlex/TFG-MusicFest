package com.example.musicfest.domain.model

data class OrderModel(
    @com.google.firebase.firestore.DocumentId
    val orderId: String = "", // Firestore meterá aquí el ID del documento automáticamente para hacer el QR
    val festivalName: String = "",
    val ticketType: String = "",
    val status: String = "",
    val userUid: String = ""
)