package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myapplication.vm.MyEvents
import com.example.myapplication.vm.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val ERROR = -1
        private const val SUCCESS = 0

        private const val NOTIFICATION_ID = 10001
        private const val CHANNEL_ID = "Sync Task"
    }

    private val viewModel: MyViewModel by inject(MyViewModel::class.java)
    private val onStopChannel = Channel<Int>()
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        android.util.Log.i("QQQQ", "SyncWorker init")
        viewModel.viewModelScope.launch {
            viewModel.eventStateSharedFlow.collect {
                android.util.Log.v("QQQQ", "event: $it")
                when (it) {
                    is MyEvents.EndSync -> {
                        onStopChannel.send(if (it.error == null) SUCCESS else ERROR)
                    }
                    else -> {
                        // ignore
                    }
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        android.util.Log.v("QQQQ", "doWork")
        setForegroundAsync(createForegroundInfo())
        viewModel.handleEvent(MyEvents.StartSync)
        val result = onStopChannel.receive()
        android.util.Log.v("QQQQ", "done: $result")
        return if (result == SUCCESS) {
            Result.success()
        } else {
            Result.failure()
        }
//        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val title = "Sync"
        val cancel = "Cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        createChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("Syncing")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        val name = "Sync"
        val descriptionText = "Sync Lottery data"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }
}