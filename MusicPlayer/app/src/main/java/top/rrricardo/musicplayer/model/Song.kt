package top.rrricardo.musicplayer.model

import androidx.media3.common.MediaItem
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val mediaId : String,
    val title: String,
    val subTitle: String,
    val songUrl: String,
    val imageUrl: String
)

fun MediaItem.toSong() =
    Song(
        id = 0,
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        subTitle = mediaMetadata.subtitle.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString()
    )