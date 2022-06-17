package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.cache.MotoMnsCache;

public class PreferredNetworkModePreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener {
    protected CarrierConfigManager mCarrierConfigManager;
    private boolean mIsGlobalCdma;
    private PersistableBundle mPersistableBundle;
    protected TelephonyManager mTelephonyManager;

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

    public PreferredNetworkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    public int getAvailabilityStatus(int i) {
        boolean z;
        PersistableBundle carrierConfigForSubId = MotoMnsCache.getIns(this.mContext).getCarrierConfigForSubId(i);
        if (i != -1 && carrierConfigForSubId != null && !carrierConfigForSubId.getBoolean("hide_carrier_network_settings_bool") && !carrierConfigForSubId.getBoolean("hide_preferred_network_type_bool") && isWorldPhone(carrierConfigForSubId)) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            return 0;
        }
        return 2;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        int preferredNetworkMode = getPreferredNetworkMode();
        listPreference.setValue(Integer.toString(preferredNetworkMode));
        listPreference.setSummary(getPreferredNetworkModeSummaryResId(preferredNetworkMode));
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        this.mTelephonyManager.setAllowedNetworkTypesForReason(0, MobileNetworkUtils.getRafFromNetworkType(parseInt));
        ((ListPreference) preference).setSummary(getPreferredNetworkModeSummaryResId(parseInt));
        return true;
    }

    public void init(int i) {
        this.mSubId = i;
        PersistableBundle carrierConfigForSubId = MotoMnsCache.getIns(this.mContext).getCarrierConfigForSubId(this.mSubId);
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        this.mIsGlobalCdma = createForSubscriptionId.isLteCdmaEvdoGsmWcdmaEnabled() && carrierConfigForSubId.getBoolean("show_cdma_choices_bool");
    }

    /* access modifiers changed from: protected */
    public int getPreferredNetworkMode() {
        return MobileNetworkUtils.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesForReason(0));
    }

    /* access modifiers changed from: protected */
    public int getPreferredNetworkModeSummaryResId(int i) {
        switch (i) {
            case 0:
                return C1992R$string.preferred_network_mode_wcdma_perf_summary;
            case 1:
                return C1992R$string.preferred_network_mode_gsm_only_summary;
            case 2:
                return C1992R$string.preferred_network_mode_wcdma_only_summary;
            case 3:
                return C1992R$string.preferred_network_mode_gsm_wcdma_summary;
            case 4:
                if (this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled()) {
                    return C1992R$string.preferred_network_mode_cdma_summary;
                }
                return C1992R$string.preferred_network_mode_cdma_evdo_summary;
            case 5:
                return C1992R$string.preferred_network_mode_cdma_only_summary;
            case 6:
                return C1992R$string.preferred_network_mode_evdo_only_summary;
            case 7:
                return C1992R$string.preferred_network_mode_cdma_evdo_gsm_wcdma_summary;
            case 8:
                return C1992R$string.preferred_network_mode_lte_cdma_evdo_summary;
            case 9:
                return C1992R$string.preferred_network_mode_lte_gsm_wcdma_summary;
            case 10:
                if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                    return C1992R$string.preferred_network_mode_lte_cdma_evdo_gsm_wcdma_summary;
                }
                return C1992R$string.preferred_network_mode_lte_summary;
            case 11:
                return C1992R$string.preferred_network_mode_lte_summary;
            case 12:
                return C1992R$string.preferred_network_mode_lte_wcdma_summary;
            case 13:
                return C1992R$string.preferred_network_mode_tdscdma_summary;
            case 14:
                return C1992R$string.preferred_network_mode_tdscdma_wcdma_summary;
            case 15:
                return C1992R$string.preferred_network_mode_lte_tdscdma_summary;
            case 16:
                return C1992R$string.preferred_network_mode_tdscdma_gsm_summary;
            case 17:
                return C1992R$string.preferred_network_mode_lte_tdscdma_gsm_summary;
            case 18:
                return C1992R$string.preferred_network_mode_tdscdma_gsm_wcdma_summary;
            case 19:
                return C1992R$string.preferred_network_mode_lte_tdscdma_wcdma_summary;
            case 20:
                return C1992R$string.preferred_network_mode_lte_tdscdma_gsm_wcdma_summary;
            case 21:
                return C1992R$string.preferred_network_mode_tdscdma_cdma_evdo_gsm_wcdma_summary;
            case 22:
                return C1992R$string.preferred_network_mode_lte_tdscdma_cdma_evdo_gsm_wcdma_summary;
            case 23:
                return C1992R$string.preferred_network_mode_nr_only_summary;
            case 24:
                return C1992R$string.preferred_network_mode_nr_lte_summary;
            case 25:
                return C1992R$string.preferred_network_mode_nr_lte_cdma_evdo_summary;
            case 26:
                return C1992R$string.preferred_network_mode_nr_lte_gsm_wcdma_summary;
            case 27:
                return C1992R$string.preferred_network_mode_global_summary;
            case 28:
                return C1992R$string.preferred_network_mode_nr_lte_wcdma_summary;
            case 29:
                return C1992R$string.preferred_network_mode_nr_lte_tdscdma_summary;
            case 30:
                return C1992R$string.preferred_network_mode_nr_lte_tdscdma_gsm_summary;
            case 31:
                return C1992R$string.preferred_network_mode_nr_lte_tdscdma_wcdma_summary;
            case 32:
                return C1992R$string.preferred_network_mode_nr_lte_tdscdma_gsm_wcdma_summary;
            case 33:
                return C1992R$string.f63x1efbff85;
            default:
                return C1992R$string.preferred_network_mode_global_summary;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isWorldPhone(PersistableBundle persistableBundle) {
        return persistableBundle.getBoolean("world_phone_bool");
    }
}
