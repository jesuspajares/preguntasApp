package com.tuempresa.preguntasapp

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class JuegoTest {

    private lateinit var juego: Juego

    @Before
    fun setUp() {
        juego = Juego()
        juego.preguntas = mutableListOf(
            Triple("Preguntador1", "Pregunta1", "Respuesta1"),
            Triple("Preguntador2", "Pregunta2", "Respuesta2"),
            Triple("Preguntador3", "Pregunta3", "Respuesta3")
        )
    }

    @Test
    fun esJuegoNuevo() {
        assertTrue(juego.esJuegoNuevo())
        juego.puntosEquipo1 = 1
        assertFalse(juego.esJuegoNuevo())
    }

    @Test
    fun mostrarPreguntaActual() {
        val pregunta = juego.mostrarPreguntaActual()
        assertNotNull(pregunta)
        assertEquals("Pregunta1", pregunta?.second)
    }

    @Test
    fun avanzarPregunta() {
        assertTrue(juego.avanzarPregunta())
        assertEquals(1, juego.preguntaActual)
        assertTrue(juego.avanzarPregunta())
        assertEquals(2, juego.preguntaActual)
        assertFalse(juego.avanzarPregunta())
        assertEquals(3, juego.preguntaActual)
    }

    @Test
    fun resetJuego() {
        juego.puntosEquipo1 = 5
        juego.puntosEquipo2 = 3
        juego.preguntaActual = 2
        juego.preguntasRespondidas.add("1")

        juego.resetJuego()

        assertEquals(0, juego.puntosEquipo1)
        assertEquals(0, juego.puntosEquipo2)
        assertEquals(0, juego.preguntaActual)
        assertTrue(juego.preguntasRespondidas.isEmpty())
    }
}
