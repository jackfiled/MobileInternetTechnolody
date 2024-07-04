package top.rrricardo.musicplayer.utils

fun Long.toTime(): String {
    val stringBuffer = StringBuffer()

    val minutes = (this / 60000).toInt()
    val second = (this / 1000 % 60).toInt()

    stringBuffer
        .append(String.format("%02d", minutes))
        .append(":")
        .append(String.format("%02d", second))

    return stringBuffer.toString()
}