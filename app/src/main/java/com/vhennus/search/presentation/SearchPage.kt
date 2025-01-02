package com.vhennus.search.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


@Composable
fun SearchPage(navController: NavController){
    GeneralScaffold(
        topBar = { BackTopBar("Search", navController) },
        floatingActionButton = {}
    ) {
        
    }
}