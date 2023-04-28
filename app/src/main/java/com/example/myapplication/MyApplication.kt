package com.example.myapplication

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.analytics.Analytics
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_DAY_NIGHT_MODE
import com.example.myapplication.compose.appsettings.settingsDataStore
import com.example.myapplication.compose.lotterylog.vm.LotteryLogViewModel
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import com.example.myapplication.compose.lotterytable.vm.Source
import com.example.myapplication.compose.possibility.vm.PossibilityScreenViewModel
import com.example.service.cache.DayNightMode
import com.example.service.cache.Preferences
import com.example.service.cache.log.LotteryLogDatabase
import com.example.service.cache.lto.LotteryDataDatabase
import com.example.service.service.ParseService
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.LotteryLogUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree
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

        if (VERSION.SDK_INT <= VERSION_CODES.R) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(
                UiModeManager.MODE_NIGHT_YES
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dayNightSettings = settingsDataStore.data.stateIn(this).value[stringPreferencesKey(
                SETTINGS_KEY_DAY_NIGHT_MODE
            )]
            if (dayNightSettings != null) {
                Utils.setMode(this@MyApplication, DayNightMode.valueOf(dayNightSettings))
            }
        }

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        fetchRemoveConfigAndRunTask()
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

    private fun scheduleSyncTask(context: Context, interval: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val syncTask = PeriodicWorkRequestBuilder<SyncWorker>(
            interval, TimeUnit.HOURS, // repeatInterval (the period cycle)
            15, TimeUnit.MINUTES
        ) // flexInterval
            .setInputData(Data.Builder().putString(SyncWorker.SOURCE, Source.PERIODIC.name).build())
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "Sync task",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                syncTask
            )
    }

    private fun fetchRemoveConfigAndRunTask() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings: FirebaseRemoteConfigSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf("periodic_sync_duration" to 3))

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("periodic_sync_duration: ${remoteConfig.getLong("periodic_sync_duration")}")
                scheduleSyncTask(this, remoteConfig.getLong("periodic_sync_duration"))
            } else {
                Timber.w("Failed to fetch remote config")
            }
        }
    }
}

val myModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryDataDatabase::class.java, "lottery"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryLogDatabase::class.java, "lottery-log"
        ).fallbackToDestructiveMigration().build()
    }

    single { LotteryTableViewModel(get(), get(), get(), get()) }
    single { ParseService() }
    single { SyncUseCase(get()) }
    single { DisplayUseCase() }
    single { Preferences(get()) }
    single { SettingsUseCase(get()) }
    single { Analytics() }
    single { LotteryLogUseCase() }
    single { LotteryLogViewModel(get()) }
    single { PossibilityScreenViewModel(get(), get()) }
}