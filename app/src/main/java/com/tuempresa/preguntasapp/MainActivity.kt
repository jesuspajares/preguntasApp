package com.tuempresa.preguntasapp

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private lateinit var juego: Juego
    private lateinit var preguntasManager: PreguntasManager
    private lateinit var puntuacionManager: PuntuacionManager

    private lateinit var textPregunta: TextView
    private lateinit var textPreguntador: TextView
    private lateinit var textPreguntasRestantes: TextView
    private lateinit var btnEquipo1: Button
    private lateinit var btnEquipo2: Button
    private lateinit var btnMostrarRespuesta: Button
    private lateinit var btnSiguientePregunta: Button
    private lateinit var btnReiniciar: Button
    private lateinit var progressEquipo1: ProgressBar
    private lateinit var progressEquipo2: ProgressBar
    private lateinit var textPuntuacion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vinculamos las vistas
        textPregunta = findViewById(R.id.textPregunta)
        textPreguntador = findViewById(R.id.textPreguntador)
        textPreguntasRestantes = findViewById(R.id.textPreguntasRestantes)
        btnEquipo1 = findViewById(R.id.btnEquipo1)
        btnEquipo2 = findViewById(R.id.btnEquipo2)
        btnMostrarRespuesta = findViewById(R.id.btnMostrarRespuesta)
        btnSiguientePregunta = findViewById(R.id.btnSiguiente)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        progressEquipo1 = findViewById(R.id.progressEquipo1)
        progressEquipo2 = findViewById(R.id.progressEquipo2)
        textPuntuacion = findViewById(R.id.textPuntuacion)

        // Inicializar las clases
        preguntasManager = PreguntasManager(this)
        puntuacionManager = PuntuacionManager(this)
        juego = Juego()

        // Cargar puntuaciones y preguntas
        puntuacionManager.cargarPuntuaciones(juego)
        if (juego.preguntas.isEmpty()) {
            juego.preguntas = preguntasManager.leerPreguntasDesdeAssets()
        }

        actualizarPuntuaciones()
        mostrarPreguntaActual()

        btnEquipo1.setOnClickListener {
            juego.puntosEquipo1++
            actualizarPuntuaciones()
            juego.preguntasRespondidas.add(juego.preguntaActual.toString())
            if (!juego.avanzarPregunta()) {
                mostrarFinDeJuego()
            } else {
                mostrarPreguntaActual()
            }
        }

        btnEquipo2.setOnClickListener {
            juego.puntosEquipo2++
            actualizarPuntuaciones()
            juego.preguntasRespondidas.add(juego.preguntaActual.toString())
            if (!juego.avanzarPregunta()) {
                mostrarFinDeJuego()
            } else {
                mostrarPreguntaActual()
            }
        }

        btnSiguientePregunta.setOnClickListener {
            juego.preguntasRespondidas.add(juego.preguntaActual.toString())
            if (!juego.avanzarPregunta()) {
                mostrarFinDeJuego()
            } else {
                mostrarPreguntaActual()
            }
        }

        btnMostrarRespuesta.setOnClickListener {
            mostrarRespuestaEnDialogo()
        }

        btnReiniciar.setOnClickListener {
            juego.resetJuego()
            juego.preguntas = preguntasManager.leerPreguntasDesdeAssets()
            actualizarPuntuaciones()
            habilitarBotones()
            mostrarPreguntaActual()
        }
    }

    override fun onPause() {
        super.onPause()
        puntuacionManager.guardarPuntuaciones(juego)
    }

    private fun mostrarPreguntaActual() {
        val pregunta = juego.mostrarPreguntaActual()
        if (pregunta != null) {
            val (preguntador, preguntaTexto, _) = pregunta
            textPreguntador.text = "Preguntador: $preguntador"
            textPregunta.text = preguntaTexto
            textPreguntasRestantes.text = "Preguntas restantes: ${juego.preguntas.size - juego.preguntaActual}"
        } else if (juego.preguntaActual >= juego.preguntas.size) {
            mostrarFinDeJuego()
        }
    }

    private fun actualizarPuntuaciones() {
        progressEquipo1.progress = juego.puntosEquipo1
        progressEquipo2.progress = juego.puntosEquipo2
        textPuntuacion.text = "Puntuación: ${juego.puntosEquipo1} - ${juego.puntosEquipo2}"
    }

    private fun mostrarRespuestaEnDialogo() {
        val pregunta = juego.mostrarPreguntaActual()
        if (pregunta != null) {
            val (_, _, respuesta) = pregunta
            AlertDialog.Builder(this)
                .setTitle("Respuesta")
                .setMessage(respuesta)
                .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun mostrarFinDeJuego() {
        AlertDialog.Builder(this)
            .setTitle("¡Juego terminado!")
            .setMessage("No quedan más preguntas. Puntuación final:\nEquipo 1: ${juego.puntosEquipo1}\nEquipo 2: ${juego.puntosEquipo2}")
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .show()
        textPregunta.text = "¡Fin del juego!"
        textPreguntador.text = ""
        textPreguntasRestantes.text = "No quedan preguntas."
        deshabilitarBotones()
    }

    private fun habilitarBotones() {
        btnEquipo1.isVisible = true
        btnEquipo2.isVisible = true
        btnMostrarRespuesta.isVisible = true
        btnSiguientePregunta.isVisible = true
    }

    private fun deshabilitarBotones() {
        btnEquipo1.isVisible = false
        btnEquipo2.isVisible = false
        btnMostrarRespuesta.isVisible = false
        btnSiguientePregunta.isVisible = false
    }
}
