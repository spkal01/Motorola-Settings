package com.motorola.settings.network;

import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.settings.C1978R$array;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.cache.MotoMnsCache;
import com.motorola.settingslib.moto5gmenu.Moto5gMenuUtils;
import java.util.Arrays;

public class MotoTelephonyFeature {
    public static final boolean IS_DEBUGGABLE = Build.IS_DEBUGGABLE;

    public static boolean supportCommonFeatures(Context context, int i) {
        return isFtr4081Enabled(context, i) || isCarrierNeedsCustomPnt(context, i) || isFtr4808Enabled(context, i) || isFtr5084Enabled(context, i) || MotoMobileNetworkUtils.modemSupports5g(context, i) || isFtr5717Enabled(context, i) || isSubsidyLockSupported(context) || isFtr6379Enabled(context, i) || Moto5gMenuUtils.is5gMenuNeeded(context, i) || is5gModeNeeded(context, i) || isFtr5600Enabled(context, i) || isUscCarrier(context, i);
    }

    public static boolean supportAttFeatures(Context context, int i) {
        return needShowOperatorSelectionforPtn(context, i) || isFtr6342Enabled(context, i) || isFtr5934Enabled(context, i) || isAttVtSupported(context, i) || isFtr5013Enabled(context, i);
    }

    public static boolean supportEmaraFeatures(Context context, int i) {
        return isFtr4705Enabled(context, i);
    }

    public static boolean supportSprintFeatures(Context context, int i) {
        return isFtr6418Enabled(context, i);
    }

    public static boolean supportTmoFeatures(Context context, int i) {
        return isFtr4947Enabled(context, i) || isFtr6252Enabled(context, i) || isFtr5379Enabled(context, i) || isTmoVtSupported(context, i);
    }

    public static boolean supportVisibleFeatures(Context context, int i) {
        return isRoamingSettingDisabled(context, i);
    }

    public static boolean supportVzwFeatures(Context context, int i) {
        return isVzwVtSupported(context, i) || (isCdmaLessDevice(context, i) && isVzwCarrier(context, i)) || isFtr5756Enabled(context, i) || advancedCallSettings(context, i) || isVzwWorldPhoneEnabled(context, i);
    }

