package com.example.myapplication

import android.app.Application
import android.content.Context
import android.provider.SyncStateContract
import androidx.room.Room
import androidx.work.*
import com.example.analytics.Analytics
import com.example.debugger.MyLog
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
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@MyApplication)
            // declare modules
            modules(myModule)
        }

        startSyncTask(this)
    }

    private fun startSyncTask(context: Context) {
        MyLog.log( "startSyncTask")
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
        MyLog.log("startSyncTask end")

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