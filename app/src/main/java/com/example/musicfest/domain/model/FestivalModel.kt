package com.example.musicfest.domain.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class FestivalModel(
    var name: String = "",
    
    @get:PropertyName("date_start") @set:PropertyName("date_start") @field:PropertyName("date_start")
    var dateStart: Date = Date(),

    @get:PropertyName("date_end") @set:PropertyName("date_end") @field:PropertyName("date_end")
    var dateEnd: Date = Date(),
    
    var genres: List<String> = listOf(),
    
    var city: String = "",
    
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    
    var status: String = "",
    
    @get:Exclude @set:Exclude @field:Exclude
    var ticketTypes: Map<String, Double> = emptyMap()
){
    @get:Exclude
    val musicGenres: List<MusicGenre>
        get() = genres.mapNotNull { MusicGenre.valueOf(it) }
}
