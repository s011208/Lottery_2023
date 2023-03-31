package com.example.myapplication

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import androidx.work.*
import com.example.analytics.Analytics
import com.example.myapplication.vm.MyViewModel
import com.example.myapplication.vm.Source
import com.example.service.cache.LotteryDataDatabase
import com.example.service.cache.Preferences
import com.example.service.service.ParseService
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.*
import java.util.concurrent.TimeUnit


class MyApplication : Application() {

    companion object {
        private const val LOG_TAG = "QQQQ"
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()

        startKoin {
            // declare used Android context
            androidContext(this@MyApplication)
            // declare modules
            modules(myModule)
        }

        startSyncTask(this)

        if (VERSION.SDK_INT <= VERSION_CODES.R) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, "$tag:$LOG_TAG", message, t)
                }
            })
        }
    }

    private fun startSyncTask(context: Context) {
        Timber.d("startSyncTask")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val syncTask = PeriodicWorkRequestBuilder<SyncWorker>(
            6, TimeUnit.HOURS, // repeatInterval (the period cycle)
            15, TimeUnit.MINUTES
        ) // flexInterval
            .setInputData(Data.Builder().putString(SyncWorker.SOURCE, Source.PERIODIC.name).build())
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("Sync task", ExistingPeriodicWorkPolicy.REPLACE, syncTask)
        Timber.d("startSyncTask end")

        // TODO 移到下載的地方
//        val myWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
//            .setBackoffCriteria(
//                BackoffPolicy.LINEAR,
//                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
//                TimeUnit.MILLISECONDS
//            )
//            .setInputData(Data.Builder().putString(SyncWorker.SOURCE, Source.ONE_TIME.name).build())
//            .build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork("Test", ExistingWorkPolicy.REPLACE, myWorkRequest)
    }
}

val myModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryDataDatabase::class.java, "database-name"
        ).build()
    }

    single { MyViewModel(get(), get(), get()) }
    single { ParseService() }
    single { SyncUseCase(get()) }
    single { DisplayUseCase() }
    single { Preferences(get()) }
    single { SettingsUseCase(get()) }
    single { Analytics() }
}