package com.appleto.loppydriver.service

import android.app.job.JobParameters
import android.content.Intent
import android.app.job.JobService
import android.os.Build
import com.appleto.loppydriver.helper.Utils


class TestJobService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        val service = Intent(applicationContext, LocationService::class.java)
        applicationContext.startService(service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.scheduleJob(applicationContext)
        } // reschedule the job
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        private val TAG = "SyncService"
    }

}