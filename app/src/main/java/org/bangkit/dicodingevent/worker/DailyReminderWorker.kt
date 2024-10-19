package org.bangkit.dicodingevent.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.bangkit.dicodingevent.DicodingEventApp.Companion.CHANNEL_ID
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.ui.activities.DetailActivity

@HiltWorker
class DailyReminderWorker @AssistedInject constructor (
    @Assisted private val repository: DicodingEventRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "DailyReminderWorker"
    }

    init {
        Log.d(TAG, "worker: created")
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "worker: doWork")
        return try {
            val event = repository.getClosestEvent()
            sendNotification(event.data)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(event: DicodingEventModel?) {
        if (event == null) return

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT, event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_event_24)
            .setContentTitle(event.name)
            .setContentText(event.summary)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.notify(event.id.hashCode(), notificationBuilder.build())
    }
}