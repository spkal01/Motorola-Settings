package com.motorola.settings.resetsettings;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public final class ResetDeviceSettingsManagerTask {
    private static ResetDeviceSettingsManagerTask INSTANCE = null;
    /* access modifiers changed from: private */
    public static final String TAG = "ResetDeviceSettingsManagerTask";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public boolean mInProgress;
    /* access modifiers changed from: private */
    public ResetDeviceSettingsManagerListener mListener;

    interface ResetDeviceSettingsManagerListener {
        void onInit();

        void onSuccess();
    }

    private ResetDeviceSettingsManagerTask() {
    }

    public static ResetDeviceSettingsManagerTask getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResetDeviceSettingsManagerTask();
        }
        return INSTANCE;
    }

    public void attach(Context context, ResetDeviceSettingsManagerListener resetDeviceSettingsManagerListener) {
        this.mContext = context;
        this.mListener = resetDeviceSettingsManagerListener;
    }

    public void detach() {
        this.mContext = null;
        this.mListener = null;
    }

    public void reset() {
        if (!isInProgress()) {
            new ResetDeviceAsyncTask().execute(new Void[0]);
        }
    }

    public boolean isInProgress() {
        return this.mInProgress;
    }

    class ResetDeviceAsyncTask extends AsyncTask<Void, Void, Void> {
        ResetDeviceAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            boolean unused = ResetDeviceSettingsManagerTask.this.mInProgress = true;
            if (ResetDeviceSettingsManagerTask.this.mListener != null) {
                ResetDeviceSettingsManagerTask.this.mListener.onInit();
            }
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            new ResetDeviceSettingsManager(ResetDeviceSettingsManagerTask.this.mContext).resetDeviceSettings();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            boolean unused = ResetDeviceSettingsManagerTask.this.mInProgress = false;
            try {
                Thread.sleep(3000);
                if (ResetDeviceSettingsManagerTask.this.mListener != null) {
                    ResetDeviceSettingsManagerTask.this.mListener.onSuccess();
                }
            } catch (InterruptedException e) {
                Log.e(ResetDeviceSettingsManagerTask.TAG, "Unable to clean listener", e);
            }
        }
    }
}
