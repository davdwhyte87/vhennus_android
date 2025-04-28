package com.vhennus.general.presentation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun AppScaffold(
    snackbarHostState: SnackbarHostState,
    topBar: @Composable ()-> Unit,
    screenView: @Composable ()->Unit,

) {
    Scaffold(
        topBar = {topBar()},
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){ barData ->
                val data = barData.visuals as? CustomSnackbarVisuals
                val bg = when (data?.type) {
                    SnackbarType.SUCCESS -> Color(0xFF4CAF50)
                    SnackbarType.ERROR   -> Color(0xFFF44336)
                    SnackbarType.DEFAULT -> Color.DarkGray
                    null->Color.DarkGray
                }
                Snackbar(
                    containerColor = bg,
                    contentColor = Color.White,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(data?.message ?: "")
                }
            }
//            SnackbarHost(hostState) { data ->
//                val bg = when (currentType) {
//                    SnackbarType.SUCCESS -> Color(0xFF4CAF50)
//                    SnackbarType.ERROR   -> Color(0xFFF44336)
//                    SnackbarType.DEFAULT -> Color.DarkGray
//                }
//                Snackbar(
//                    containerColor = bg,
//                    contentColor = Color.White,
//                    modifier = Modifier.padding(8.dp)
//                ) {
//                    Text(data.visuals.message)
//                }
//            }
        },
    ) { padding ->
        Surface (
            modifier = Modifier
                .padding(padding)

        ){
            screenView()
        }

    }
}
