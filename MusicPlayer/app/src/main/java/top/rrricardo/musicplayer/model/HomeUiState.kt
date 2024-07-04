package top.rrricardo.musicplayer.model

data class HomeUiState (
    val loading: Boolean = false,
    val songs: List<Song>? = emptyList(),
    val selectedSong: Song? = null,
    val errorMessage: String? = null
)