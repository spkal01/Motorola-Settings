package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedPreferredNetworkModePreferenceController;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwPreferredNetworkModePreferenceController extends ExtendedPreferredNetworkModePreferenceController implements LifecycleObserver {
    private static final int INVALID_RES = -1;
    private static final String LOG_TAG = "WMPrefController";
    private static final int MODE_CDMA = 100;
    private static final int MODE_GLOBAL = 300;
    private static final int MODE_GSM = 200;
    private static final int MODE_LTE_CDMA = 400;
    private static final int MODE_LTE_GSM = 500;
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private PersistableBundle mCarrierConfig;
    private boolean mIs5GAvailable;
    private ListPreference mListPreference;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showNoSIMDialog$1(DialogInterface dialogInterface, int i) {
    }

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

    public VzwPreferredNetworkModePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle, int i) {
        super.init(i);
        this.mCarrierConfig = this.mCarrierConfigManager.getConfigForSubId(i);
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new C1940x901f682a(this));
        }
        lifecycle.addObserver(this);
        long allowedNetworkTypes = this.mTelephonyManager.getAllowedNetworkTypes();
        Log.d(LOG_TAG, "allowedNetworkTypes: " + allowedNetworkTypes);
        int i2 = ((allowedNetworkTypes & 524288) > 524288 ? 1 : ((allowedNetworkTypes & 524288) == 524288 ? 0 : -1));
        boolean z = true;
        if (!(i2 == 0) || !MotoMobileNetworkUtils.modemSupports5g(this.mContext, i)) {
            z = false;
        }
        this.mIs5GAvailable = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        updateState(this.mListPreference);
    }

    public int getAvailabilityStatus(int i) {
        if ((!MotoTelephonyFeature.isCdmaLessDevice(this.mContext, i)) && (super.getAvailabilityStatus(i) == 0)) {
            return 0;
        }
        return 2;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mListPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            this.mListPreference.setEntries(C1978R$array.preferred_network_mode_choices_vzw);
            if (this.mIs5GAvailable) {
                this.mListPreference.setEntryValues(C1978R$array.preferred_network_mode_values_vzw_5G);
            } else {
                this.mListPreference.setEntryValues(C1978R$array.preferred_network_mode_values_vzw);
            }
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            ListPreference listPreference = (ListPreference) preference;
            int preferredNetworkMode = getPreferredNetworkMode();
            listPreference.setValue(Integer.toString(mapNetworkMode(preferredNetworkMode)));
            listPreference.setSummary(getPreferredNetworkModeSummaryResId(preferredNetworkMode));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            return super.onPreferenceChange(preference, obj);
        }
        int parseInt = Integer.parseInt((String) obj);
        if (parseInt < 0 || parseInt > 27) {
            Log.e(LOG_TAG, "onPreferenceChange Invalid Network Mode (" + parseInt + ") Chosen. Ignore mode");
            return false;
        }
        int simState = this.mTelephonyManager.getSimState();
        Log.d(LOG_TAG, "onPreferenceChange SIM getState status:" + simState);
        int checkModeCategory = checkModeCategory(parseInt);
        if ((MODE_GSM != checkModeCategory && MODE_LTE_GSM != checkModeCategory && MODE_GLOBAL != checkModeCategory) || simState != 1) {
            return super.onPreferenceChange(preference, obj);
        }
        showNoSIMDialog();
        return false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AllowedNetworkTypesListener allowedNetworkTypesListener;
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig) && (allowedNetworkTypesListener = this.mAllowedNetworkTypesListener) != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AllowedNetworkTypesListener allowedNetworkTypesListener;
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig) && (allowedNetworkTypesListener = this.mAllowedNetworkTypesListener) != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002a, code lost:
        r1 = com.android.settings.C1992R$string.preferred_network_mode_lte_cdma_summary;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getPreferredNetworkModeSummaryResId(int r3) {
        /*
            r2 = this;
            android.os.PersistableBundle r0 = r2.mCarrierConfig
            boolean r0 = com.motorola.settings.network.MotoTelephonyFeature.isVzwWorldPhoneEnabled(r0)
            if (r0 != 0) goto L_0x000d
            int r2 = super.getPreferredNetworkModeSummaryResId(r3)
            return r2
        L_0x000d:
            r0 = -1
            if (r3 == 0) goto L_0x0036
            r1 = 4
            if (r3 == r1) goto L_0x0033
            r1 = 12
            if (r3 == r1) goto L_0x0030
            r1 = 20
            if (r3 == r1) goto L_0x0030
            r1 = 22
            if (r3 == r1) goto L_0x002d
            switch(r3) {
                case 8: goto L_0x002a;
                case 9: goto L_0x0030;
                case 10: goto L_0x002d;
                default: goto L_0x0022;
            }
        L_0x0022:
            switch(r3) {
                case 16: goto L_0x0036;
                case 17: goto L_0x0030;
                case 18: goto L_0x0036;
                default: goto L_0x0025;
            }
        L_0x0025:
            switch(r3) {
                case 25: goto L_0x002a;
                case 26: goto L_0x0030;
                case 27: goto L_0x002d;
                default: goto L_0x0028;
            }
        L_0x0028:
            r1 = r0
            goto L_0x0038
        L_0x002a:
            int r1 = com.android.settings.C1992R$string.preferred_network_mode_lte_cdma_summary
            goto L_0x0038
        L_0x002d:
            int r1 = com.android.settings.C1992R$string.preferred_network_mode_global_summary
            goto L_0x0038
        L_0x0030:
            int r1 = com.android.settings.C1992R$string.preferred_network_mode_lte_gsm_umts_summary
            goto L_0x0038
        L_0x0033:
            int r1 = com.android.settings.C1992R$string.preferred_network_mode_cdma_only_summary
            goto L_0x0038
        L_0x0036:
            int r1 = com.android.settings.C1992R$string.preferred_network_mode_gsmumts_perf_summary
        L_0x0038:
            if (r1 != r0) goto L_0x0054
            int r1 = super.getPreferredNetworkModeSummaryResId(r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r0 = "getPreferredNetworkModeSummaryResId: Using super class summary. Network Mode: "
            r2.append(r0)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "WMPrefController"
            android.util.Log.i(r3, r2)
        L_0x0054:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.network.vzw.VzwPreferredNetworkModePreferenceController.getPreferredNetworkModeSummaryResId(int):int");
    }

    /* access modifiers changed from: protected */
    public boolean isWorldPhone(PersistableBundle persistableBundle) {
        return super.isWorldPhone(persistableBundle) || MotoTelephonyFeature.isVzwWorldPhoneEnabled(persistableBundle);
    }

    private void showNoSIMDialog() {
        Log.d(LOG_TAG, "showNoSIMDialog()...");
        AlertDialog create = new AlertDialog.Builder(this.mContext).setMessage(C1992R$string.SIMInsertionNotice).setNeutralButton(17039370, C1939x901f6829.INSTANCE).setTitle(C1992R$string.NoSIMTitle).create();
        create.getWindow().addFlags(2);
        create.show();
    }

    private int mapNetworkMode(int i) {
        int i2;
        int checkModeCategory = checkModeCategory(i);
        Log.d(LOG_TAG, "mapNetworkMode input: " + i + " category: " + checkModeCategory);
        if (checkModeCategory != 100) {
            if (checkModeCategory != MODE_GSM) {
                if (checkModeCategory != MODE_LTE_CDMA) {
                    if (checkModeCategory != MODE_LTE_GSM) {
                        i2 = this.mIs5GAvailable ? 27 : 10;
                        Log.d(LOG_TAG, "mapNetworkMode output: " + i2);
                        return i2;
                    }
                }
            }
            i2 = this.mIs5GAvailable ? 26 : 9;
            Log.d(LOG_TAG, "mapNetworkMode output: " + i2);
            return i2;
        }
        i2 = this.mIs5GAvailable ? 25 : 8;
        Log.d(LOG_TAG, "mapNetworkMode output: " + i2);
        return i2;
    }

    private int checkModeCategory(int i) {
        int i2;
        Log.d(LOG_TAG, "checkModeCategory input: " + i);
        if (i != 8) {
            if (i != 9) {
                if (i != 11) {
                    if (i != 12) {
                        if (i != 25) {
                            if (i != 26) {
                                switch (i) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                        i2 = MODE_GSM;
                                        break;
                                    case 4:
                                    case 5:
                                    case 6:
                                        break;
                                    default:
                                        i2 = MODE_GLOBAL;
                                        break;
                                }
                            }
                        }
                    }
                }
                i2 = 100;
                Log.d(LOG_TAG, "checkModeCategory output: " + i2);
                return i2;
            }
            i2 = MODE_LTE_GSM;
            Log.d(LOG_TAG, "checkModeCategory output: " + i2);
            return i2;
        }
        i2 = MODE_LTE_CDMA;
        Log.d(LOG_TAG, "checkModeCategory output: " + i2);
        return i2;
    }
}
