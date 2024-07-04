package top.rrricardo.chiara.model

import top.rrricardo.chiara.openapi.model.ShowSeasonResponse

data class ShowPageState(
    val loading: Boolean = false,
    val showSeason: ShowSeasonResponse? = null
)
