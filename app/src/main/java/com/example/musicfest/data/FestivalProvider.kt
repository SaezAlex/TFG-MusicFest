package com.example.musicfest.data

import com.example.musicfest.domain.model.ArtistModel
import com.example.musicfest.domain.model.FestivalModel
import com.example.musicfest.domain.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FestivalProvider @Inject constructor(private val firestore: FirebaseFirestore) {
    /**
     * Función que accede a firestore, obtiene la colección "festivals", hace una lista de objetos FestivalModel y la devuelve
     */
    suspend fun getFestivals(): List<FestivalModel> {
        val snapshot = firestore
            .collection("festivals")
            .limit(20)
            .get()
            .await()
        
        val festivals = snapshot.documents.map { document ->
            val festival = document.toObject(FestivalModel::class.java)!!
            
            val ticketTypesSnapshot = document.reference
                .collection("ticket_types")
                .get()
                .await()
            
            festival.ticketTypes = ticketTypesSnapshot.documents.associate { ticketDoc ->
                ticketDoc.id to (ticketDoc.getDouble("price") ?: 0.00)
            }
            festival
        }
        return festivals
    }

    suspend fun getFestival(name: String): FestivalModel? {

        val querySnapshot = firestore
            .collection("festivals")
            .whereEqualTo("name", name)
            .limit(1)
            .get()
            .await()


        if (querySnapshot.isEmpty) return null


        val documentSnapshot = querySnapshot.documents.first()
        val festival = documentSnapshot.toObject(FestivalModel::class.java)!!


        val ticketTypesSnapshot = documentSnapshot.reference
            .collection("ticket_types")
            .get()
            .await()

        festival.ticketTypes = ticketTypesSnapshot.documents.associate { ticketDoc ->
            ticketDoc.id to (ticketDoc.getDouble("price") ?: 0.00)
        }

        return festival
    }

    suspend fun buyTicket(festivalName: String, ticketType: String, userUid: String): Result<String> {
        return try {
            // buscar el festival por su nombre para obtener su Referencia (ID real de Firestore)
            val querySnapshot = firestore
                .collection("festivals")
                .whereEqualTo("name", festivalName)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) throw Exception("Festival no encontrado")

            val festivalRef = querySnapshot.documents.first().reference
            // referencia al tipo de ticket
            val ticketRef = festivalRef.collection("ticket_types").document(ticketType)

            // ejecutae  transacción segura
            val ticketId = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(ticketRef)

                //he añadido este campo (en los dos primeros festivales)
                val currentStock = snapshot.getLong("available_quantity") ?: 0L

                if (currentStock > 0) {
                    // Restar 1 al stock
                    transaction.update(ticketRef, "available_quantity", currentStock - 1)

                    // si se agotan, marcamos el festival como sold_out
                    if (currentStock - 1L == 0L) {
                        transaction.update(festivalRef, "status", "sold_out")
                    }

                    // generar el documento del pedido (Order)
                    val newTicketRef = firestore.collection("orders").document()
                    val orderData = hashMapOf(
                        "festivalName" to festivalName,
                        "ticketType" to ticketType,
                        "status" to "valid",
                        "userUid" to userUid // <- Aquí podrías enlazarlo al usuario logueado
                    )
                    transaction.set(newTicketRef, orderData)

                    // Devolvemos el ID de la entrada generada
                    newTicketRef.id
                } else {
                    throw Exception("SOLD_OUT")
                }
            }.await()

            Result.success(ticketId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getInitialSearchResults(): List<FestivalModel> {
        val snapshot = firestore
            .collection("festivals")
            .limit(10)
            .get()
            .await()
        
        val festivals = snapshot.documents.map { document ->
            val festival = document.toObject(FestivalModel::class.java)!!
            
            val ticketTypesSnapshot = document.reference
                .collection("ticket_types")
                .get()
                .await()
            
            festival.ticketTypes = ticketTypesSnapshot.documents.associate { ticketDoc ->
                ticketDoc.id to (ticketDoc.getDouble("price") ?: 0.00)
            }
            festival
        }
        return festivals
    }
    suspend fun getUserOrders(uid: String): List<OrderModel> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userUid", uid)
                .get()
                .await()

            // covertirr los documentos a nuestra clase OrderModel
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderModel::class.java)
            }
        } catch (e: Exception) {
            emptyList() // si hay error, devolvemos lista vacía
        }
    }
    suspend fun search(query: String): List<FestivalModel> {
        val snapshot = firestore
            .collection("festivals")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        
        val festivals = snapshot.documents.map { document ->
            val festival = document.toObject(FestivalModel::class.java)!!
            
            val ticketTypesSnapshot = document.reference
                .collection("ticket_types")
                .get()
                .await()
            
            festival.ticketTypes = ticketTypesSnapshot.documents.associate { ticketDoc ->
                ticketDoc.id to (ticketDoc.getDouble("price") ?: 0.00)
            }
            festival
        }
        return festivals
    }

    // obtener los artistas ordenados alfabeticamente
    suspend fun getArtists(): List<ArtistModel> {
        return try {
            val snapshot = firestore.collection("artists")
                .orderBy("name")
                .get()
                .await()

            snapshot.toObjects(ArtistModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun searchWithFilters(genres: List<String>): List<FestivalModel> {
        var snapshot = firestore
            .collection("festivals")
            .orderBy("name")
        
        if (genres.isNotEmpty()) {
            snapshot = snapshot
                .whereArrayContainsAny("genres", genres)
        }
        
        snapshot
            .get()
            .await()
        
        return snapshot
            .get()
            .await()
            .toObjects(FestivalModel::class.java)
    }
}