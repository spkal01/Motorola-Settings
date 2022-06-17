package com.motorola.settings;

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
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C1986R$integer;
import com.motorola.android.provider.MotorolaSettings;

public class CarrierPropUpdateJobService extends JobService {
    private static final boolean DEBUG = (!"user".equals(Build.TYPE));
    private String mChannelId;
    private Handler mHandler;
    private HandlerThread mHandlerThread = null;
    private JobParameters mJobParameters;

    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.d("CarrierPropUpdateJobService", "onCreate()");
        }
        HandlerThread handlerThread = new HandlerThread("CarrierPropUpdateJobService");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d("CarrierPropUpdateJobService", "onDestroy()");
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        super.onDestroy();
    }

    public boolean onStartJob(JobParameters jobParameters) {
        if (DEBUG) {
            Log.d("CarrierPropUpdateJobService", "onStartJob()");
        }
        this.mJobParameters = jobParameters;
        String channelId = getChannelId(this);
        this.mChannelId = channelId;
        if (TextUtils.isEmpty(channelId)) {
            return false;
        }
        setCarrierSpecificSystemProperties();
        return false;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        if (!DEBUG) {
            return false;
        }
        Log.d("CarrierPropUpdateJobService", "onStopJob()");
        return false;
    }

    private void setCarrierSpecificSystemProperties() {
        String str = this.mChannelId;
        str.hashCode();
        if (str.equals("oraeu")) {
            ensureProductBrand1Property("orange");
        }
    }

    private void ensureProductBrand1Property(String str) {
        String str2 = SystemProperties.get("ro.product.brand1");
        if (TextUtils.isEmpty(str2)) {
            setProp("ro.product.brand1", str);
            return;
        }
        Log.e("CarrierPropUpdateJobService", "ro.product.brand1 is already set to " + str2);
    }

    private void setProp(String str, String str2) {
        try {
            SystemProperties.set(str, str2);
        } catch (Exception e) {
            Log.e("CarrierPropUpdateJobService", "Failed to set " + str + " to " + str2, e);
        }
    }

    public static void scheduleCarrierPropUpdateOperation(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (jobScheduler == null) {
            Log.e("CarrierPropUpdateJobService", "Could not start JobService. JobScheduler is null.");
            return;
        }
        int i = C1986R$integer.job_carrier_prop_init;
        if (jobScheduler.getPendingJob(i) == null) {
            JobInfo.Builder builder = new JobInfo.Builder(i, new ComponentName(context, CarrierPropUpdateJobService.class));
            builder.setMinimumLatency(0);
            jobScheduler.schedule(builder.build());
        } else if (DEBUG) {
            Log.d("CarrierPropUpdateJobService", "Job already scheduled.");
        }
    }

    public static String getChannelId(Context context) {
        return MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
    }
}
