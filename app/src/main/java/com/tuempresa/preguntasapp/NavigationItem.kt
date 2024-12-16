package com.tuempresa.preguntasapp

import kotlinx.serialization.Serializable

internal sealed interface NavigationItem {
    @Serializable
    data object PreguntasScreen : NavigationItem

    @Serializable
    data object EndGameScreen : NavigationItem
}