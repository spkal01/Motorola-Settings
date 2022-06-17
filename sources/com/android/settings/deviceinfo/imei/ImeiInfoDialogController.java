package com.android.settings.deviceinfo.imei;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import com.android.settings.C1980R$bool;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.motorola.settings.deviceinfo.DeviceUtils;
import java.util.Locale;

public class ImeiInfoDialogController {
    static final int ID_CDMA_SETTINGS = C1985R$id.cdma_settings;
    static final int ID_GSM_SETTINGS = C1985R$id.gsm_settings;
    static final int ID_IMEI_SV_VALUE = C1985R$id.imei_sv_value;
    static final int ID_IMEI_VALUE = C1985R$id.imei_value;
    static final int ID_MEID_NUMBER_DECIMAL_LABEL = C1985R$id.meid_number_decimal_label;
    static final int ID_MEID_NUMBER_DECIMAL_VALUE = C1985R$id.meid_number_decimal_value;
    static final int ID_MEID_NUMBER_LABEL = C1985R$id.meid_number_label;
    static final int ID_MEID_NUMBER_VALUE = C1985R$id.meid_number_value;
    private static final int ID_MIN_NUMBER_LABEL = C1985R$id.min_number_label;
    static final int ID_MIN_NUMBER_VALUE = C1985R$id.min_number_value;
    static final int ID_PRL_VERSION_VALUE = C1985R$id.prl_version_value;
    private final ImeiInfoDialogFragment mDialog;
    private final int mSlotId;
    private final SubscriptionInfo mSubscriptionInfo;
    private final TelephonyManager mTelephonyManager;

