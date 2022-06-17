package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.accessibility.rtt.TelecomUtil;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class RTTSettingPreferenceController extends BasePreferenceController {
    private static final String DIALER_RTT_CONFIGURATION = "dialer_rtt_configuration";
    private static final String TAG = "RTTSettingsCtr";
    private final CarrierConfigManager mCarrierConfigManager;
    private final Context mContext;
    private final String mDialerPackage;
    private final CharSequence[] mModes;
    private final PackageManager mPackageManager;
    Intent mRTTIntent;

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

    public RTTSettingPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mModes = context.getResources().getTextArray(C1978R$array.rtt_setting_mode);
        this.mDialerPackage = context.getString(C1992R$string.config_rtt_setting_package_name);
        this.mPackageManager = context.getPackageManager();
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mRTTIntent = new Intent(context.getString(C1992R$string.config_rtt_setting_intent_action));
        Log.d(TAG, "init controller");
    }

    public int getAvailabilityStatus() {
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(this.mRTTIntent, 0);
        if (queryIntentActivities == null || queryIntentActivities.isEmpty() || !isRttSettingSupported()) {
            return 3;
        }
        return 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.findPreference(getPreferenceKey()).setIntent(this.mRTTIntent);
    }

    public CharSequence getSummary() {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), DIALER_RTT_CONFIGURATION, 0);
        Log.d(TAG, "DIALER_RTT_CONFIGURATION value =  " + i);
        return this.mModes[i];
    }

    /* access modifiers changed from: package-private */
    public boolean isRttSettingSupported() {
        Log.d(TAG, "isRttSettingSupported [start]");
        if (!isDefaultDialerSupportedRTT(this.mContext)) {
            Log.d(TAG, "Dialer doesn't support RTT.");
            return false;
        }
        for (PhoneAccountHandle next : TelecomUtil.getCallCapablePhoneAccounts(this.mContext)) {
            int subIdForPhoneAccountHandle = TelecomUtil.getSubIdForPhoneAccountHandle(this.mContext, next);
            Log.d(TAG, "subscription id for the device: " + subIdForPhoneAccountHandle);
            boolean isRttSupportedByTelecom = isRttSupportedByTelecom(next);
            Log.d(TAG, "rtt calling supported by telecom:: " + isRttSupportedByTelecom);
            if (isRttSupportedByTelecom) {
                if (this.mCarrierConfigManager.getConfigForSubId(subIdForPhoneAccountHandle) == null || !getBooleanCarrierConfig("ignore_rtt_mode_setting_bool")) {
                    Log.d(TAG, "IGNORE_RTT_MODE_SETTING_BOOL is false.");
                } else {
                    Log.d(TAG, "RTT visibility setting is supported.");
                    return true;
                }
            }
        }
        Log.d(TAG, "isRttSettingSupported [Not support]");
        return false;
    }

    private boolean isRttSupportedByTelecom(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = TelecomUtil.getTelecomManager(this.mContext).getPhoneAccount(phoneAccountHandle);
        if (phoneAccount == null || !phoneAccount.hasCapabilities(4096)) {
            return false;
        }
        Log.d(TAG, "Phone account has RTT capability.");
        return true;
    }

    private boolean getBooleanCarrierConfig(String str) {
        if (this.mCarrierConfigManager == null) {
            return CarrierConfigManager.getDefaultConfig().getBoolean(str);
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(SubscriptionManager.getDefaultVoiceSubscriptionId());
        if (configForSubId != null) {
            return configForSubId.getBoolean(str);
        }
        return CarrierConfigManager.getDefaultConfig().getBoolean(str);
    }

    private static boolean isDefaultDialerSupportedRTT(Context context) {
        return TextUtils.equals(context.getString(C1992R$string.config_rtt_setting_package_name), TelecomUtil.getTelecomManager(context).getDefaultDialerPackage());
    }
}
