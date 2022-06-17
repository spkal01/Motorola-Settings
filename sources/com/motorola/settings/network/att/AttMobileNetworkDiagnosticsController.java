package com.motorola.settings.network.att;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.MotoTelephonyTogglePreferenceController;
import java.time.Duration;

public class AttMobileNetworkDiagnosticsController extends MotoTelephonyTogglePreferenceController implements LifecycleObserver, OnCreate, OnResume, OnStart, OnStop {
    private static final String EXTRA_IQI_STATE = "iqi_state";
    private static final String KEY_TIME_LEFT_MILLIS = "time_left_in_millis";
    private static final String KEY_TIME_MILLIS = "key_time_in_millis";
    public static final int REQUEST_CODE_IQI_DISABLE = 102;
    private static final int RESULT_IQI_DISABLE_CONFIRM = 4;
    private static final String SHARED_PREFERENCES_NAME = "mnd_prefs";
    private static final String TAG = "AttNetworkDiagnostics";
    private static final long TIMEOUT = Duration.ofSeconds(30).toMillis();
    private static final long TIMER_INTERVAL = 1000;
    private static long mTimeInMillis = TIMEOUT;
    /* access modifiers changed from: private */
    public static long mTimeLeftInMillis = TIMEOUT;
    private static CountDownTimer mTimer;
    /* access modifiers changed from: private */
    public SwitchPreference mButtonDiagnostic;
    private Fragment mFragment;
    private BroadcastReceiver mIqiReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AttMobileNetworkDiagnosticsController.this.stopTimer(true);
            if (AttMobileNetworkDiagnosticsController.this.mButtonDiagnostic != null) {
                AttMobileNetworkDiagnosticsController.this.mButtonDiagnostic.setEnabled(true);
            }
        }
    };

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

    public boolean setChecked(boolean z) {
        return false;
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AttMobileNetworkDiagnosticsController(Context context, String str) {
        super(context, str);
    }

    public void onCreate(Bundle bundle) {
        mTimeInMillis = getMdnSharedPrefs(KEY_TIME_MILLIS, TIMEOUT);
        mTimeLeftInMillis = getMdnSharedPrefs(KEY_TIME_LEFT_MILLIS, TIMEOUT);
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mButtonDiagnostic = switchPreference;
        if (switchPreference != null) {
            super.displayPreference(preferenceScreen);
            updateButtonDiagnostic();
        }
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr5934Enabled(this.mContext, i) ? 1 : 2;
    }

    public void onStop() {
        stopTimer(false);
        try {
            this.mContext.unregisterReceiver(this.mIqiReceiver);
        } catch (Exception e) {
            MotoMnsLog.loge(TAG, "Exception while unregistering receiver: " + e);
        }
    }

    public void onStart() {
        this.mContext.registerReceiver(this.mIqiReceiver, new IntentFilter("com.motorola.intent.action.IQI_SETTING_CHANGE_COMPLETE"), "com.att.iqi.permission.RECEIVE_UPLOAD_NOTIFICATIONS", (Handler) null);
    }

    public void onResume() {
        updateButtonDiagnostic();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == 4) {
            this.mButtonDiagnostic.setEnabled(false);
            startTimer(TIMEOUT);
        }
    }

    private void updateButtonDiagnostic() {
        SwitchPreference switchPreference = this.mButtonDiagnostic;
        if (switchPreference != null) {
            switchPreference.setChecked(getDiagnsoticEnabled(this.mContext));
            if (mTimeLeftInMillis > TIMER_INTERVAL) {
                long currentTimeMillis = System.currentTimeMillis() - mTimeInMillis;
                if (currentTimeMillis < mTimeLeftInMillis) {
                    this.mButtonDiagnostic.setEnabled(false);
                    startTimer(mTimeLeftInMillis - currentTimeMillis);
                    mTimeInMillis = TIMEOUT;
                    mTimeLeftInMillis = TIMEOUT;
                }
            }
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        boolean isChecked = this.mButtonDiagnostic.isChecked();
        Intent intent = new Intent("com.motorola.intent.action.IQI_SERVICE_STATE_CHANGE");
        intent.putExtra(EXTRA_IQI_STATE, !isChecked);
        intent.setClassName("com.motorola.iqimotmetrics", "com.motorola.iqimotmetrics.IqiStateChangeActivity");
        this.mFragment.startActivityForResult(intent, 102);
        return false;
    }

    public boolean isChecked() {
        return getDiagnsoticEnabled(this.mContext);
    }

    private boolean getDiagnsoticEnabled(Context context) {
        return MotorolaSettings.Global.getInt(context.getContentResolver(), "mobile_network_diagnostics", 1) > 0;
    }

    private void startTimer(long j) {
        if (mTimer != null) {
            stopTimer(true);
        }
        C19352 r1 = new CountDownTimer(j, TIMER_INTERVAL) {
            public void onTick(long j) {
                long unused = AttMobileNetworkDiagnosticsController.mTimeLeftInMillis = j;
            }

            public void onFinish() {
                long unused = AttMobileNetworkDiagnosticsController.mTimeLeftInMillis = AttMobileNetworkDiagnosticsController.TIMEOUT;
                if (AttMobileNetworkDiagnosticsController.this.mButtonDiagnostic == null) {
                    MotoMnsLog.logd(AttMobileNetworkDiagnosticsController.TAG, "Ignore timeout!");
                    return;
                }
                MotoMnsLog.logd(AttMobileNetworkDiagnosticsController.TAG, "Timeout ");
                AttMobileNetworkDiagnosticsController.this.mButtonDiagnostic.setEnabled(true);
            }
        };
        mTimer = r1;
        r1.start();
    }

    /* access modifiers changed from: private */
    public void stopTimer(boolean z) {
        if (mTimer != null) {
            long j = TIMEOUT;
            if (z) {
                mTimeLeftInMillis = TIMEOUT;
            }
            long j2 = mTimeLeftInMillis;
            if (j2 <= TIMER_INTERVAL) {
                mTimeLeftInMillis = TIMEOUT;
                mTimeInMillis = TIMEOUT;
            } else {
                if (j2 != TIMEOUT) {
                    j = System.currentTimeMillis();
                }
                mTimeInMillis = j;
            }
            setMdnSharedPrefs(KEY_TIME_LEFT_MILLIS, mTimeLeftInMillis);
            setMdnSharedPrefs(KEY_TIME_MILLIS, mTimeInMillis);
            mTimer.cancel();
            mTimer = null;
        }
    }

    private long getMdnSharedPrefs(String str, long j) {
        return this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).getLong(str, j);
    }

    private void setMdnSharedPrefs(String str, long j) {
        this.mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).edit().putLong(str, j).apply();
    }
}
