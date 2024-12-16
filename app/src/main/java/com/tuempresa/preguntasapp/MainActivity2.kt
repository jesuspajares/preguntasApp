package com.tuempresa.preguntasapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity2 : ComponentActivity() {
    private lateinit var juego: Juego
    private lateinit var preguntasManager: PreguntasManager
    private lateinit var puntuacionManager: PuntuacionManager

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preguntasManager = PreguntasManager(this)
        puntuacionManager = PuntuacionManager(this)
        juego = Juego()
        enableEdgeToEdge()
        setContent {
            installSplashScreen()
            Navigation(preguntasManager, puntuacionManager, juego)
        }
    }
}