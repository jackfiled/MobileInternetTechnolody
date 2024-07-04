package top.rrricardo.chiara.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.rrricardo.chiara.ui.component.ChiaraAppBar
import top.rrricardo.chiara.ui.component.HomeBottomBar
import top.rrricardo.chiara.ui.component.MusicItem
import top.rrricardo.chiara.ui.viewmodel.AlbumPageViewModel
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@Composable
fun AlbumPage(
    mainViewModel: MainViewModel,
    albumPageViewModel: AlbumPageViewModel,
    navController: NavController
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChiaraAppBar(
                title = albumPageViewModel.albumPageState.album?.title ?: "音乐播放器",
                showNavigationIcon = true,
                onNavigateUp = {
                    navController.navigateUp()
                }
            )

            with(albumPageViewModel.albumPageState) {
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
                                        top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp
                                    )
                            )
                        }
                    }

                    !loading -> {
                        album?.songs?.let { songs ->
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                items(songs) {
                                    MusicItem(
                                        title = it.title,
                                        artist = it.arist,
                                        coverImageUrl = it.coverImageUrl,
                                        playing = albumPageViewModel.albumPageState.selectedSong?.id == it.id
                                    ) {
                                        albumPageViewModel.selectSong(it)
                                        albumPageViewModel.playSong()
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            HomeBottomBar(mainViewModel = mainViewModel, navController = navController)
        }
    }
}
