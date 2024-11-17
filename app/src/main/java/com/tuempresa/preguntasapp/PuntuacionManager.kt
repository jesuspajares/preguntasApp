package com.tuempresa.preguntasapp

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PuntuacionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("puntuaciones", Context.MODE_PRIVATE)

    fun guardarPuntuaciones(juego: Juego) {
        val editor = sharedPreferences.edit()
        editor.putInt("puntosEquipo1", juego.puntosEquipo1)
        editor.putInt("puntosEquipo2", juego.puntosEquipo2)
        editor.putInt("preguntaActual", juego.preguntaActual)

        // Guardar las preguntas respondidas
        val preguntasRespondidasSet = juego.preguntasRespondidas.toMutableSet()
        editor.putStringSet("preguntasRespondidas", preguntasRespondidasSet)

        // Convertir la lista de preguntas a JSON y guardarla
        val gson = Gson()
        val preguntasJson = gson.toJson(juego.preguntas)
        Log.d("PuntuacionManager", "Guardando preguntas: $preguntasJson")
        editor.putString("preguntas", preguntasJson)

        editor.apply()
    }

    fun cargarPuntuaciones(juego: Juego) {
        juego.puntosEquipo1 = sharedPreferences.getInt("puntosEquipo1", 0)
        juego.puntosEquipo2 = sharedPreferences.getInt("puntosEquipo2", 0)
        juego.preguntaActual = sharedPreferences.getInt("preguntaActual", 0)
        juego.preguntasRespondidas = sharedPreferences.getStringSet("preguntasRespondidas", setOf())?.toMutableSet() ?: mutableSetOf()

        val preguntasJson = sharedPreferences.getString("preguntas", null)
        Log.d("PuntuacionManager", "Cargando preguntas: $preguntasJson")
        if (!preguntasJson.isNullOrEmpty()) {
            val gson = Gson()
            val tipo = object : TypeToken<MutableList<Triple<String, String, String>>>() {}.type
            juego.preguntas = gson.fromJson(preguntasJson, tipo)
        } else {
            // Cargar preguntas desde assets si no hay preguntas guardadas
            val preguntasManager = PreguntasManager(context)
            juego.preguntas = preguntasManager.leerPreguntasDesdeAssets()
            Log.d("PuntuacionManager", "Preguntas cargadas desde assets: ${juego.preguntas}")
        }
    }
}
