package top.rrricardo.chiara.model

import top.rrricardo.chiara.openapi.model.AlbumResponse
import top.rrricardo.chiara.openapi.model.SongResponse

data class AlbumPageState(
    val loading: Boolean = false,
    val selectedSong: SongResponse? = null,
    val album: AlbumResponse? = null,
    val errorMessage: String? = null
)
