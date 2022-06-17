package com.motorola.settings;

import android.app.WallpaperManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.C1986R$integer;
import com.motorola.android.provider.MotorolaSettings;
import java.io.File;
import java.io.IOException;

public class CarrierMediaUpdateJobService extends JobService {
    private static final boolean DEBUG = (!"user".equals(Build.TYPE));
    private String mChannelId;
    private Handler mHandler;
    private HandlerThread mHandlerThread = null;
    private JobParameters mJobParameters;

    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.d("CarrierMediaUpdateService", "onCreate()");
        }
        HandlerThread handlerThread = new HandlerThread("CarrierMediaUpdateService");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d("CarrierMediaUpdateService", "onDestroy()");
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        super.onDestroy();
    }

    public boolean onStartJob(JobParameters jobParameters) {
        boolean z = DEBUG;
        if (z) {
            Log.d("CarrierMediaUpdateService", "onStartJob()");
        }
        this.mJobParameters = jobParameters;
        this.mChannelId = getChannelId(this);
        File file = new File("/product/carrier/" + this.mChannelId + "/media");
        if (file.exists() && file.isDirectory()) {
            if (z) {
                Log.d("CarrierMediaUpdateService", "carrier media root directory = " + file.getAbsolutePath());
            }
            this.mHandler.post(new CarrierMediaUpdateJobService$$ExternalSyntheticLambda0(this, file));
            return true;
        } else if (!z) {
            return false;
        } else {
            Log.d("CarrierMediaUpdateService", "carrier media root directory" + file.getAbsolutePath() + " does not exist.");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(File file) {
        updateCarrierMediaPath(file);
        jobFinished(this.mJobParameters, false);
    }

    public boolean onStopJob(JobParameters jobParameters) {
        if (!DEBUG) {
            return false;
        }
        Log.d("CarrierMediaUpdateService", "onStopJob()");
        return false;
    }

    private void updateCarrierDefaultWallpaper(File file) {
        try {
            if (DEBUG) {
                Log.d("CarrierMediaUpdateService", "Trigger wallpaper update.");
            }
            WallpaperManager.getInstance(this).clear(1);
        } catch (IOException e) {
            if (DEBUG) {
                Log.d("CarrierMediaUpdateService", "Error when clearing the wallpaper : " + e);
            }
        }
        try {
            WallpaperManager.getInstance(this).clear(4);
        } catch (IOException e2) {
            if (DEBUG) {
                Log.d("CarrierMediaUpdateService", "Error when clearing the cli wallpaper : " + e2);
            }
        }
    }

    private void updateCarrierMediaPath(File file) {
        String str = SystemProperties.get("persist.carrier.media.dir", "");
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.equals(str)) {
            if (DEBUG) {
                Log.d("CarrierMediaUpdateService", "Set new media root dir to system property: " + absolutePath);
            }
            SystemProperties.set("persist.carrier.media.dir", absolutePath);
            updateCarrierDefaultWallpaper(file);
        } else if (DEBUG) {
            Log.d("CarrierMediaUpdateService", "Media content dir hasn't changed. Do nothing.");
        }
    }

    public static void scheduleCarrierMediaUpdateOperation(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (jobScheduler == null) {
            Log.e("CarrierMediaUpdateService", "Could not start JobService. JobScheduler is null.");
            return;
        }
        int i = C1986R$integer.job_carrier_media_update;
        if (jobScheduler.getPendingJob(i) == null) {
            JobInfo.Builder builder = new JobInfo.Builder(i, new ComponentName(context, CarrierMediaUpdateJobService.class));
            builder.setMinimumLatency(1000);
            jobScheduler.schedule(builder.build());
        } else if (DEBUG) {
            Log.d("CarrierMediaUpdateService", "Job already scheduled.");
        }
    }

    public static String getChannelId(Context context) {
        return MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
    }
}
