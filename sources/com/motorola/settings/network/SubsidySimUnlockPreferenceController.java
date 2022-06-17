package com.motorola.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.android.subsidyutils.SubsidyUtil;

public class SubsidySimUnlockPreferenceController extends MotoTelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    public static final String KEY_SUBSIDY_UNLOCK = "subsidy_sim_unlock";
    private static final String SUBSIDY_UNLOCK_ACTION = "com.motorola.comcast.intent.action.SUBSIDY_UNLOCK";
    private static final String TAG = "SubsidySimUnlockPreferenceController";
    private static int mSubType;
    /* access modifiers changed from: private */
    public Preference mPreference;
    private ContentObserver mSIMLockObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            SubsidySimUnlockPreferenceController subsidySimUnlockPreferenceController = SubsidySimUnlockPreferenceController.this;
            subsidySimUnlockPreferenceController.updateState(subsidySimUnlockPreferenceController.mPreference);
        }
    };
    private GetSubTypeTask mTask;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SubsidySimUnlockPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return (!MotoTelephonyFeature.isSubsidyLockSupported(this.mContext) || !isSubsidyLocked(this.mContext)) ? 2 : 1;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onStart() {
        if (MotoTelephonyFeature.isSubsidyLockSupported(this.mContext)) {
            GetSubTypeTask getSubTypeTask = new GetSubTypeTask();
            this.mTask = getSubTypeTask;
            getSubTypeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Global.getUriFor("carrier_subsidy_lock_enabled"), false, this.mSIMLockObserver);
    }

    public void onStop() {
        GetSubTypeTask getSubTypeTask = this.mTask;
        if (getSubTypeTask != null) {
            getSubTypeTask.cancel(false);
            this.mTask = null;
        }
        this.mContext.getContentResolver().unregisterContentObserver(this.mSIMLockObserver);
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Intent intent = new Intent(SUBSIDY_UNLOCK_ACTION);
        intent.addFlags(16777216);
        this.mContext.sendBroadcast(intent);
        return true;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!isSubsidyLocked(this.mContext)) {
            if (preference != null) {
                preference.setVisible(false);
            }
        } else if (preference != null) {
            preference.setVisible(true);
        }
    }

    private static boolean isSubsidyLocked(Context context) {
        boolean z = false;
        int i = MotorolaSettings.Global.getInt(context.getContentResolver(), "carrier_subsidy_lock_enabled", 0);
        mSubType = i;
        if (i == IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER.ordinal()) {
            z = true;
        }
        MotoMnsLog.logd(TAG, "isSubsidyLocked :" + z + " @" + i);
        return z;
    }

    private class GetSubTypeTask extends AsyncTask<Void, Void, Integer> {
        private GetSubTypeTask() {
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Void... voidArr) {
            int i;
            try {
                i = SubsidyUtil.getSubType(SubsidySimUnlockPreferenceController.this.mContext);
            } catch (Exception e) {
                MotoMnsLog.loge(SubsidySimUnlockPreferenceController.TAG, e.toString());
                i = 0;
            }
            return Integer.valueOf(i);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer num) {
            if (SubsidySimUnlockPreferenceController.this.mContext != null) {
                int i = MotorolaSettings.Global.getInt(SubsidySimUnlockPreferenceController.this.mContext.getContentResolver(), "carrier_subsidy_lock_enabled", 0);
                MotoMnsLog.logd(SubsidySimUnlockPreferenceController.TAG, "SettingsSubType:" + i + " subType:" + num);
                if (num.intValue() != i) {
                    MotorolaSettings.Global.putInt(SubsidySimUnlockPreferenceController.this.mContext.getContentResolver(), "carrier_subsidy_lock_enabled", num.intValue());
                    if (num.intValue() == IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER.ordinal() && SubsidySimUnlockPreferenceController.this.mPreference != null) {
                        SubsidySimUnlockPreferenceController subsidySimUnlockPreferenceController = SubsidySimUnlockPreferenceController.this;
                        subsidySimUnlockPreferenceController.updateState(subsidySimUnlockPreferenceController.mPreference);
                    }
                }
            }
        }
    }
}
