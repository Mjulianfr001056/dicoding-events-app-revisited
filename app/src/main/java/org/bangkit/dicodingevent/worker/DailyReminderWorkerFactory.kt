package org.bangkit.dicodingevent.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import javax.inject.Inject

class DailyReminderWorkerFactory @Inject constructor(
    private val repository: DicodingEventRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = DailyReminderWorker(repository, appContext, workerParameters)
}