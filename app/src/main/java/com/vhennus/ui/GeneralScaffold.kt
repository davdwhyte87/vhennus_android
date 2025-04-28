package com.vhennus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
            .background(color = MaterialTheme.colorScheme.secondary),
        bottomBar = {

        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.secondary,
        floatingActionButton = {
            floatingActionButton()
        },

        floatingActionButtonPosition = FabPosition.EndOverlay,
    ) { it ->
        Surface (
            modifier = Modifier
            .padding(it)
            .padding(start = 15.dp, end = 15.dp)
                .consumeWindowInsets(it)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ){
            screenView()
        }
//        Column(modifier = Modifier
//            .padding(it)
//            .padding(start = 15.dp, end = 15.dp)
//
//        ) {
//            screenView()
//        }
    }
}