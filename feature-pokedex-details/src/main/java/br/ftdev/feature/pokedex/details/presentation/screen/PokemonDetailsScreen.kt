package br.ftdev.feature.pokedex.details.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import br.ftdev.core.ui.R
import br.ftdev.core.ui.component.ErrorMessage
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.core.ui.theme.PokemonTypeColor
import br.ftdev.core.ui.util.getVerticalGradient
import br.ftdev.feature.pokedex.details.presentation.PokemonDetailsViewModel
import br.ftdev.feature.pokedex.details.presentation.state.PokemonDetailsUiState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun PokemonDetailsScreen(
    viewModel: PokemonDetailsViewModel = koinViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PokemonAppTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is PokemonDetailsUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is PokemonDetailsUiState.Success -> {
                        PokemonDetailsContent(
                            pokemon = state.pokemon,
                            onBackClick = { navController.navigateUp() }
                        )
                    }

                    is PokemonDetailsUiState.Error -> {
                        ErrorMessage(
                            message = state.message,
                            onRetry = { viewModel.fetchDetails() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonDetailsContent(
    pokemon: PokemonDetails,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstColor = pokemon.types.first().name
    val secondColor = pokemon.types.lastOrNull()?.name ?: pokemon.types.first().name

    val gradientBrush = getVerticalGradient(
        listOf(
            PokemonTypeColor.getTypeColor(firstColor).copy(alpha = 0.2f),
            PokemonTypeColor.getTypeColor(firstColor).copy(alpha = 0.8f),
            PokemonTypeColor.getTypeColor(secondColor).copy(alpha = 0.8f)
        )
    )

    val view = LocalView.current
    val statusBarColor = remember(firstColor) {
        PokemonTypeColor.getTypeColor(firstColor).copy(alpha = 0.2f)
    }
    val isColorDark = remember(statusBarColor) {
        ColorUtils.calculateLuminance(statusBarColor.toArgb()) < 0.5
    }

    DisposableEffect(view, statusBarColor, isColorDark) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            val originalStatusBarColor = window.statusBarColor
            val insetsController = WindowInsetsControllerCompat(window, view)
            val originalAppearance = insetsController.isAppearanceLightStatusBars

            window.statusBarColor = statusBarColor.toArgb()
            insetsController.isAppearanceLightStatusBars = !isColorDark

            onDispose {
                if (window.statusBarColor == statusBarColor.toArgb()) {
                    window.statusBarColor = originalStatusBarColor
                }
                if (insetsController.isAppearanceLightStatusBars == !isColorDark) {
                    insetsController.isAppearanceLightStatusBars = originalAppearance
                }
            }
        } else {
            onDispose { }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(gradientBrush)
                .padding(dimensionResource(R.dimen.padding_medium)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_small)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) { // Tamanho fixo para área de toque
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "#${pokemon.id.toString().padStart(3, '0')}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                } // Fim Row Voltar/ID

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small))) // Espaço antes do nome
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemon.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = pokemon.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_size_large))
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            PokemonTypesSection(types = pokemon.types)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            PokemonMeasurements(height = pokemon.height, weight = pokemon.weight)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            PokemonStatsSection(stats = pokemon.stats)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        }

    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PokemonTypesSection(types: List<PokemonType>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium),
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { type ->
            TypeChip(typeName = type.name)
        }
    }
}

@Composable
fun TypeChip(typeName: String, modifier: Modifier = Modifier) {
    val typeColor = PokemonTypeColor.getTypeColor(typeName)

    AssistChip(
        modifier = modifier,
        onClick = { },
        label = {
            Text(typeName.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else
                    it.toString()
            })
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = typeColor,
            labelColor = Color.White
        ),
        border = null
    )

}

@Composable
fun PokemonMeasurements(height: Float, weight: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DetailItem(label = "Altura", value = "$height m")
        DetailItem(label = "Peso", value = "$weight kg")
    }
}

@Composable
fun PokemonStatsSection(stats: List<PokemonStat>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base Stats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
        )
        val maxStatValue = 255

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            stats.forEach { stat ->
                StatItem(
                    statName = stat.name,
                    statValue = stat.baseStat,
                    maxValue = maxStatValue
                )
            }
        }
    }
}

@Composable
fun StatItem(
    statName: String,
    statValue: Int,
    maxValue: Int,
    modifier: Modifier = Modifier
) {

    val progressColor = when {
        statValue < 60 -> Color.Red.copy(alpha = 0.7f)
        statValue < 90 -> Color(0xFFFFA500).copy(alpha = 0.8f)
        else -> Color.Green.copy(alpha = 0.7f)
    }

    val progress = statValue.toFloat() / maxValue.toFloat()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(dimensionResource(R.dimen.width_detail_name))
        )
        Text(
            text = statValue.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(dimensionResource(R.dimen.width_detail_number)),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.progress_bar_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_bar_rounded))),
            color = progressColor,
            trackColor = progressColor.copy(alpha = 0.2f)
        )
    }
}