    private static boolean isCarrierConfigEnabled(Context context, int i, String str) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return MotoMnsCache.getIns(context).getCarrierConfigForSubId(i).getBoolean(str);
    }

    private static boolean isResEnabled(Context context, int i, int i2) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return MotoMnsCache.getIns(context).getResourcesForSubId(i).getBoolean(i2);
    }

    private static boolean isEmptyString(Context context, int i, int i2) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return true;
        }
        return TextUtils.isEmpty(MotoMnsCache.getIns(context).getResourcesForSubId(i).getString(i2));
    }

    private static boolean carrierListContainsSimCarrier(Context context, int i, int i2) {
        String[] stringArray;
        if (SubscriptionManager.isValidSubscriptionId(i) && (stringArray = MotoMnsCache.getIns(context).getResourcesForSubId(i).getStringArray(i2)) != null) {
            return Arrays.asList(stringArray).contains(String.valueOf(getCarrierId(context, i)));
        }
        return false;
    }

    private static boolean isAttCarrier(Context context, int i) {
        return carrierListContainsSimCarrier(context, i, C1978R$array.carrier_ids_att);
    }

    private static boolean isTmoCarrier(Context context, int i) {
        return carrierListContainsSimCarrier(context, i, C1978R$array.carrier_ids_tmo);
    }

    private static boolean isVzwCarrier(Context context, int i) {
        return carrierListContainsSimCarrier(context, i, C1978R$array.carrier_ids_vzw);
    }

    private static int getCarrierId(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return -1;
        }
        return TelephonyManager.from(context).createForSubscriptionId(i).getSimCarrierId();
    }

    public static String getChannelId(Context context) {
        return MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
    }

    public static boolean isFtr4081Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, 17891605) || !isResEnabled(context, i, C1980R$bool.config_enable_show_carrier_name_selection)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr4081Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isCarrierNeedsCustomPnt(Context context, int i) {
        return isCarrierNeedsCustom5gPnt(context, i) || isCarrierNeedsCustomNon5gPnt(context, i);
    }

    public static boolean isCarrierNeedsCustom5gPnt(Context context, int i) {
        boolean isResEnabled = isResEnabled(context, i, C1980R$bool.config_carrier_needs_custom_5g_pnt);
        MotoMnsLog.logd("MotoTelephonyFeature", "isCarrierNeedsCustom5gPnt: " + isResEnabled);
        return isResEnabled;
    }

    public static boolean isCarrierNeedsCustomNon5gPnt(Context context, int i) {
        boolean isResEnabled = isResEnabled(context, i, C1980R$bool.config_carrier_needs_custom_non_5g_pnt);
        MotoMnsLog.logd("MotoTelephonyFeature", "isCarrierNeedsCustomNon5gPnt: " + isResEnabled);
        return isResEnabled;
    }

    public static boolean isSubsidyLockSupported(Context context) {
        String channelId = getChannelId(context);
        String[] stringArray = context.getResources().getStringArray(C1978R$array.subsidy_lock_supported_channel_ids);
        MotoMnsLog.logd("MotoTelephonyFeature", "isSubsidyLockSupported: channelId: " + channelId);
        return Arrays.asList(stringArray).contains(channelId);
    }

    public static boolean isFtr4808Enabled(Context context, int i) {
        boolean z = false;
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        if (MotoMobileNetworkUtils.secondarySimSupportsUpTo3g() && !MotoMobileNetworkUtils.isDds(i)) {
            z = true;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr4808Enabled: " + z + ", subId: " + i);
        return z;
    }

    public static boolean isFtr5084Enabled(Context context, int i) {
        if (isCarrierConfigEnabled(context, i, "config_enable_network_mode_bool")) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5084Enabled: true, subId: " + i);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0029, code lost:
        if (r2.length != 0) goto L_0x002c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isFtr5303Enabled(android.content.Context r4, int r5) {
        /*
            boolean r0 = android.telephony.SubscriptionManager.isValidSubscriptionId(r5)
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            com.motorola.settings.network.cache.MotoMnsCache r4 = com.motorola.settings.network.cache.MotoMnsCache.getIns(r4)
            android.content.res.Resources r4 = r4.getResourcesForSubId(r5)
            int r0 = com.android.settings.C1980R$bool.config_formatNetworkSelection
            boolean r0 = r4.getBoolean(r0)
            if (r0 == 0) goto L_0x002c
            int r2 = com.android.settings.C1978R$array.config_prefixedNationalRoamingNetworks
            java.lang.String[] r2 = r4.getStringArray(r2)
            int r3 = com.android.settings.C1992R$string.config_homeNetwork
            java.lang.String r4 = r4.getString(r3)
            if (r4 == 0) goto L_0x002d
            if (r2 == 0) goto L_0x002d
            int r4 = r2.length
            if (r4 != 0) goto L_0x002c
            goto L_0x002d
        L_0x002c:
            r1 = r0
        L_0x002d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r0 = "isFtr5303Enabled: "
            r4.append(r0)
            r4.append(r1)
            java.lang.String r0 = ", subId: "
            r4.append(r0)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "MotoTelephonyFeature"
            com.motorola.settings.network.MotoMnsLog.logd(r5, r4)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.network.MotoTelephonyFeature.isFtr5303Enabled(android.content.Context, int):boolean");
    }

    public static boolean isFtr5609Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_hide_plmn_forbidden_string)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5609Enabled: true, subId: " + i);
        return true;
    }

    public static boolean localizeChinaOperator(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_localized_china_operator)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "localizedChinaOperator: true, subId: " + i);
        return true;
    }

    public static boolean isFtr5717Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.ftr_5717_usc_data_roam_ui)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5717Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr5600Enabled(Context context, int i) {
        if (!isResEnabled(context, i, C1980R$bool.config_support_vzw_or_mvno_wfc_setting)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5600Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr6379Enabled(Context context, int i) {
        if (!MotoMnsCache.getIns(context).isComponentEnabled("com.motorola.msimsettings.SIM_SETTINGS", true) || !isResEnabled(context, i, C1980R$bool.config_keep_consistency_of_mns_and_msimsettings)) {
            return false;
        }
        return true;
    }

    public static boolean needShowOperatorSelectionforPtn(Context context, int i) {
        if (!isCarrierConfigEnabled(context, i, "show_network_operator_for_ptn_bool")) {
            return false;
        }
        String str = SystemProperties.get("ro.build.type", "");
        String str2 = SystemProperties.get("ro.build.tags", "");
        StringBuilder sb = new StringBuilder();
        sb.append("IS_DEBUGGABLE:");
        boolean z = IS_DEBUGGABLE;
        sb.append(z);
        sb.append(" , buildType:");
        sb.append(str);
        sb.append(", buildTag:");
        sb.append(str2);
        MotoMnsLog.logd("MotoTelephonyFeature", sb.toString());
        if (!z) {
            return "bldccfg,test-keys".equals(str) && "bldccfg,test-keys".equals(str2);
        }
        return true;
    }

    public static boolean isFtr5379Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_support_tmo_network_selection_enabled)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5379Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr4705Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, 17891817)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr4705Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr7270Enabled(Context context, int i) {
        return "retjio".equals(getChannelId(context));
    }

    public static boolean isCdmaLessDevice(Context context, int i) {
        boolean z = !new MotoExtTelephonyManager(context, i).isUiccApplicationAvailable(4);
        MotoMnsLog.logd("MotoTelephonyFeature", "isCdmaLessDevice: " + z);
        return z;
    }

    public static boolean isRoamingSettingDisabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_disable_roaming_setting_for_visible)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isRoamingSettingDisabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr4896Enabled(Context context, int i) {
        if (isEmptyString(context, i, C1992R$string.mobile_data_disable_dialog_intent)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr4896Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr5261Enabled(Context context, int i) {
        if (isEmptyString(context, i, C1992R$string.mobile_data_disable_dialog_intent)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5261Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr6342Enabled(Context context, int i) {
        if (isEmptyString(context, i, C1992R$string.roaming_data_disable_dialog_intent)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr6342Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isVzwVtSupported(Context context, int i) {
        if (!isVzwCarrier(context, i) || !isCarrierConfigEnabled(context, i, "moto_vt_common_req_bool") || !MotoMnsCache.getIns(context).isPackageEnabled("com.android.dialer", true)) {
            return false;
        }
        return true;
    }

    public static boolean isTmoVtSupported(Context context, int i) {
        if (!isTmoCarrier(context, i) || !isCarrierConfigEnabled(context, i, "moto_vt_common_req_bool") || !MotoMnsCache.getIns(context).isPackageEnabled("com.android.dialer", true)) {
            return false;
        }
        return true;
    }

    public static boolean isAttVtSupported(Context context, int i) {
        if (!isAttCarrier(context, i) || !isCarrierConfigEnabled(context, i, "moto_vt_common_req_bool") || !MotoMnsCache.getIns(context).isPackageEnabled("com.android.dialer", true)) {
            return false;
        }
        return true;
    }

    public static boolean isFtr5934Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !MotoMnsCache.getIns(context).isPackageEnabled("com.att.iqi", true) || !MotoMnsCache.getIns(context).isPackageEnabled("com.motorola.iqimotmetrics", true)) {
            MotoMnsLog.logd("MotoTelephonyFeature", "ftr5934Enabled:false");
            return false;
        }
        boolean isResEnabled = isResEnabled(context, i, 17891527);
        MotoMnsLog.logd("MotoTelephonyFeature", "ftr5934Enabled:" + isResEnabled);
        return isResEnabled;
    }

    public static boolean isCarrierNeedsCustom3gPnt(Context context, int i) {
        boolean isResEnabled = isResEnabled(context, i, C1980R$bool.config_carrier_needs_custom_3g_pnt);
        MotoMnsLog.logd("MotoTelephonyFeature", "isCarrierNeedsCustom3gPnt: " + isResEnabled);
        return isResEnabled;
    }

    public static boolean isHideRatInfo(Context context, int i) {
        if (!isResEnabled(context, i, C1980R$bool.config_hide_ratinfo)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isHideRatInfo: true, subId: " + i);
        return true;
    }

    public static boolean isFtr4947Enabled(Context context, int i) {
        boolean z = false;
        if (!((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(i)) {
            return false;
        }
        if (carrierListContainsSimCarrier(context, i, C1978R$array.ftr_4947_carrier_list) && MotoMobileNetworkUtils.isComponentEnabled(context, "com.motorola.lifetimedata.DISPLAY_DATA_COUNTER", true)) {
            z = true;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr4947Enabled: " + z + ", subId: " + i);
        return z;
    }

    public static boolean isFtr6252Enabled(Context context, int i) {
        boolean z = false;
        if (!((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(i)) {
            return false;
        }
        int slotIndex = SubscriptionManager.getSlotIndex(i);
        String str = slotIndex == 1 ? "com.motorola.carriersettingsext.WFC_SLOT1" : "com.motorola.carriersettingsext.WFC";
        if (carrierListContainsSimCarrier(context, i, C1978R$array.ftr_6252_carrier_list) && isCarrierConfigEnabled(context, i, "moto_carrier_specific_wfc_req_bool") && MotoMnsCache.getIns(context).isComponentEnabled(str, true)) {
            z = true;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr6252Enabled: " + z + ", subId: " + i + " Slot id: " + slotIndex);
        return z;
    }

    public static boolean isFtr5756Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || 1839 != getCarrierId(context, i) || !isVzwNetworkExtendersEnabled(context, i) || !MotoMobileNetworkUtils.isPackageEnabled(context, "com.motorola.carriersettingsext", true)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5756Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isVzwNetworkExtendersEnabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        boolean booleanForApplication = MotoMobileNetworkUtils.getBooleanForApplication(context, "com.motorola.carriersettingsext", "ftr_5756_vzw_csg_enabled");
        boolean booleanForApplication2 = MotoMobileNetworkUtils.getBooleanForApplication(context, "com.motorola.carriersettingsext", "ftr_5756_device_supports_csg");
        MotoMnsLog.logd("MotoTelephonyFeature", "vzwNetworkExtendersToggle:" + booleanForApplication + ", subId: " + i);
        MotoMnsLog.logd("MotoTelephonyFeature", "deviceSupportsCsg: " + booleanForApplication2 + ", subId: " + i);
        if (!booleanForApplication || !booleanForApplication2) {
            return false;
        }
        return true;
    }

    public static boolean isFtr6418Enabled(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !MotoMnsCache.getIns(context).isPackageEnabled("com.motorola.sprintwfc", false)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr6418Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr7199Enabled(Context context, int i) {
        return isTmoCarrier(context, i) || "tmo".equals(getChannelId(context)) || isResEnabled(context, i, C1980R$bool.config_show_vonr_toggle);
    }

    public static boolean is5gModeNeeded(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_is_5g_mode_needed)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "is5gModeNeeded: true, subId: " + i);
        return true;
    }

    public static boolean isVoNRMenuNeeded(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.config_is_vonr_menu_needed)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isVoNRMenuNeeded: true, subId: " + i);
        return true;
    }

    public static boolean isFtr5013Enabled(Context context, int i) {
        boolean z = false;
        if (!((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(i)) {
            return false;
        }
        int slotIndex = SubscriptionManager.getSlotIndex(i);
        String str = slotIndex == 1 ? "com.motorola.wfc.TOGGLE_WFC_SLOT1" : "com.motorola.wfc.TOGGLE_WFC";
        if (isCarrierConfigEnabled(context, i, "moto_carrier_specific_wfc_req_bool") && MotoMnsCache.getIns(context).isComponentEnabled(str, true)) {
            z = true;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5013Enabled: " + z + ", subId: " + i + " Slot id: " + slotIndex);
        return z;
    }

    public static boolean advancedCallSettings(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, C1980R$bool.advanced_call_setting) || !MotoMnsCache.getIns(context).isPackageEnabled("com.motorola.vzw.settings.extensions", true)) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "advancedCallSettings: true, subId: " + i);
        return true;
    }

    public static boolean isVzwWorldPhoneEnabled(Context context, int i) {
        return isVzwWorldPhoneEnabled(((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i));
    }

    public static boolean isVzwWorldPhoneEnabled(PersistableBundle persistableBundle) {
        return persistableBundle.getBoolean("moto_vzw_world_phone");
    }

    public static boolean isFtr5083Enabled(Context context, int i) {
        if (!isCarrierConfigEnabled(context, i, "ftr_5083_att_enable_disable_2g_bool")) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isFtr5083Enabled: true, subId: " + i);
        return true;
    }

    public static boolean isUscCarrier(Context context, int i) {
        boolean z = 1952 == getCarrierId(context, i);
        MotoMnsLog.logd("MotoTelephonyFeature", "isUscCarrier :" + z);
        return z;
    }

    public static boolean isCarrier5GOptionEnabled(Context context, int i) {
        if (!isCarrierConfigEnabled(context, i, "att_carrier_enable_disable_5g_bool")) {
            return false;
        }
        MotoMnsLog.logd("MotoTelephonyFeature", "isCarrier5GOptionEabled: true, subId: " + i);
        return true;
    }

    public static boolean isFtr7454Enabled(Context context, int i) {
        return isTmoCarrier(context, i) || "tmo".equals(getChannelId(context));
    }
}
