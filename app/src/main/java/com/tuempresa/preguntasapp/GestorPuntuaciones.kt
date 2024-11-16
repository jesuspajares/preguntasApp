package com.tuempresa.preguntasapp

class GestorPuntuaciones {
    private var puntosEquipo1 = 0
    private var puntosEquipo2 = 0

    fun sumarPuntos(equipo: Int) {
        if (equipo == 1) puntosEquipo1++
        else if (equipo == 2) puntosEquipo2++
    }

    fun obtenerPuntuaciones(): String {
        return "Equipo 1: $puntosEquipo1 | Equipo 2: $puntosEquipo2"
    }

    fun equipoGanador(): String {
        return when {
            puntosEquipo1 > puntosEquipo2 -> "¡Equipo 1 ha ganado!"
            puntosEquipo2 > puntosEquipo1 -> "¡Equipo 2 ha ganado!"
            else -> "¡Es un empate!"
        }
    }
}
