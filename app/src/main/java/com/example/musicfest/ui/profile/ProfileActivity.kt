package com.example.musicfest.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.musicfest.R
import com.example.musicfest.data.SessionManager
import com.example.musicfest.databinding.ActivityProfileBinding
import com.example.musicfest.domain.model.UserModel
import com.example.musicfest.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding


    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfileBack.setOnClickListener {
            finish()
        }

        observeUserData()
        setupLogout()
    }

    private fun observeUserData() {
        // Observamos el StateFlow del usuario
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                if (user != null) {

                    binding.tvName.text = "${user.name} ${user.surname}"
                    binding.tvUserName.text=user.uid
                    // Cargamos la imagen con Glide
                    if (user.image_url.isNotEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(user.image_url)
                            .placeholder(R.mipmap.ic_launcher) // imagen por defecto mientras carga
                            .circleCrop() // para que la imagen salga borobil
                            .into(binding.ivProfile)
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupLogout() {

        binding.cardLogout.setOnClickListener {
            sessionManager.logout()

            // Redirigimos al Login y limpiamos el historial de navegación
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}