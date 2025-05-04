package br.ftdev.core.ui.component.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.ftdev.core.ui.R
import br.ftdev.core.ui.theme.PokeYellow
import coil.network.HttpException
import java.io.IOException

private const val DEFAULT_ERROR_MESSAGE = "Erro desconhecido ao buscar Pokémon"
private const val REFRESH_ERROR_MESSAGE = "Um erro inesperado ocorreu durante a atualização."
private const val NETWORK_ERROR_MESSAGE = "Sem conexão. Verifique sua internet."
private const val SERVER_ERROR_MESSAGE = "Falha no servidor"

@Composable
fun String.ErrorMessage(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            tint = PokeYellow,
            contentDescription = "Filtrar",
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = this@ErrorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

fun Throwable.getErrorMessage(isRefresh: Boolean = false): String {
    return when (this) {
        is IOException -> NETWORK_ERROR_MESSAGE
        is HttpException -> "$SERVER_ERROR_MESSAGE (${this.response.code})."
        else -> this.message ?: if (isRefresh) REFRESH_ERROR_MESSAGE else DEFAULT_ERROR_MESSAGE
    }
}