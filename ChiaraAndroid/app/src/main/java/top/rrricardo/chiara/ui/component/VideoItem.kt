package top.rrricardo.chiara.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.rrricardo.chiara.openapi.model.EpisodeResponse
import top.rrricardo.chiara.openapi.model.ShowSeasonResponse

@Composable
fun SeasonItem(season: ShowSeasonResponse, onClick: () -> Unit) {
    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .clickable {
            onClick()
        }
        .fillMaxWidth()
        .height(80.dp)) {
        HorizontalDivider()

        Text(
            text = season.title,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun EpisodeItem(episode: EpisodeResponse, onClick: () -> Unit) {
    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .clickable {
            onClick()
        }
        .fillMaxWidth()
        .height(80.dp)) {
        HorizontalDivider()

        Text(
            text = episode.title,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 0.4f)) {
            Text(
                text = episode.episodeNumber,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeasonItemPreview() {
    Column {
        SeasonItem(
            season = ShowSeasonResponse(
                id = 1,
                title = "Tonikaku Kawaii S2",
                episodes = emptyList()
            )
        ) {

        }

        EpisodeItem(
            episode = EpisodeResponse(
                id = 1,
                title = "Tonikaku Kawaii S2 E1",
                episodeNumber = "1",
                showSeasonId = 1
            )
        ) {
        }
    }
}