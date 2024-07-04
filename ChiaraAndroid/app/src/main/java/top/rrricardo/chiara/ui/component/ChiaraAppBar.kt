package top.rrricardo.chiara.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.rrricardo.chiara.ui.theme.ChiaraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiaraAppBar(
    modifier: Modifier = Modifier,
    title: String,
    showNavigationIcon: Boolean = false,
    onNavigateUp: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .background(appBarColor)
            .windowInsetsTopHeight(WindowInsets.statusBars)
    )
    TopAppBar(title = {
        Row {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }, navigationIcon = {
        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 0.4f)) {
            if (showNavigationIcon) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onNavigateUp()
                        }
                )
            }
        }
    }, actions = actions, modifier = modifier.background(appBarColor)
    )
}

@Preview(showBackground = true)
@Composable
fun ChiaraAppBarPreview() {
    ChiaraTheme {
        ChiaraAppBar(
            title = "Genshin Impact - Jade Moon Upon a Sea of Clouds",
            showNavigationIcon = true
        )
    }
}