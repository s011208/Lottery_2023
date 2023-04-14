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
import com.example.myapplication.compose.lotterytable.vm.LotteryTableEvents
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import com.example.myapplication.compose.lotterytable.vm.Source
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class SyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val ERROR = -1
        private const val SUCCESS = 0

        private const val NOTIFICATION_ID = 10001
        private const val CHANNEL_ID = "Sync Task"

        internal const val SOURCE = "source"
    }

    private val viewModel: LotteryTableViewModel by inject(LotteryTableViewModel::class.java)
    private val onStopChannel = Channel<Int>()
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        Timber.d("SyncWorker init")
        viewModel.viewModelScope.launch {
            viewModel.eventStateSharedFlow.collect {
                Timber.d("event: $it")
                when (it) {
                    is LotteryTableEvents.EndSync -> {
                        onStopChannel.send(SUCCESS)
                    }
                    is LotteryTableEvents.SyncingProgress -> {
                        setForegroundAsync(
                            context.getString(R.string.syncing_title),
                            context.getString(R.string.syncing_content)
                        )
                    }
                    else -> {
                        // ignore
                    }
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        val source = Source.valueOf(inputData.getString(SOURCE) ?: Source.UNKNOWN.name)
        Timber.d("doWork, source: $source")
        setForegroundAsync("Sync is running", "Preparing for sync")
        viewModel.handleEvent(LotteryTableEvents.StartSync(source))
        val result = onStopChannel.receive()
        Timber.d("done: $result")
        return if (result == SUCCESS) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun setForegroundAsync(title: String, content: String) {
        setForegroundAsync(createForegroundInfo(title, content))
    }

    private fun createForegroundInfo(title: String, content: String): ForegroundInfo {
        val cancel = "Cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        createChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(content)
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