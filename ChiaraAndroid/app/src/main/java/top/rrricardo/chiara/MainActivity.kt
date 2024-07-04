package top.rrricardo.chiara

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import top.rrricardo.chiara.service.impl.MusicService
import top.rrricardo.chiara.ui.ChiaraApp
import top.rrricardo.chiara.ui.theme.ChiaraTheme
import top.rrricardo.chiara.ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChiaraTheme {
                ChiaraApp(mainViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mainViewModel.destroyMediaController()

        stopService(Intent(this, MusicService::class.java))
    }
}
