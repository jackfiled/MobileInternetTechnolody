package top.rrricardo.chiara.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import top.rrricardo.chiara.Configuration
import top.rrricardo.chiara.R
import top.rrricardo.chiara.ui.theme.ChiaraTheme

@Composable
fun MusicItem(
    title: String,
    artist: String,
    coverImageUrl: String,
    playing: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (playing) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.background
    }

    ConstraintLayout(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick()
            }
            .background(backgroundColor)
            .fillMaxWidth()
    ) {
        val (
            dividerRef, titleRef, artistRef, imageRef
        ) = createRefs()

        HorizontalDivider(Modifier.constrainAs(dividerRef) {
            top.linkTo(parent.top)
            centerHorizontallyTo(parent)
            width = Dimension.fillToConstraints
        })

        val coverImage = if (coverImageUrl == Configuration.SERVER_ADDRESS) {
            painterResource(id = R.drawable.ic_round_album)
        } else {
            rememberAsyncImagePainter(ImageRequest
                .Builder(LocalContext.current).data(coverImageUrl)
                .crossfade(true).build())
        }

        Image(
            painter = coverImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium)
                .constrainAs(imageRef) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                }
        )

        val textColor = if (playing) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onBackground
        }

        Text(
            text = title,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.constrainAs(titleRef) {
                linkTo(
                    start = parent.start,
                    end = imageRef.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start, 16.dp)
                width = Dimension.preferredWrapContent
            }
        )

        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 0.4f)) {
            Text(
                text = artist,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.constrainAs(artistRef) {
                    linkTo(
                        start = parent.start,
                        end = imageRef.start,
                        startMargin = 24.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(titleRef.bottom, 6.dp)
                    start.linkTo(parent.start, 16.dp)
                    width = Dimension.preferredWrapContent
                }
            )
        }
    }
}

@Preview
@Composable
fun MusicItemPreview() {
    ChiaraTheme {
        Column {
            MusicItem(
                title = "Genshin Impact - Jade Moon Upon a Sea of Clouds",
                artist = "Yu-Peng Chen, HOYO-MiX",
                coverImageUrl = "http://10.29.155.159:5078/api/file/b1c6a383-f860-4391-82ff-9507851603cc"
            ) {

            }
            MusicItem(
                title = "Genshin Impact - Jade Moon Upon a Sea of Clouds",
                artist = "Yu-Peng Chen, HOYO-MiX",
                coverImageUrl = "http://10.29.155.159:5078/api/file/b1c6a383-f860-4391-82ff-9507851603cc",
                playing = true
            ) {

            }
        }
    }
}