    private static CharSequence getTextAsDigits(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return "";
        }
        if (!TextUtils.isDigitsOnly(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.setSpan(new TtsSpan.DigitsBuilder(charSequence.toString()).build(), 0, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    public ImeiInfoDialogController(ImeiInfoDialogFragment imeiInfoDialogFragment, int i) {
        this.mDialog = imeiInfoDialogFragment;
        this.mSlotId = i;
        Context context = imeiInfoDialogFragment.getContext();
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoForSimSlotIndex(i);
        this.mSubscriptionInfo = activeSubscriptionInfoForSimSlotIndex;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (activeSubscriptionInfoForSimSlotIndex != null) {
            this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(activeSubscriptionInfoForSimSlotIndex.getSubscriptionId());
        } else if (isValidSlotIndex(i, telephonyManager)) {
            this.mTelephonyManager = telephonyManager;
        } else {
            this.mTelephonyManager = null;
        }
    }

    public void populateImeiInfo() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            Log.w("ImeiInfoDialog", "TelephonyManager for this slot is null. Invalid slot? id=" + this.mSlotId);
        } else if (telephonyManager.getPhoneType() == 2) {
            updateDialogForCdmaPhone();
        } else {
            updateDialogForGsmPhone();
        }
    }

    private void updateDialogForCdmaPhone() {
        Resources resources = this.mDialog.getContext().getResources();
        ImeiInfoDialogFragment imeiInfoDialogFragment = this.mDialog;
        int i = ID_MEID_NUMBER_VALUE;
        imeiInfoDialogFragment.setText(i, getMeid());
        if (resources.getBoolean(C1980R$bool.config_show_meid_in_hex_and_decimal_bool)) {
            this.mDialog.setText(ID_MEID_NUMBER_LABEL, resources.getString(C1992R$string.status_meid_number_hex));
            this.mDialog.setText(ID_MEID_NUMBER_DECIMAL_VALUE, getMeidDecimal(getMeid()));
        } else {
            this.mDialog.removeViewFromScreen(ID_MEID_NUMBER_DECIMAL_LABEL);
            this.mDialog.removeViewFromScreen(ID_MEID_NUMBER_DECIMAL_VALUE);
        }
        ImeiInfoDialogFragment imeiInfoDialogFragment2 = this.mDialog;
        int i2 = ID_MIN_NUMBER_VALUE;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        imeiInfoDialogFragment2.setText(i2, subscriptionInfo != null ? this.mTelephonyManager.getCdmaMin(subscriptionInfo.getSubscriptionId()) : "");
        if (resources.getBoolean(C1980R$bool.config_msid_enable)) {
            this.mDialog.setText(ID_MIN_NUMBER_LABEL, resources.getString(C1992R$string.status_msid_number));
        }
        this.mDialog.setText(ID_PRL_VERSION_VALUE, getCdmaPrlVersion());
        if ((this.mSubscriptionInfo == null || !isCdmaLteEnabled()) && (this.mSubscriptionInfo != null || !isSimPresent(this.mSlotId))) {
            this.mDialog.removeViewFromScreen(ID_GSM_SETTINGS);
            return;
        }
        ImeiInfoDialogFragment imeiInfoDialogFragment3 = this.mDialog;
        imeiInfoDialogFragment3.setText(ID_IMEI_VALUE, getTextAsDigits(DeviceUtils.getFormattedImei(imeiInfoDialogFragment3.getContext(), this.mSlotId)));
        this.mDialog.setText(ID_IMEI_SV_VALUE, getTextAsDigits(this.mTelephonyManager.getDeviceSoftwareVersion(this.mSlotId)));
        if (DeviceUtils.hideMEID(this.mDialog.getContext())) {
            this.mDialog.removeViewFromScreen(ID_MEID_NUMBER_LABEL);
            this.mDialog.removeViewFromScreen(i);
            this.mDialog.removeViewFromScreen(ID_MEID_NUMBER_DECIMAL_LABEL);
            this.mDialog.removeViewFromScreen(ID_MEID_NUMBER_DECIMAL_VALUE);
        }
    }

    private String getMeidDecimal(String str) {
        long j;
        if (TextUtils.isEmpty(str) || str.length() != 14) {
            return "";
        }
        String substring = str.substring(0, 8);
        String substring2 = str.substring(8);
        long j2 = 0;
        try {
            j = Long.parseLong(substring, 16);
        } catch (NumberFormatException unused) {
            j = 0;
        }
        String format = String.format(Locale.US, "%010d", new Object[]{Long.valueOf(j)});
        try {
            j2 = Long.parseLong(substring2, 16);
        } catch (NumberFormatException unused2) {
        }
        String format2 = String.format(Locale.US, "%08d", new Object[]{Long.valueOf(j2)});
        StringBuilder sb = new StringBuilder(22);
        sb.append(format.substring(0, 5));
        sb.append(' ');
        sb.append(format.substring(5));
        sb.append(' ');
        sb.append(format2.substring(0, 4));
        sb.append(' ');
        sb.append(format2.substring(4));
        return sb.toString();
    }

    private void updateDialogForGsmPhone() {
        ImeiInfoDialogFragment imeiInfoDialogFragment = this.mDialog;
        imeiInfoDialogFragment.setText(ID_IMEI_VALUE, getTextAsDigits(DeviceUtils.getFormattedImei(imeiInfoDialogFragment.getContext(), this.mSlotId)));
        this.mDialog.setText(ID_IMEI_SV_VALUE, getTextAsDigits(this.mTelephonyManager.getDeviceSoftwareVersion(this.mSlotId)));
        this.mDialog.removeViewFromScreen(ID_CDMA_SETTINGS);
    }

    /* access modifiers changed from: package-private */
    public String getCdmaPrlVersion() {
        return this.mSubscriptionInfo != null ? this.mTelephonyManager.getCdmaPrlVersion() : "";
    }

    /* access modifiers changed from: package-private */
    public boolean isCdmaLteEnabled() {
        return this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled();
    }

    /* access modifiers changed from: package-private */
    public boolean isSimPresent(int i) {
        int simState = this.mTelephonyManager.getSimState(i);
        return (simState == 1 || simState == 0) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public String getMeid() {
        return this.mTelephonyManager.getMeid(this.mSlotId);
    }

    private boolean isValidSlotIndex(int i, TelephonyManager telephonyManager) {
        return i >= 0 && i < telephonyManager.getPhoneCount();
    }
}
