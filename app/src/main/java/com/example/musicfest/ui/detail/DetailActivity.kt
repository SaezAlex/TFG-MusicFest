package com.example.musicfest.ui.detail

// Intents y las URIs
import android.content.Intent
import android.net.Uri

// Android Base & UI
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.example.musicfest.data.SessionManager
import com.example.musicfest.R

// View Binding & Resources
import com.example.musicfest.databinding.ActivityDetailBinding
import com.example.musicfest.domain.model.FestivalModel
import com.example.musicfest.ui.home.FestivalListState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()

    private val festivalViewModel: FestivalDetailViewModel by viewModels()
    private var isFavorite = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.detail) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        initListener()

        // Iniciamos la carga de datos
        val currentUid = sessionManager.currentUid
        if (currentUid != null) {
            festivalViewModel.loadUserProfile(currentUid) // Carga los favoritos del usuario
        }
        festivalViewModel.getFestival(args.name)
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                festivalViewModel.state.collect { state ->
                    when (state) {
                        is FestivalListState.SuccessDetail -> {
                            // COMPROBACIÓN: Antes de pintar, miramos si el usuario ya lo tiene en favoritos
                            val user = festivalViewModel.userState.value
                            isFavorite = user?.likedFestivals?.contains(state.festival.name) == true

                            bindFestivalData(state.festival)
                        }
                        is FestivalListState.Error -> {
                            Toast.makeText(this@DetailActivity, "Error al cargar el festival", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }

        // Lógica de compra (Se queda igual)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                festivalViewModel.purchaseState.collect { state ->
                    when (state) {
                        is FestivalDetailViewModel.PurchaseState.Idle -> {
                            binding.btnAction.isEnabled = true
                            binding.btnAction.text = "Ver Disponibilidad"
                        }
                        is FestivalDetailViewModel.PurchaseState.Loading -> {
                            binding.btnAction.isEnabled = false
                            binding.btnAction.text = "Procesando..."
                        }
                        is FestivalDetailViewModel.PurchaseState.Success -> {
                            binding.btnAction.isEnabled = true
                            binding.btnAction.text = "¡Comprado!"
                            showQRCodeDialog(state.ticketId)
                        }
                        is FestivalDetailViewModel.PurchaseState.Error -> {
                            binding.btnAction.isEnabled = true
                            binding.btnAction.text = "Ver Disponibilidad"
                            val errorMsg = if (state.message == "SOLD_OUT") "¡Entradas agotadas!" else "Error: ${state.message}"
                            Toast.makeText(this@DetailActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindFestivalData(festival: FestivalModel) {
        binding.tvFestivalName.text = festival.name
        binding.tvCity.text = festival.city
        binding.tvStatus.text = "Estado: ${festival.status}"
        binding.tvGenresList.text = festival.genres.joinToString(", ")

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvDateStart.text = "Inicia: ${dateFormatter.format(festival.dateStart)}"

        // --- Lógica Visual Favorito ---
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.heart)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.empty_heart)
        }

        favouriteFestival(festival)

        binding.btnTransport.setOnClickListener {
            val latitude = festival.location.latitude
            val longitude = festival.location.longitude
            val gmmIntentUri = Uri.parse("geo:0,0?q=$latitude,$longitude(${festival.name})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(mapIntent)
        }

        binding.btnAccommodation.setOnClickListener {
            val bookingDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val checkin = bookingDateFormatter.format(festival.dateStart)
            val checkout = bookingDateFormatter.format(festival.dateEnd)
            val url = "https://www.booking.com/searchresults.html?ss=${festival.city}&checkin=$checkin&checkout=$checkout"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        binding.btnAction.setOnClickListener {
            val currentUid = sessionManager.currentUid
            if (currentUid != null) {
                festivalViewModel.purchaseTicket(festival.name, "general", currentUid)
            }
        }
    }

    private fun favouriteFestival(festival: FestivalModel) {
        binding.btnFavorite.setOnClickListener {
            val currentUid = sessionManager.currentUid

            if (currentUid != null) {
                isFavorite = !isFavorite
                festivalViewModel.toggleFavorite(festival.name, currentUid, isFavorite)

                // IMPORTANTE: Actualizamos la UI localmente para que cambie el icono
                if (isFavorite) {
                    binding.btnFavorite.setImageResource(R.drawable.heart)
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.empty_heart)
                }

                Toast.makeText(this, if(isFavorite) "Añadido a favoritos" else "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Inicia sesión primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showQRCodeDialog(ticketId: String) {
        try {
            val barcodeEncoder = com.journeyapps.barcodescanner.BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(ticketId, com.google.zxing.BarcodeFormat.QR_CODE, 600, 600)
            val imageView = android.widget.ImageView(this).apply {
                setImageBitmap(bitmap)
                setPadding(40, 40, 40, 40)
            }
            MaterialAlertDialogBuilder(this)
                .setTitle("¡Entrada Confirmada!")
                .setMessage("Guarda este código QR para acceder al recinto.")
                .setView(imageView)
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo generar el QR", Toast.LENGTH_SHORT).show()
        }
    }
}