package top.rrricardo.chiara.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.rrricardo.chiara.ui.component.ChiaraAppBar
import top.rrricardo.chiara.ui.component.HomeBottomBar
import top.rrricardo.chiara.ui.component.MusicItem
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@Composable
fun PlayListPage(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    Scaffold(modifier = modifier, topBar = {
        ChiaraAppBar(
            title = "播放列表",
            showNavigationIcon = true,
            onNavigateUp = {
                navController.navigateUp()
            }
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            mainViewModel.musicController.clearPlayList()
            mainViewModel.refreshPlayList()
        }) {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
        }
    }, bottomBar = {
        HomeBottomBar(mainViewModel = mainViewModel, navController = navController)
    }) { padding ->
        Surface(
            modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            mainViewModel.refreshPlayList()
            Column(modifier = Modifier.fillMaxSize()) {
                with(mainViewModel) {
                    if (playList.isEmpty()) {
                        NoSongHint()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onBackground)
                        ) {
                            items(playList) {
                                MusicItem(
                                    title = it.title,
                                    artist = it.arist,
                                    coverImageUrl = it.coverImageUrl,
                                    playing = it.id == mainViewModel.musicControllerState.currentSong?.id
                                ) {
                                    mainViewModel.musicController.play(it.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoSongHint() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Text(
            text = "还没有任何歌曲呢",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoSongHitPreview() {
    NoSongHint()
}