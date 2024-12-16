package com.tuempresa.preguntasapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntasScreen(
    preguntasManager: PreguntasManager,
    puntuacionManager: PuntuacionManager,
    juego: Juego,
    onEndGame: () -> Unit
) {
    // Cargar puntuaciones y preguntas
    puntuacionManager.cargarPuntuaciones(juego)
    if (juego.preguntas.isEmpty()) {
        juego.preguntas = preguntasManager.leerPreguntasDesdeAssets()
    }

    // Cargar puntuaciones y preguntas
    puntuacionManager.cargarPuntuaciones(juego)
    if (juego.preguntas.isEmpty()) {
        juego.preguntas = preguntasManager.leerPreguntasDesdeAssets()
    }

    var currentProgressTeamOne by rememberSaveable { mutableFloatStateOf(0f) }
    var currentProgressTeamTwo by rememberSaveable { mutableFloatStateOf(0f) }
    var preguntador by rememberSaveable { mutableStateOf("") }
    var preguntasRestantes by rememberSaveable { mutableStateOf("") }
    var preguntaActual by rememberSaveable { mutableStateOf("") }
    var respuestaPreguntaActual by rememberSaveable { mutableStateOf("") }
    var puntuaciones by rememberSaveable { mutableStateOf("") }
    var showAnswer by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    RestartButton(onRestart = {
                        juego.resetJuego()
                        juego.preguntas.shuffle()
                        actualizarPuntuaciones(
                            juego = juego,
                            mostrarPuntuaciones = { puntuaciones = it },
                            mostrarProgresosEquipos = { equipo1, equipo2 ->
                                currentProgressTeamOne = equipo1 / 100f
                                currentProgressTeamTwo = equipo2 / 100f
                            })
                        mostrarPreguntaActual(
                            juego = juego,
                            mostrarPreguntador = { preguntador = it },
                            mostrarPregunta = { preguntaActual = it },
                            mostrarPreguntasRestantes = { preguntasRestantes = it },
                            finDeJuego = onEndGame
                        )
                    })
                })
        }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                actualizarPuntuaciones(
                    juego = juego,
                    mostrarPuntuaciones = { puntuaciones = it },
                    mostrarProgresosEquipos = { equipo1, equipo2 ->
                        currentProgressTeamOne = equipo1 / 100f
                        currentProgressTeamTwo = equipo2 / 100f
                    })
                mostrarPreguntaActual(
                    juego = juego,
                    mostrarPreguntador = { preguntador = it },
                    mostrarPregunta = { preguntaActual = it },
                    mostrarPreguntasRestantes = { preguntasRestantes = it },
                    finDeJuego = onEndGame
                )
                Text(text = preguntador, color = Color.Gray)
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = preguntaActual,
                    color = Color.Gray
                )
                TeamButtons(
                    onWonBlueTeam = {
                        juego.puntosEquipo1++
                        actualizarPuntuaciones(
                            juego = juego,
                            mostrarPuntuaciones = { puntuaciones = it },
                            mostrarProgresosEquipos = { equipo1, equipo2 ->
                                currentProgressTeamOne = equipo1 / 100f
                                currentProgressTeamTwo = equipo2 / 100f
                            })
                        juego.preguntasRespondidas.add(juego.preguntaActual.toString())
                        if (!juego.avanzarPregunta()) {
                            onEndGame()
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = onEndGame
                            )
                        }
                    },
                    onWonRedTeam = {
                        juego.puntosEquipo2++
                        actualizarPuntuaciones(
                            juego = juego,
                            mostrarPuntuaciones = { puntuaciones = it },
                            mostrarProgresosEquipos = { equipo1, equipo2 ->
                                currentProgressTeamOne = equipo1 / 100f
                                currentProgressTeamTwo = equipo2 / 100f
                            })
                        juego.preguntasRespondidas.add(juego.preguntaActual.toString())
                        if (!juego.avanzarPregunta()) {
                            onEndGame()
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = onEndGame
                            )
                        }
                    })//ver como subir los puntos
                LinearProgressIndicator(
                    progress = { currentProgressTeamOne },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    color = Color.Blue,
                    trackColor = Color.LightGray
                )
                LinearProgressIndicator(
                    progress = { currentProgressTeamTwo },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    color = Color.Red,
                    trackColor = Color.LightGray
                )
                Text(modifier = Modifier.padding(vertical = 16.dp), text = puntuaciones)
                Spacer(modifier = Modifier.weight(1f))
                Text(modifier = Modifier.padding(vertical = 16.dp), text = preguntasRestantes)
                ButtonsManageQuestions(
                    showAnswer = showAnswer,
                    onShowAnswer = {
                        showAnswer = it
                        if (showAnswer) respuestaPreguntaActual = mostrarRespuestaEnDialogo(juego)
                    },
                    onNextQuestion = {
                        juego.preguntasRespondidas.add(juego.preguntaActual.toString())
                        if (!juego.avanzarPregunta()) {
                            onEndGame()
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = onEndGame
                            )
                        }
                    })
                if (showAnswer) AnswerBottomSheet(
                    solucionPregunta = respuestaPreguntaActual,
                    onDismiss = { showAnswer = false })

            }
        }
    }
}

