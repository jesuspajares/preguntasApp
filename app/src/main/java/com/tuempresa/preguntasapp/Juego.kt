package com.tuempresa.preguntasapp

class Juego {
    var puntosEquipo1 = 0
    var puntosEquipo2 = 0
    var preguntaActual = 0
    var preguntasRespondidas = mutableSetOf<String>()
    var preguntas = mutableListOf<Triple<String, String, String>>()

    fun esJuegoNuevo(): Boolean {
        return puntosEquipo1 == 0 && puntosEquipo2 == 0 && preguntaActual == 0
    }

    fun mostrarPreguntaActual(): Triple<String, String, String>? {
        if (preguntaActual < preguntas.size) {
            if (preguntasRespondidas.contains(preguntaActual.toString())) {
                return null // Pregunta ya respondida
            }
            return preguntas[preguntaActual]
        }
        return null
    }

    fun avanzarPregunta(): Boolean {
        preguntaActual++
        if (preguntaActual >= preguntas.size) {
            return false // No hay más preguntas
        }
        return true
    }

    fun resetJuego() {
        puntosEquipo1 = 0
        puntosEquipo2 = 0
        preguntaActual = 0
        preguntasRespondidas.clear()
    }
}
