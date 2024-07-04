package top.rrricardo.chiara.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import top.rrricardo.chiara.Configuration
import top.rrricardo.chiara.openapi.client.infrastructure.ApiClient
import top.rrricardo.chiara.service.MusicController
import top.rrricardo.chiara.service.impl.MusicControllerImpl
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiClient() = ApiClient(
        baseUrl = Configuration.SERVER_ADDRESS,
        okHttpClientBuilder = OkHttpClient.Builder().readTimeout(Duration.ofSeconds(30))
    )

    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)
}