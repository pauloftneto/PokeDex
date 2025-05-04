package br.ftdev.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import br.ftdev.core.ui.R
import androidx.compose.ui.Alignment.Companion as Alignment1

private const val MAX_STAT_VALUE = 255F

@Composable
fun StatItem(
    statName: String,
    statValue: Int,
    modifier: Modifier = Modifier
) {

    val progressColor = when {
        statValue < 60 -> Color.Red.copy(alpha = 0.7f)
        statValue < 90 -> Color(0xFFFFA500).copy(alpha = 0.8f)
        else -> Color.Green.copy(alpha = 0.7f)
    }

    val progress = statValue.toFloat() / MAX_STAT_VALUE

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment1.CenterVertically
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