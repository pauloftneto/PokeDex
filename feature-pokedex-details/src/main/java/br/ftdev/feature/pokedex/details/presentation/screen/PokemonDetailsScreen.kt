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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import br.ftdev.core.ui.R
import br.ftdev.core.ui.component.PokemonMeasurements
import br.ftdev.core.ui.component.StatItem
import br.ftdev.core.ui.component.ToAsyncImage
import br.ftdev.core.ui.component.TypeChip
import br.ftdev.core.ui.component.error.ErrorMessage
import br.ftdev.core.ui.component.toImageRequest
import br.ftdev.core.ui.component.toPaddedId
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.core.ui.theme.PokemonTypeColor
import br.ftdev.core.ui.util.getVerticalGradient
import br.ftdev.feature.pokedex.details.presentation.PokemonDetailsViewModel
import br.ftdev.feature.pokedex.details.presentation.state.PokemonDetailsUiState
import org.koin.androidx.compose.koinViewModel

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
                        state.message.ErrorMessage(
                            onRetry = { viewModel.fetchDetails() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonDetailsContent(
    pokemon: PokemonDetails,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstColor = pokemon.types.first().name

    val gradientBrush = pokemon.gradientBrush(firstColor)

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
                .clip(
                    RoundedCornerShape(
                        bottomStart = dimensionResource(R.dimen.shape_corner_radius),
                        bottomEnd = dimensionResource(R.dimen.shape_corner_radius)
                    )
                )
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
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "#${pokemon.id.toPaddedId()}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                pokemon.imageUrl?.toImageRequest(LocalContext.current)
                    ?.ToAsyncImage(
                        contentDescription = pokemon.name,
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
private fun PokemonTypesSection(types: List<PokemonType>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium),
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { type ->
            type.name.TypeChip()
        }
    }
}

@Composable
private fun PokemonStatsSection(stats: List<PokemonStat>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.base_stats),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            stats.forEach { stat ->
                StatItem(
                    statName = stat.name,
                    statValue = stat.baseStat
                )
            }
        }
    }
}

@Composable
private fun PokemonDetails.gradientBrush(
    firstColor: String
): Brush {

    val secondColor = this.types.lastOrNull()?.name ?: this.types.first().name

    return getVerticalGradient(
        listOf(
            PokemonTypeColor.getTypeColor(firstColor).copy(alpha = 0.2f),
            PokemonTypeColor.getTypeColor(firstColor).copy(alpha = 0.8f),
            PokemonTypeColor.getTypeColor(secondColor).copy(alpha = 0.8f)
        )
    )
}