package top.rrricardo.chiara.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import top.rrricardo.chiara.R
import top.rrricardo.chiara.model.PlayerState
import top.rrricardo.chiara.openapi.model.SongResponse
import top.rrricardo.chiara.service.MusicController
import top.rrricardo.chiara.ui.Navigation
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }
    val musicControllerState = mainViewModel.musicControllerState
    val musicController = mainViewModel.musicController

    AnimatedVisibility(
        visible = true,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 0 -> {
                                    musicController.skipToPreviousSong()
                                }

                                offsetX < 0 -> {
                                    musicController.skipToNextSong()
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = dragAmount.x
                        }
                    )
                }
                .background(
                    if (!isSystemInDarkTheme()) {
                        Color.LightGray
                    } else {
                        Color.DarkGray
                    }
                ),
        ) {
            if (musicControllerState.currentSong != null) {
                HomeBottomBarItem(
                    song = musicControllerState.currentSong,
                    musicController = musicController,
                    playerState = musicControllerState.playerState,
                    navController = navController
                )
            } else {
                NonPlayingHomeBar(navController = navController)
            }
        }
    }
}

@Composable
fun HomeBottomBarItem(
    song: SongResponse,
    musicController: MusicController,
    playerState: PlayerState,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .clickable(onClick = {
                navController.navigate(Navigation.SONG_SCREEN)
            })
    ) {
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(song.coverImageUrl)
                        .crossfade(true).build()
                ),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .offset(16.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 32.dp),
            ) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    song.arist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = 0.60f
                        }

                )
            }
            val painter = rememberAsyncImagePainter(
                if (playerState == PlayerState.PLAYING) {
                    R.drawable.ic_round_pause
                } else {
                    R.drawable.ic_round_play_arrow
                }
            )

            Image(
                painter = painter,
                contentDescription = "Music",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 24.dp
                        )
                    ) {
                        if (playerState == PlayerState.PLAYING) {
                            musicController.pause()
                        } else {
                            musicController.resume()
                        }
                    },
            )

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.List,
                contentDescription = "播放列表",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(36.dp)
                    .clickable {
                        navController.navigate(Navigation.PLAYLIST_SCREEN)
                    }
            )
        }
    }
}

@Composable
fun NonPlayingHomeBar(navController: NavController) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = "Music",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 32.dp),
            ) {
                Text(
                    text = "未在播放",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.List,
                contentDescription = "播放列表",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(36.dp)
                    .clickable {
                        navController.navigate(Navigation.PLAYLIST_SCREEN)
                    }
            )
        }
    }
}