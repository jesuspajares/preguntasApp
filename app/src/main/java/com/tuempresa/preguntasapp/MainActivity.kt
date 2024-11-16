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

    private val preguntas = mutableListOf<Triple<String, String, String>>() // Preguntador, Pregunta, Respuesta
    private var preguntaActualIndex = 1
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
        preguntasRespondidas = sharedPreferences.getStringSet("preguntasRespondidas", setOf<String>())?.toMutableSet() ?: mutableSetOf<String>()

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
            puntosEquipo1 = 0
            puntosEquipo2 = 0
            preguntaActualIndex = 0
            preguntasRespondidas.clear()  // Limpia las preguntas respondidas
            actualizarPuntuaciones()
            mostrarPreguntaActual()  // Asegúrate de que muestra la primera pregunta
        }

    }

    private fun leerPreguntasDesdeAssets() {
        val assetManager = assets
        val inputStream = assetManager.open("preguntas.xlsx")

        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        // Leer la cabecera
        val cabecera = sheet.getRow(0)

        // Leer las preguntas
        val preguntasList = mutableListOf<Triple<String, String, String>>()
        for (row in sheet) {
            if (row.rowNum == 0) continue // Saltar la cabecera
            val preguntador = row.getCell(0)?.stringCellValue ?: "Desconocido"
            val pregunta = row.getCell(1)?.stringCellValue ?: "Sin pregunta"
            val respuesta = row.getCell(2)?.stringCellValue ?: "Sin respuesta"

            // Agregar la pregunta a la lista
            preguntasList.add(Triple(preguntador, pregunta, respuesta))
        }

        // Mezclar las preguntas de forma aleatoria
        if (aleatoreizar) {
            preguntasList.shuffle()
        }

        // Asignar las preguntas a la lista de preguntas
        preguntas = preguntasList

        workbook.close()
    }


    private fun mostrarPreguntaActual() {
        // Verificar si la pregunta ya ha sido respondida
        if (preguntasRespondidas.contains(preguntaActualIndex.toString())) {
            avanzarPregunta()
            return
        }

        if (preguntaActualIndex < preguntas.size) {
            val (preguntador, pregunta, _) = preguntas[preguntaActualIndex]
            textPreguntador.text = "Preguntador: $preguntador"
            textPregunta.text = pregunta
            textPreguntasRestantes.text =
                "Preguntas restantes: ${preguntas.size - preguntaActualIndex - 1}"
        } else {
            textPregunta.text = "¡Fin del juego!"
            textPreguntador.text = ""
            textPreguntasRestantes.text = "No quedan preguntas."
        }
    }

    private fun avanzarPregunta() {
        if (preguntaActualIndex < preguntas.size - 1) {
            preguntaActualIndex++
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
        editor.putInt("preguntaActualIndex", preguntaActualIndex)

        // Guardar las preguntas respondidas
        preguntasRespondidas.add(preguntaActualIndex.toString())
        editor.putStringSet("preguntasRespondidas", preguntasRespondidas)
        editor.apply()
    }

    private fun mostrarRespuestaEnDialogo() {
        // Asegurarse de que la pregunta actual esté disponible
        if (preguntaActualIndex < preguntas.size) {
            val (_, _, respuesta) = preguntas[preguntaActualIndex]

            // Crear y mostrar el cuadro de diálogo con la respuesta
            AlertDialog.Builder(this)
                .setTitle("Respuesta")
                .setMessage(respuesta)
                .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

}
