package com.example.musicfest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.musicfest.data.SessionManager
import com.example.musicfest.databinding.ActivityMainBinding
import com.example.musicfest.domain.model.UserModel
import com.example.musicfest.ui.profile.ProfileActivity
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    private  var selectedUser: UserModel?= null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply{
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top)
            insets
        }
        
        initUI()
        //carga de datos del usuario
        lifecycleScope.launch {
            val user = sessionManager.fetchUserData()
            if (user != null) {
                // Puedes quitar este Toast más adelante, es para que confirmes que funciona
                Toast.makeText(this@MainActivity, "Hola, ${user.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun initUI() {
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        val navGraph = navController.navInflater.inflate(R.navigation.client_graph)
        navGraph.setStartDestination(R.id.homeFragment)
        navController.graph = navGraph
        binding.bottomBar.setupWithNavController(navController)

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        navController.addOnDestinationChangedListener { _, destination, _ -> 
            when(destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.tvToolbarTitle.text = getString(R.string.homeFragmentTitle)
                    binding.tvToolbarSubtitle.visibility = View.VISIBLE
                    binding.tvToolbarSubtitle.text = getString(R.string.homeFragmentSubtitle)
                }
                R.id.picturesFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.tvToolbarTitle.text = getString(R.string.picturesFragmentTitle)
                    binding.tvToolbarSubtitle.visibility = View.GONE
                } 
                R.id.ticketsFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.tvToolbarTitle.text = getString(R.string.ticketsFragmentTitle)
                    binding.tvToolbarSubtitle.visibility = View.GONE
                } 
                R.id.favoritesFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.tvToolbarTitle.text = getString(R.string.favoritesFragmentTitle)
                    binding.tvToolbarSubtitle.visibility = View.GONE
                }
                else -> {
                    binding.toolbar.visibility = View.GONE
                }
            }
        }
    }
}