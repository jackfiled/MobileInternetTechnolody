package top.rrricardo.chiara.model

import top.rrricardo.chiara.openapi.model.EpisodeResponse
import top.rrricardo.chiara.openapi.model.ShowSeasonResponse

data class VideoPageState(
    val loading: Boolean = false,
    val playing: Boolean = false,
    val showSeasonResponse: ShowSeasonResponse? = null,
    val episodeResponse: EpisodeResponse? = null
)