private fun mostrarRespuestaEnDialogo(juego: Juego): String {
    val pregunta = juego.mostrarPreguntaActual()
    return if (pregunta != null) {
        val (_, _, respuesta) = pregunta
        respuesta
    } else {
        ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerBottomSheet(solucionPregunta: String, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = solucionPregunta)
        }
    }
}

@Composable
private fun RestartButton(onRestart: () -> Unit) {
    IconButton(onClick = onRestart) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reiniciar",
            tint = Color.Blue
        )
    }
}

@Composable
private fun TeamButtons(onWonBlueTeam: () -> Unit, onWonRedTeam: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Button(
            onClick = onWonBlueTeam,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text = "Equipo 1")
        }
        Button(
            onClick = onWonRedTeam,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Equipo 2")
        }
    }
}

@Composable
private fun ButtonsManageQuestions(
    showAnswer: Boolean,
    onShowAnswer: (Boolean) -> Unit,
    onNextQuestion: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundedButton(
            image = if (showAnswer) ImageVector.vectorResource(R.drawable.ic_visibility_on) else ImageVector.vectorResource(
                R.drawable.ic_visibility_off
            ),
            textButton = "Mostrar respuesta",
            onClick = {
                onShowAnswer(!showAnswer)
            }
        )
        RoundedButton(
            image = Icons.Default.PlayArrow,
            textButton = "Siguiente pregunta",
            onClick = onNextQuestion
        )
    }
}

@Composable
private fun RoundedButton(
    modifier: Modifier = Modifier,
    image: ImageVector,
    textButton: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier.background(Color.Green, shape = CircleShape)
        ) {
            Icon(imageVector = image, tint = Color.Red, contentDescription = null)
        }
        Text(text = textButton)
    }
}

private fun mostrarPreguntaActual(
    juego: Juego,
    mostrarPreguntador: (String) -> Unit,
    mostrarPregunta: (String) -> Unit,
    mostrarPreguntasRestantes: (String) -> Unit,
    finDeJuego: () -> Unit
) {
    val pregunta = juego.mostrarPreguntaActual()
    if (pregunta != null) {
        val (preguntador, preguntaTexto, _) = pregunta
        mostrarPreguntador("Preguntador: $preguntador")
        mostrarPregunta(preguntaTexto)
        mostrarPreguntasRestantes("Preguntas restantes: ${juego.preguntas.size - juego.preguntaActual}")
    } else if (juego.preguntaActual >= juego.preguntas.size) finDeJuego()
}

private fun actualizarPuntuaciones(
    juego: Juego,
    mostrarProgresosEquipos: (Int, Int) -> Unit,
    mostrarPuntuaciones: (String) -> Unit
) {
    mostrarProgresosEquipos(juego.puntosEquipo1, juego.puntosEquipo2)
    mostrarPuntuaciones("Puntuación: ${juego.puntosEquipo1} - ${juego.puntosEquipo2}")
}

@Preview(showBackground = true)
@Composable
private fun PreguntasScreenPrev() {
    val context = LocalContext.current
    PreguntasScreen(
        preguntasManager = PreguntasManager(context),
        puntuacionManager = PuntuacionManager(context),
        juego = Juego(),
        onEndGame = {})
}