package top.rrricardo.musicplayer.ui.page

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import top.rrricardo.musicplayer.R
import top.rrricardo.musicplayer.model.HomeEvent
import top.rrricardo.musicplayer.model.HomeUiState
import top.rrricardo.musicplayer.model.PlayState
import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.ui.component.MusicItem

@Composable
fun HomePage(
    onEvent: (HomeEvent) -> Unit,
    uiState: HomeUiState
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)

            Spacer(
                modifier = Modifier
                    .background(appBarColor)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            HomeAppBar(backgroundColor = appBarColor, modifier = Modifier.fillMaxWidth())

            with(uiState) {
                when {
                    loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.Center)
                                    .padding(
                                        top = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                            )
                        }
                    }

                    !loading && songs != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onBackground)
                                    .align(Alignment.TopCenter)
                            ) {
                                items(songs) {
                                    MusicItem(onClick = {
                                        onEvent(HomeEvent.OnSongSelected(it))
                                        onEvent(HomeEvent.PlaySong)
                                    }, song = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(backgroundColor: Color, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Row {
                Text(text = "音乐播放器", modifier = Modifier.padding(start = 8.dp))
            }
        },
        actions = {
            CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 0.4f)) {

            }
        },
        modifier = modifier.background(backgroundColor)
    )
}

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvent) -> Unit,
    playState: PlayState?,
    song: Song?,
    onBarClick: () -> Unit
) {
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    AnimatedVisibility(visible = playState != PlayState.STOPPED,
        modifier = modifier) {
        if (song != null) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 0 -> {
                                    onEvent(HomeEvent.SkipToPreviousSong)
                                }

                                offsetX < 0 -> {
                                    onEvent(HomeEvent.SkipToNextSong)
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
                HomeBottomBarItem(song = song, onEvent = onEvent, playerState = playState, onBarClick = onBarClick)
            }
        }
    }
}

@Composable
fun HomeBottomBarItem(
    song: Song,
    onEvent: (HomeEvent) -> Unit,
    playerState: PlayState?,
    onBarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .clickable(onClick = { onBarClick() })

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.imageUrl),
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
                    song.subTitle,
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
                if (playerState == PlayState.PLAYING) {
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
                    .padding(end = 16.dp)
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
                        if (playerState == PlayState.PLAYING) {
                            onEvent(HomeEvent.PauseSong)
                        } else {
                            onEvent(HomeEvent.ResumeSong)
                        }
                    },
            )

        }
    }
}


@Preview
@Composable
fun HomePagePreview() {
    HomePage(onEvent = {}, uiState = HomeUiState())
}