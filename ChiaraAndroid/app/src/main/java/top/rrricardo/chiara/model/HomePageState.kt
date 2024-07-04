package top.rrricardo.chiara.model

import top.rrricardo.chiara.openapi.model.AlbumResponse
import top.rrricardo.chiara.openapi.model.ShowSeasonResponse
import top.rrricardo.chiara.openapi.model.SongResponse

data class HomePageState(
    val loading: Boolean = false,
    val isMusicPage: Boolean = true,
    val selectedAlbum : AlbumResponse? = null,
    val errorMessage: String? = null,
    val albums: List<AlbumResponse> = emptyList(),
    val selectedSeason : ShowSeasonResponse? = null,
    val seasons: List<ShowSeasonResponse> = emptyList(),
)
