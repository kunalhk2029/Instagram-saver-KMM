package com.saver.igv1.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.saver.igv1.business.data.app_storage.external.stories.StoriesExternalStorageService
import com.saver.igv1.business.data.app_storage.external.stories.StoriesExternalStorageServiceImpl
import com.saver.igv1.business.data.db.PrimaryDatabase
import com.saver.igv1.business.data.prefs.SharedPrefs
import com.saver.igv1.business.domain.VideoPlayerManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

actual val platformModule: Module = module {
    singleOf(::SharedPrefs)


    single<HttpClient> {
        HttpClient(Darwin) {

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single<PrimaryDatabase> {
        val dbFile = NSHomeDirectory() + "/primary_database.db"
        Room.databaseBuilder<PrimaryDatabase>(
            name = dbFile
        ).setDriver(BundledSQLiteDriver()).build()
    }

    singleOf(::VideoPlayerManager)

    singleOf(::StoriesExternalStorageServiceImpl).bind<StoriesExternalStorageService>()

}