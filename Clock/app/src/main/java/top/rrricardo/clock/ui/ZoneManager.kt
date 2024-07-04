package top.rrricardo.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import top.rrricardo.clock.NavigationRouters
import top.rrricardo.clock.model.ClockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneManager(navigationController: NavController = rememberNavController()) {
    val viewModel: ClockViewModel = viewModel()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "添加或删除时区")
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navigationController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to main application"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.zones.forEach {
                ZoneItem(zone = it)
            }
            OutlinedButton(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                onClick = {}) {
                Text(text = "添加时区",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun ZoneItem(zone: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = zone, fontSize = 20.sp)
    }
}

@Preview
@Composable
fun ZoneManagerPreview() {
    ZoneManager()
}