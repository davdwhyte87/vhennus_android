package com.vhennus.general.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun LoadImageWithPlaceholder(imageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Image from URL",
        placeholder = ColorPainter(Color.Gray),
        contentScale = ContentScale.Crop,
        error = ColorPainter(Color.Red),
        modifier = modifier
    )
}

@Composable
fun LoadImageWithUri(imageUrl: Uri, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Image from URL",
        placeholder = ColorPainter(Color.Gray),
        error = ColorPainter(Color.Red),
        modifier = modifier
    )
}