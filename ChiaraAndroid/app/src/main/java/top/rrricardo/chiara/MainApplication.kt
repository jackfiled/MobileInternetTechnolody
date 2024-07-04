package top.rrricardo.chiara

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

}

object Configuration {
    const val SERVER_ADDRESS = "http://chiara.jackfiled.icu"
}