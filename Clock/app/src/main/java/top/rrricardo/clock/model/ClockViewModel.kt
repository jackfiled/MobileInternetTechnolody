package top.rrricardo.clock.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import top.rrricardo.clock.controller.AnimatorController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ClockViewModel : ViewModel() {

    val zones = listOf("Asia/Shanghai", "Asia/Tokyo", "America/Los_Angeles", "Europe/London")

    /**
     * 时钟当前的年份
     */
    var year : Int by mutableIntStateOf(0)

    /**
     * 时钟当前的月份
     */
    var month : Int by mutableIntStateOf(0)

    /**
     * 时钟当前的日期
     */
    var day : Int by mutableIntStateOf(0)

    /**
     * 时钟当前的小时数
     */
    var hour : Int by mutableIntStateOf(0)

    /**
     * 时钟当前的分钟数
     */
    var minute : Int by mutableIntStateOf(0)

    /**
     * 时钟当前的秒数
     */
    var second : Int by mutableIntStateOf(0)

    /**
     * 当前是否为数字时钟
     */
    var isDigit : Boolean by mutableStateOf(false)

    private val animatorController = AnimatorController(this)

    private var currentZone : String = zones[0]

    fun restore(zoneString: String = currentZone) {
        Log.i("Clock", "Restoring of $zoneString is triggered.")
        currentZone = zoneString
        val now = ZonedDateTime.now(ZoneId.of(zoneString))

        year = now.year
        month = now.month.value
        day = now.dayOfMonth

        hour = now.hour
        minute = now.minute
        second = now.second

        animatorController.start(second, 60)
    }

    fun formatDigitString(): String {
        val hourString = if (hour >= 10) {
            "$hour"
        } else {
            "0$hour"
        }

        val minuteString = if (minute >= 10) {
            "$minute"
        } else {
            "0$minute"
        }

        val secondString = if (second >= 10) {
            "$second"
        } else {
            "0$second"
        }

        return "${hourString}:${minuteString}:${secondString}"
    }

    fun changeMode() {
        isDigit = !isDigit
    }
}