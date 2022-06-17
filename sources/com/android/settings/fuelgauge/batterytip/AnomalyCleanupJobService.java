package com.android.settings.fuelgauge.batterytip;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import com.android.settings.C1986R$integer;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;

public class AnomalyCleanupJobService extends JobService {
    static final long CLEAN_UP_FREQUENCY_MS = TimeUnit.DAYS.toMillis(1);

    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleCleanUp(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        ComponentName componentName = new ComponentName(context, AnomalyCleanupJobService.class);
        int i = C1986R$integer.job_anomaly_clean_up;
        JobInfo.Builder persisted = new JobInfo.Builder(i, componentName).setPeriodic(CLEAN_UP_FREQUENCY_MS).setRequiresDeviceIdle(true).setRequiresCharging(true).setPersisted(true);
        if (jobScheduler.getPendingJob(i) == null && jobScheduler.schedule(persisted.build()) != 1) {
            Log.i("AnomalyCleanUpJobService", "Anomaly clean up job service schedule failed.");
        }
    }

    public boolean onStartJob(JobParameters jobParameters) {
        ThreadUtils.postOnBackgroundThread((Runnable) new AnomalyCleanupJobService$$ExternalSyntheticLambda0(this, BatteryDatabaseManager.getInstance(this), new BatteryTipPolicy(this), jobParameters));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(BatteryDatabaseManager batteryDatabaseManager, BatteryTipPolicy batteryTipPolicy, JobParameters jobParameters) {
        batteryDatabaseManager.deleteAllAnomaliesBeforeTimeStamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis((long) batteryTipPolicy.dataHistoryRetainDay));
        jobFinished(jobParameters, false);
    }
}
