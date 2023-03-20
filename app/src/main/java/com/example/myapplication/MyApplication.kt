package com.example.myapplication

import android.app.Application
import android.content.Context
import android.provider.SyncStateContract
import androidx.room.Room
import androidx.work.*
import com.example.myapplication.vm.MyViewModel
import com.example.service.cache.LotteryDataDatabase
import com.example.service.service.ParseService
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
        android.util.Log.v("QQQQ", "startSyncTask")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val syncTask = PeriodicWorkRequestBuilder<SyncWorker>(
            6, TimeUnit.HOURS, // repeatInterval (the period cycle)
            15, TimeUnit.MINUTES) // flexInterval
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork("Sync task", ExistingPeriodicWorkPolicy.REPLACE, syncTask)
        android.util.Log.v("QQQQ", "startSyncTask end")

        // TODO 移到下載的地方
        val myWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork("Test", ExistingWorkPolicy.REPLACE, myWorkRequest)
    }
}

val myModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryDataDatabase::class.java, "database-name"
        ).build()
    }

    single { MyViewModel(SyncUseCase(ParseService())) }
}