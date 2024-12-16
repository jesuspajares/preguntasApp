package com.tuempresa.preguntasapp

import kotlinx.serialization.Serializable

internal sealed interface NavigationItem {
    @Serializable
    data object PreguntasScreen : NavigationItem

    @Serializable
    data class EndGameScreen(val ganador: String) : NavigationItem
}