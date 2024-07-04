package top.rrricardo.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import top.rrricardo.clock.ui.ClockApp
import top.rrricardo.clock.ui.ZoneManager
import top.rrricardo.clock.ui.theme.ClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApplication()
        }
    }
}

@Composable
fun MainApplication() {
    val navigationController = rememberNavController()

    ClockTheme {
        NavHost(navController = navigationController, startDestination = "clock") {
            composable(NavigationRouters.CLOCK) {
                ClockApp(navigationController)
            }
            composable(NavigationRouters.ZONE_MANAGER) {
                ZoneManager(navigationController)
            }
        }
    }
}

object NavigationRouters {
    const val CLOCK = "clock"

    const val ZONE_MANAGER = "zoneManager"
}