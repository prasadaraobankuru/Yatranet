package com.yatra.dependencies;

/**
 * Created by Prasad on 6/14/2018.
 */

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
/**
 * Created by vipul on 9/12/2015.
 */
public class MyService extends JobService {

    // This method is called when the service instance
    // is created
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "myService created");
    }

    // This method is called when the service instance
    // is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MyService", "myService destroyed");
    }

    // This method is called when the scheduled job
    // is started
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("MyService", "on start job");
        return true;
    }

    // This method is called when the scheduled job
    // is stopped
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("MyService", "on stop job");
        return true;
    }

    MainActivity myMainActivity;

    public void setUICallback(MainActivity activity) {
        myMainActivity = activity;
    }


    // This method is called when the start command
    // is fired
    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {
        Messenger callback = intent.getParcelableExtra("messenger");
        Message m = Message.obtain();
        m.what = 2;
        m.obj = this;
        try {
            callback.send(m);
        } catch (RemoteException e) {
            Log.e("MyService", "Error passing service object back to activity.");
        }
        return START_NOT_STICKY;
    }

    // Method that schedules the job
    public void scheduleJob(JobInfo build) {
        Log.i("MyService","Scheduling job");
        JobScheduler jobScheduler = (JobScheduler)getSystemService
                (Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(build);
    }
}