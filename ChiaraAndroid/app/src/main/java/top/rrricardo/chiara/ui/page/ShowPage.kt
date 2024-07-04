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
import top.rrricardo.chiara.ui.Navigation
import top.rrricardo.chiara.ui.component.ChiaraAppBar
import top.rrricardo.chiara.ui.component.EpisodeItem
import top.rrricardo.chiara.ui.viewmodel.ShowPageViewModel

@Composable
fun ShowPage(
    showPageViewModel: ShowPageViewModel,
    navController: NavController
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChiaraAppBar(title = showPageViewModel.showPageState.showSeason?.title ?: "Video")

            with(showPageViewModel.showPageState) {
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
                        showSeason?.episodes?.let {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                items(it) {
                                    EpisodeItem(episode = it) {
                                        navController.navigate(
                                            "${Navigation.VIDEO_SCREEN}?videoId=${it.id}&showId=${it.showSeasonId}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}