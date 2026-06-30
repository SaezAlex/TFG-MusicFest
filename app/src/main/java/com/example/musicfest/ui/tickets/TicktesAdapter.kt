package com.example.musicfest.ui.tickets.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicfest.databinding.ItemTicketBinding
import com.example.musicfest.domain.model.OrderModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class TicketsAdapter(
    private var ticketsList: List<OrderModel> = emptyList()
) : RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    // funcion para actualizar la lista cuando lleguen los datos de Firestore
    fun updateTickets(newList: List<OrderModel>) {
        ticketsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(ticketsList[position])
    }

    override fun getItemCount(): Int = ticketsList.size

    inner class TicketViewHolder(private val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderModel) {
            // Pintamos los textos
            binding.tvFestivalNameTicket.text = order.festivalName
            binding.tvTicketType.text = "TIPO: ${order.ticketType.uppercase()}"

            // generar el codigo QR usando el ID del pedido
            try {
                // Si el orderId está vacío , no intentamos generar el QR
                if (order.orderId.isNotEmpty()) {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                        order.orderId,
                        BarcodeFormat.QR_CODE,
                        400, // Ancho en píxeles
                        400  // Alto en píxeles
                    )
                    binding.ivQrCode.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}