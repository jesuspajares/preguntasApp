package com.tuempresa.preguntasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndGameScreen(equipoGanador: String) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) })
    }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$equipoGanador sois unos gorrinos de campeonato", fontSize = 24.sp)
                Image(
                    painter = painterResource(R.drawable.ic_pig),
                    contentDescription = null,
                    modifier = Modifier.padding(60.dp)
                )
            }
        }
    }
}
