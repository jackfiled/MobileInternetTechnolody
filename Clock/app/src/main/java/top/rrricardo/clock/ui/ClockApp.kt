package top.rrricardo.clock.ui

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import top.rrricardo.clock.NavigationRouters
import top.rrricardo.clock.model.ClockViewModel
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockApp(navigationController : NavController = rememberNavController()) {
    val viewModel : ClockViewModel = viewModel()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "世界时钟")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigationController.navigate(NavigationRouters.ZONE_MANAGER)
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Manager the added zones")
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 30.dp)
        ) {
            Clocks(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Clocks(viewModel: ClockViewModel) {
    val pageStates = rememberPagerState {
        viewModel.zones.size
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    LaunchedEffect(key1 = pageStates.currentPage) {
        Log.i("Clock", "Current page is ${pageStates.currentPage}.")

        viewModel.restore(viewModel.zones[pageStates.currentPage])
    }

    HorizontalPager(state = pageStates) { pageState ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier
                .height(250.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    viewModel.changeMode()
                }
            ) {
                Crossfade(
                    targetState = viewModel.isDigit,
                    label = "ClockOrDigit",
                    animationSpec = tween(500)
                ) {
                    if (it) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 60.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = viewModel.formatDigitString(),
                                fontSize = 28.sp
                            )
                        }
                    } else {
                        Clock(viewModel)
                    }
                }
            }
            Text(
                text = viewModel.zones[pageState],
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color.LightGray,
                fontSize = 18.sp
            )
            Text(
                text = "${viewModel.year}年${viewModel.month}月${viewModel.day}日",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun Clock(viewModel: ClockViewModel) {
    val size = 240.dp

    Box(
        Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val radius = size.toPx() / 2
            val hourLength = radius * 0.1
            val minuteLength = radius * 0.05

            for (i in 0..11) {
                val hourAngel = i * Math.PI * 2 / 12

                // 小时刻度
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(
                        (radius + (radius) * sin(hourAngel)).toFloat(),
                        (radius - radius * cos(hourAngel)).toFloat()
                    ),
                    end = Offset(
                        (radius + (radius - hourLength) * sin(hourAngel)).toFloat(),
                        (radius - (radius - hourLength) * cos(hourAngel)).toFloat()
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // 分钟刻度
                for (j in 1..4) {
                    val minuteAngel = (i * 5 + j) * Math.PI * 2 / 60

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(
                            (radius + radius * sin(minuteAngel)).toFloat(),
                            (radius - radius * cos(minuteAngel)).toFloat()
                        ),
                        end = Offset(
                            (radius + (radius - minuteLength) * sin(minuteAngel)).toFloat(),
                            (radius - (radius - minuteLength) * cos(minuteAngel)).toFloat()
                        ),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // 秒针
            val secondPointerAngel = viewModel.second * Math.PI * 2 / 60
            val secondPointerLength = radius * 0.95

            drawLine(
                color = Color.Red,
                start = Offset(
                    (radius + secondPointerLength * sin(secondPointerAngel)).toFloat(),
                    (radius - secondPointerLength * cos(secondPointerAngel)).toFloat()
                ),
                end = Offset(radius, radius),
                strokeWidth = 2.dp.toPx()
            )

            // 分针
            val minutePointerAngel = viewModel.minute * Math.PI * 2 / 60
            val minutePointerLength = radius * 0.8

            drawLine(
                color = Color.Black,
                start = Offset(
                    (radius + minutePointerLength * sin(minutePointerAngel)).toFloat(),
                    (radius - minutePointerLength * cos(minutePointerAngel)).toFloat()
                ),
                end = Offset(radius, radius),
                strokeWidth = 4.dp.toPx()
            )

            // 时针
            val hourPointerAngel =
                (viewModel.hour.toFloat() % 12 + viewModel.minute.toFloat() / 60) * Math.PI * 2 / 12
            val hourPointerLength = radius * 0.6

            drawLine(
                color = Color.Red,
                start = Offset(
                    (radius + hourPointerLength * sin(hourPointerAngel)).toFloat(),
                    (radius - hourPointerLength * cos(hourPointerAngel)).toFloat()
                ),
                end = Offset(radius, radius),
                strokeWidth = 8.dp.toPx()
            )
        }
    }
}

@Preview
@Composable
fun ClockAppPreview() {
    ClockApp()
}