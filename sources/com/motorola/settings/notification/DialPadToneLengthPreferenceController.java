package com.motorola.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class DialPadToneLengthPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    private static final String KEY = "dial_pad_tones_length";
    private final CarrierConfigManager mCarrierConfigManager;
    private final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            super.onChange(z);
            DialPadToneLengthPreferenceController.this.updatePreference();
        }
    };
    private final Uri mDtmfToneLengthUri = Settings.System.getUriFor("dtmf_tone_type");
    private final Uri mDtmfToneUri = Settings.System.getUriFor("dtmf_tone");
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener(Looper.getMainLooper()) {
        public void onServiceStateChanged(ServiceState serviceState) {
            DialPadToneLengthPreferenceController.this.updatePreference();
        }
    };
    private ListPreference mPreference;
    private final TelephonyManager mTelephonyManager;

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

    public DialPadToneLengthPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public int getAvailabilityStatus() {
        PersistableBundle config = this.mCarrierConfigManager.getConfig();
        if (config == null) {
            return 3;
        }
        boolean z = config.getBoolean("hide_carrier_network_settings_bool");
        if (!this.mTelephonyManager.canChangeDtmfToneLength() || z) {
            return 3;
        }
        return 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = listPreference;
        if (listPreference != null) {
            listPreference.setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int findIndexOfValue = this.mPreference.findIndexOfValue((String) obj);
        Settings.System.putInt(this.mContext.getContentResolver(), "dtmf_tone_type", findIndexOfValue);
        updatePreference(findIndexOfValue);
        return true;
    }

    public void onResume() {
        if (this.mPreference != null) {
            this.mContext.getContentResolver().registerContentObserver(this.mDtmfToneUri, true, this.mContentObserver);
            this.mContext.getContentResolver().registerContentObserver(this.mDtmfToneLengthUri, true, this.mContentObserver);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 1);
        }
    }

    public void onPause() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    /* access modifiers changed from: private */
    public void updatePreference() {
        updatePreference(Settings.System.getInt(this.mContext.getContentResolver(), "dtmf_tone_type", 0));
    }

    private void updatePreference(int i) {
        this.mPreference.setVisible(isAvailable());
        this.mPreference.setEnabled(isDtmfToneEnabled());
        this.mPreference.setValueIndex(i);
        ListPreference listPreference = this.mPreference;
        listPreference.setSummary(listPreference.getEntries()[i]);
    }

    private boolean isDtmfToneEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "dtmf_tone", 0) == 1;
    }
}
