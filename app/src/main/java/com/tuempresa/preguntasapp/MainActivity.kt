package com.tuempresa.preguntasapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.WorkbookFactory

class MainActivity : AppCompatActivity() {

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

    private var puntosEquipo1 = 0
    private var puntosEquipo2 = 0

    private var preguntas =
        mutableListOf<Triple<String, String, String>>() // Preguntador, Pregunta, Respuesta
    private var preguntaActual = 0
    private var preguntasRespondidas = mutableSetOf<String>() // Guardar preguntas ya respondidas
    private var aleatoreizar = true

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

        // Recuperar puntuaciones y preguntas respondidas
        val sharedPreferences = getSharedPreferences("puntuaciones", MODE_PRIVATE)
        puntosEquipo1 = sharedPreferences.getInt("puntosEquipo1", 0)
        puntosEquipo2 = sharedPreferences.getInt("puntosEquipo2", 0)
        preguntasRespondidas =
            sharedPreferences.getStringSet("preguntasRespondidas", setOf<String>())?.toMutableSet()
                ?: mutableSetOf<String>()

        textPuntuacion.text = "Puntuacion: $puntosEquipo1 - $puntosEquipo2"

        try {
            leerPreguntasDesdeAssets()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al leer el archivo Excel: ${e.message}")
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("No se pudo cargar el archivo de preguntas. Asegurate de que preguntas.xlsx estaincluido en assets.")
                .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }

        mostrarPreguntaActual()

        btnEquipo1.setOnClickListener {
            puntosEquipo1++
            actualizarPuntuaciones()
            avanzarPregunta()
        }

        btnEquipo2.setOnClickListener {
            puntosEquipo2++
            actualizarPuntuaciones()
            avanzarPregunta()
        }

        btnSiguientePregunta.setOnClickListener {
            avanzarPregunta()
        }

        btnMostrarRespuesta.setOnClickListener {
            mostrarRespuestaEnDialogo()
        }

        btnReiniciar.setOnClickListener {
            aleatoreizar = true
            leerPreguntasDesdeAssets()
            puntosEquipo1 = 0
            puntosEquipo2 = 0
            preguntaActual = 0
            preguntasRespondidas.clear()
            actualizarPuntuaciones()
            mostrarPreguntaActual()
        }

    }

    private fun leerPreguntasDesdeAssets() {
        val assetManager = assets
        val inputStream = assetManager.open("preguntas.xlsx")

        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        // Leer las preguntas (desde la fila 2 en adelante)
        val preguntasList = mutableListOf<Triple<String, String, String>>()
        for (rowIndex in 1 until sheet.lastRowNum + 1) { // Usa lastRowNum en lugar de physicalNumberOfRows
            val row = sheet.getRow(rowIndex) ?: continue // Salta filas nulas

            val preguntador = row.getCell(0)?.stringCellValue ?: "Desconocido"
            val pregunta = row.getCell(1)?.stringCellValue ?: "Sin pregunta"
            val respuesta = row.getCell(2)?.stringCellValue ?: "Sin respuesta"

            if (pregunta.isNotBlank()) { // Asegúrate de que la pregunta no esté vacía
                preguntasList.add(Triple(preguntador, pregunta, respuesta))
            }
        }

        // Mezclar las preguntas de forma aleatoria
        if (aleatoreizar) {
            preguntasList.shuffle()
            aleatoreizar = false
        }

        for ((index, pregunta) in preguntasList.withIndex()) {
            val (preguntador, preguntaTexto, _) = pregunta
            Log.d("PreguntasAleatorias", "Pregunta $index: $preguntaTexto (Respondida por: $preguntador)")
        }

        // Asignar las preguntas a la lista de preguntas
        preguntas = preguntasList

        workbook.close()
    }

    private fun mostrarPreguntaActual() {
        // Verificar si la pregunta ya ha sido respondida
        // TODO: Comprobar que preguntasRespondidas está bien definida porque con la primera entra y no debería
        if (preguntaActual < preguntas.size && preguntasRespondidas.contains(preguntaActual.toString())) {
            avanzarPregunta() // Si ya se ha respondido, avanzar a la siguiente
            return
        }

        if (preguntaActual < preguntas.size) {
            Log.d("PreguntaActual", "Mostrando la pregunta en el índice: $preguntaActual")
            val (preguntador, pregunta, _) = preguntas[preguntaActual]
            textPreguntador.text = "Preguntador: $preguntador"
            textPregunta.text = pregunta
            textPreguntasRestantes.text =
                "Preguntas restantes: ${preguntas.size - preguntaActual}"
        } else {
            textPregunta.text = "¡Fin del juego!"
            textPreguntador.text = ""
            textPreguntasRestantes.text = "No quedan preguntas."
        }
    }


    private fun avanzarPregunta() {
        preguntaActual++
        if (preguntaActual < preguntas.size) {
            mostrarPreguntaActual()
        } else {
            AlertDialog.Builder(this)
                .setTitle("¡Juego terminado!")
                .setMessage("No quedan más preguntas. Puntuación final:\nEquipo 1: $puntosEquipo1\nEquipo 2: $puntosEquipo2")
                .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }


    private fun actualizarPuntuaciones() {
        progressEquipo1.progress = puntosEquipo1
        progressEquipo2.progress = puntosEquipo2
        textPuntuacion.text = "Puntuación: $puntosEquipo1 - $puntosEquipo2"

        // Guardar puntuaciones y el índice de la pregunta actual
        val sharedPreferences = getSharedPreferences("puntuaciones", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("puntosEquipo1", puntosEquipo1)
        editor.putInt("puntosEquipo2", puntosEquipo2)
        editor.putInt("preguntaActual", preguntaActual)

        // Guardar las preguntas respondidas
        preguntasRespondidas.add(preguntaActual.toString())
        editor.putStringSet("preguntasRespondidas", preguntasRespondidas)
        editor.apply()
    }

    private fun mostrarRespuestaEnDialogo() {
        // Asegurarse de que la pregunta actual esté disponible
        if (preguntaActual < preguntas.size) {
            val (_, _, respuesta) = preguntas[preguntaActual]

            // Crear y mostrar el cuadro de diálogo con la respuesta
            AlertDialog.Builder(this)
                .setTitle("Respuesta")
                .setMessage(respuesta)
                .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}