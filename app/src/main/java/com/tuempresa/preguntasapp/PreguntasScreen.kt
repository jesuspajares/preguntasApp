package com.tuempresa.preguntasapp

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntasScreen(
    preguntasManager: PreguntasManager,
    puntuacionManager: PuntuacionManager,
    juego: Juego,
    onEndGame: (String) -> Unit
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
                                currentProgressTeamOne = equipo1 / 50f
                                currentProgressTeamTwo = equipo2 / 50f
                            })
                        mostrarPreguntaActual(
                            juego = juego,
                            mostrarPreguntador = { preguntador = it },
                            mostrarPregunta = { preguntaActual = it },
                            mostrarPreguntasRestantes = { preguntasRestantes = it },
                            finDeJuego = { onEndGame(mostrarEquipoGanador(juego)) }
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
                    finDeJuego = { onEndGame(mostrarEquipoGanador(juego)) }
                )
                Text(text = preguntador, fontSize = 24.sp)
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = preguntaActual,
                    fontSize = 24.sp
                )
                TeamButtons(
                    onWonBlueTeam = {
                        juego.puntosEquipo1++
                        actualizarPuntuaciones(
                            juego = juego,
                            mostrarPuntuaciones = { puntuaciones = it },
                            mostrarProgresosEquipos = { equipo1, equipo2 ->
                                currentProgressTeamOne =
                                    equipo1 / 50f // antes se dividia por 50 por aquello del progreso que sea 100
                                currentProgressTeamTwo = equipo2 / 50f
                            })
                        juego.preguntasRespondidas.add(juego.preguntaActual.toString())
                        if (!juego.avanzarPregunta()) {
                            onEndGame(mostrarEquipoGanador(juego))
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = { onEndGame(mostrarEquipoGanador(juego)) }
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
                            onEndGame(mostrarEquipoGanador(juego))
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = { onEndGame(mostrarEquipoGanador(juego)) }
                            )
                        }
                    })
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
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "Puntuaciones",
                    fontSize = 24.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FlipScoreboard(
                        juego.puntosEquipo1,
                        color = Color.Blue,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(20.dp)
                            .background(color = Color.Gray)
                    )
                    FlipScoreboard(
                        juego.puntosEquipo2,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = preguntasRestantes,
                    fontSize = 24.sp
                )
                ButtonsManageQuestions(
                    showAnswer = showAnswer,
                    onShowAnswer = {
                        showAnswer = it
                        if (showAnswer) respuestaPreguntaActual = mostrarRespuestaEnDialogo(juego)
                    },
                    onNextQuestion = {
                        juego.preguntasRespondidas.add(juego.preguntaActual.toString())
                        if (!juego.avanzarPregunta()) {
                            onEndGame(mostrarEquipoGanador(juego))
                        } else {
                            mostrarPreguntaActual(
                                juego = juego,
                                mostrarPreguntador = { preguntador = it },
                                mostrarPregunta = { preguntaActual = it },
                                mostrarPreguntasRestantes = { preguntasRestantes = it },
                                finDeJuego = { onEndGame(mostrarEquipoGanador(juego)) }
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
            Text(text = solucionPregunta, fontSize = 24.sp)
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

private fun mostrarEquipoGanador(juego: Juego): String {
    return if (juego.puntosEquipo1 > juego.puntosEquipo2) {
        "Equipo 1"
    } else if (juego.puntosEquipo1 < juego.puntosEquipo2) {
        "Equipo 2"
    } else {
        "Todos"
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
            modifier = modifier.background(Color(0xFF228B22), shape = CircleShape)
        ) {
            Icon(imageVector = image, tint = Color(0xFF8B0000), contentDescription = null)
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

@Composable
fun FlipPageScoreboardNew(
    score: Int,
    backgroundColor: Color = Color.White, // Color personalizable para el fondo
    modifier: Modifier = Modifier
) {
    // Estado para almacenar el valor previo del marcador
    var previousScore by remember { mutableStateOf(score) }
    // Animación para la posición vertical de la hoja
    val offsetY = animateFloatAsState(
        targetValue = if (score != previousScore) 500f else 0f, // Controla el desplazamiento
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        finishedListener = { previousScore = score } // Actualiza el valor previo al finalizar
    )

    // Caja principal
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Número estático (el nuevo marcador)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Hoja en movimiento (el marcador previo)
        if (offsetY.value > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        y = offsetY.value.dp,
                        x = -offsetY.value.dp / 2
                    ) // Movimiento en diagonal
                    .background(backgroundColor)
                    .clip(RectangleShape), // Forma de la hoja
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = previousScore.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
private fun FlipScoreboard(
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    var previousScore by remember { mutableStateOf(score) }
    val rotation = animateFloatAsState(
        targetValue = if (score != previousScore) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        finishedListener = {
            previousScore = score
        }
    )

    Box(modifier = modifier) {
        // Parte trasera (nuevo número)
        if (rotation.value >= 90) {
            ScoreDigit(
                digit = score,
                color = color,
                rotation = rotation.value - 180,
                visible = true
            )
        }

        // Parte delantera (número actual)
        if (rotation.value < 90) {
            ScoreDigit(
                digit = previousScore,
                color = color,
                rotation = rotation.value,
                visible = true
            )
        }
    }
}

@Composable
private fun ScoreDigit(
    digit: Int,
    color: Color,
    rotation: Float,
    visible: Boolean
) {
    Box(
        modifier = Modifier
            .graphicsLayer(
                rotationX = rotation,
                cameraDistance = 8 * LocalDensity.current.density,
                alpha = if (visible) 1f else 0f
            )
            .background(color)
            .padding(8.dp)
    ) {
        Text(
            text = digit.toString(),
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
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