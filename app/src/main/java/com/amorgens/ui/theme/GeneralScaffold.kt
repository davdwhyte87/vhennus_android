package com.amorgens.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun GeneralScaffold(topBar: @Composable () -> Unit, floatingActionButton: @Composable ()->Unit, screenView: @Composable ()->Unit) {

    androidx.compose.material3.Scaffold(
        topBar = {
            topBar()
        },
        modifier = Modifier
            .padding(all = 0.dp)
            .background(color = MaterialTheme.colorScheme.primary),
        bottomBar = {

        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.tertiary,
        floatingActionButton = {
            floatingActionButton()
        },

        floatingActionButtonPosition = FabPosition.Center,
    ) { it ->
        Box(modifier = Modifier.padding(it)) {
            screenView()
        }
    }
}