package top.rrricardo.chiara.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import top.rrricardo.chiara.R
import top.rrricardo.chiara.extension.toTime
import top.rrricardo.chiara.model.PlayerState
import top.rrricardo.chiara.model.RepeatState
import top.rrricardo.chiara.openapi.model.SongResponse
import top.rrricardo.chiara.ui.Navigation
import top.rrricardo.chiara.ui.component.AnimatedVinyl
import top.rrricardo.chiara.ui.theme.ChiaraTheme
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@Composable
fun SongPage(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    mainViewModel.musicControllerState.currentSong?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SongScreenContent(
                song = it,
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun SongScreenContent(
    song: SongResponse,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val musicControllerState = mainViewModel.musicControllerState

    val dominantColor by remember { mutableStateOf(Color.Transparent) }
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(
            dominantColor, MaterialTheme.colorScheme.background
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background
        )
    }

    val sliderColors = if (isSystemInDarkTheme()) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.onBackground,
            activeTrackColor = MaterialTheme.colorScheme.onBackground,
            inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.1f
            ),
        )
    } else SliderDefaults.colors(
        thumbColor = dominantColor,
        activeTrackColor = dominantColor,
        inactiveTrackColor = dominantColor.copy(
            alpha = 0.1f
        ),
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density
                        )
                    )
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Image(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f, fill = false)
                                .aspectRatio(1f)

                        ) {
                            val imagePainter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(song.coverImageUrl)
                                    .crossfade(true).build()
                            )

                            AnimatedVinyl(
                                painter = imagePainter,
                                isSongPlaying = musicControllerState.playerState == PlayerState.PLAYING
                            )
                        }

                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(song.arist,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer {
                                alpha = 0.60f
                            })

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {

                            Slider(
                                value = musicControllerState.currentPosition.toFloat(),
                                modifier = Modifier.fillMaxWidth(),
                                valueRange = 0f..musicControllerState.totalDuration.toFloat(),
                                colors = sliderColors,
                                onValueChange = {
                                    mainViewModel.musicController.seekTo(it.toLong())
                                },
                            )
                            CompositionLocalProvider(
                                LocalContentColor provides LocalContentColor.current.copy(
                                    alpha = 0.4f
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        musicControllerState.currentPosition.toTime(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        musicControllerState.totalDuration.toTime(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {
                                        mainViewModel.musicController.skipToPreviousSong()
                                    })
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                            val playPauseIcon =
                                if (musicControllerState.playerState == PlayerState.PLAYING) {
                                    R.drawable.ic_round_pause
                                } else {
                                    R.drawable.ic_round_play_arrow
                                }

                            Icon(
                                painter = painterResource(playPauseIcon),
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onBackground)
                                    .clickable(onClick = {
                                        if (musicControllerState.playerState == PlayerState.PLAYING) {
                                            mainViewModel.musicController.pause()
                                        } else {
                                            mainViewModel.musicController.resume()
                                        }
                                    })
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Skip Next",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {
                                        mainViewModel.musicController.skipToNextSong()
                                    })
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 20.dp)
                        ) {
                            val repeatIcon = when (musicControllerState.repeatState) {
                                RepeatState.Sequence -> R.drawable.ic_round_repeat
                                RepeatState.RepeatOne -> R.drawable.ic_round_repeat_one
                                RepeatState.Shuffle -> R.drawable.ic_round_shuffle
                            }

                            Icon(
                                painter = painterResource(id = repeatIcon),
                                contentDescription = "RepeatState",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background)
                                    .clickable(onClick = {
                                        when (musicControllerState.repeatState) {
                                            RepeatState.Sequence -> {
                                                mainViewModel.musicController.setRepeatOne()
                                            }

                                            RepeatState.RepeatOne -> {
                                                mainViewModel.musicController.setShuffleRepeat()
                                            }

                                            RepeatState.Shuffle -> {
                                                mainViewModel.musicController.setSequenceRepeat()
                                            }
                                        }
                                    })
                                    .padding(12.dp)
                                    .size(32.dp)
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.List,
                                contentDescription = "PlayList",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(32.dp)
                                    .clickable {
                                        navController.navigate(Navigation.PLAYLIST_SCREEN)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPreview() {
    ChiaraTheme {
        Text(text = "测试测试测试测试测试测试测试", color = MaterialTheme.colorScheme.primary)
    }
}