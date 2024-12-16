package com.tuempresa.preguntasapp

import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuempresa.preguntasapp.NavigationItem.EndGameScreen

@Composable
fun Navigation(
    preguntasManager: PreguntasManager,
    puntuacionManager: PuntuacionManager,
    juego: Juego
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.PreguntasScreen,
// Enter from the right
        enterTransition = { slideIn(initialOffset = { fullSize -> IntOffset(fullSize.width, 0) }) },
// Exit to the left
        exitTransition = { slideOut(targetOffset = { fullSize -> IntOffset(-fullSize.width, 0) }) },
// Pop enter from the right
        popEnterTransition = {
            slideIn(initialOffset = { fullSize ->
                IntOffset(
                    -fullSize.width,
                    0
                )
            })
        },
// Pop exit to the left
        popExitTransition = {
            slideOut(targetOffset = { fullSize ->
                IntOffset(
                    fullSize.width,
                    0
                )
            })
        }
    ) {
        composable<NavigationItem.PreguntasScreen> {
            PreguntasScreen(
                preguntasManager,
                puntuacionManager,
                juego,
                onEndGame = { navController.navigate(route = EndGameScreen) })
        }
        composable<EndGameScreen> {
            EndGameScreen()
        }
    }
}
