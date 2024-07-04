package top.rrricardo.chiara.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import top.rrricardo.chiara.R
import top.rrricardo.chiara.ui.Navigation
import top.rrricardo.chiara.ui.component.ChiaraAppBar
import top.rrricardo.chiara.ui.component.HomeBottomBar
import top.rrricardo.chiara.ui.component.MusicItem
import top.rrricardo.chiara.ui.component.SeasonItem
import top.rrricardo.chiara.ui.viewmodel.HomePageViewModel
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@Composable
fun HomePage(
    mainViewModel: MainViewModel,
    homePageViewModel: HomePageViewModel,
    navController: NavHostController
) {
    Scaffold(topBar = {
        ChiaraAppBar(title = "Chiara播放器", actions = {
            Icon(imageVector = Icons.Rounded.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        homePageViewModel.fetchRemoteData()
                    }
            )
        })
    }, bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                    homePageViewModel.changeToMusicPage()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_library_music),
                        contentDescription = "Music library",
                        modifier = Modifier
                            .size(32.dp)
                    )
                }

                IconButton(onClick = {
                    homePageViewModel.changeToVideoPage()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_video_library),
                        contentDescription = "Video library",
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            with(homePageViewModel.homePageState) {
                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
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

                    !loading && isMusicPage -> {
                        MusicList(
                            mainViewModel = mainViewModel,
                            homePageViewModel = homePageViewModel,
                            navController = navController
                        )
                    }

                    !loading && !isMusicPage -> {
                        VideoList(
                            homePageViewModel = homePageViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MusicList(
    mainViewModel: MainViewModel,
    homePageViewModel: HomePageViewModel,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(homePageViewModel.homePageState.albums) {
                MusicItem(
                    title = it.title,
                    artist = it.arist,
                    coverImageUrl = it.coverImageUrl
                ) {
                    homePageViewModel.selectAlbum(it)
                    navController.navigate(
                        "${Navigation.ALBUM_SCREEN}/${it.id}"
                    )
                }
            }
        }

        HomeBottomBar(mainViewModel = mainViewModel, navController = navController)
    }
}

@Composable
fun VideoList(homePageViewModel: HomePageViewModel, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(homePageViewModel.homePageState.seasons) {
                SeasonItem(season = it) {
                    navController.navigate("${Navigation.SHOW_SCREEN}/${it.id}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    Scaffold(bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_library_music),
                        contentDescription = "Music library",
                        modifier = Modifier
                            .size(32.dp)
                    )
                }

                IconButton(onClick = {
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_video_library),
                        contentDescription = "Video library",
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
    }) {
        Box(modifier = Modifier.padding(it))
    }
}
