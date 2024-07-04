package top.rrricardo.chiara.extension

import java.util.Locale

fun Long.toTime(): String {
    val stringBuffer = StringBuffer()

    val minutes = (this / 60000).toInt()
    val seconds = (this / 1000 % 60).toInt()

    stringBuffer
        .append(String.format(Locale.CHINA, "%02d", minutes))
        .append(":")
        .append(String.format(Locale.CHINA, "%02d", seconds))

    return stringBuffer.toString()
}