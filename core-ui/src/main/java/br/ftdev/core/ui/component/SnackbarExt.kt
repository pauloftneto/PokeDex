package br.ftdev.core.ui.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

suspend fun SnackbarHostState.eventSnackbarHost(
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    actionLabel: String = "OK"
) =
    this.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = duration
    )