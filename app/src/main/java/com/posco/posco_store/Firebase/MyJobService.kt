package com.posco.posco_store.Firebase

import android.annotation.SuppressLint
import android.app.job.JobService
import android.app.job.JobParameters
import android.util.Log

@SuppressLint("SpecifyJobSchedulerIdRange")
class MyJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d(TAG, "Performing long running task in scheduled job")
        //  add long running task here.
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    companion object {
        private const val TAG = "MyJobService"
    }
}