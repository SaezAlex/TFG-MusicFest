package com.example.musicfest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase base de la aplicación que actúa como el contenedor principal de dependencias.
 * * @property HiltAndroidApp Genera el componente base de Dagger Hilt para la inyección
 * de dependencias en toda la jerarquía del proyecto.
 */
@HiltAndroidApp
class App : Application() {
}