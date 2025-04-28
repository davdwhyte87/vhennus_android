package com.vhennus.general.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Immutable


data class SnackbarDataWithType(val message: String, val type: SnackbarType)

@Immutable
data class CustomSnackbarVisuals(
    override val message: String,
    val type: SnackbarType,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false
) : SnackbarVisuals

enum class SnackbarType {
    SUCCESS,
    ERROR,
    DEFAULT
}