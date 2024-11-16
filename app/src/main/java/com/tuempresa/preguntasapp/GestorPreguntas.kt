package com.tuempresa.preguntasapp

class GestorPreguntas(private val preguntas: List<Pregunta>) {
    private var indiceActual = 0

    val preguntaActual: Pregunta?
        get() = if (indiceActual < preguntas.size) preguntas[indiceActual] else null

    val preguntasRestantes: Int
        get() = preguntas.size - indiceActual - 1

    fun siguientePregunta(): Pregunta? {
        indiceActual++
        return preguntaActual
    }

    fun barajarPreguntas(): List<Pregunta> {
        return preguntas.shuffled()
    }
}