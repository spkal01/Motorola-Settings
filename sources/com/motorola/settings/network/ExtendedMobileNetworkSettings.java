package com.motorola.settings.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionManager;
import android.view.MenuItem;
import com.android.settings.C1985R$id;
import com.android.settings.C1994R$xml;
import com.android.settings.network.telephony.BackupCallingPreferenceController;
import com.android.settings.network.telephony.MobileNetworkSettings;
import com.android.settings.network.telephony.VideoCallingPreferenceController;
import com.android.settings.network.telephony.WifiCallingPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtendedMobileNetworkSettings extends MobileNetworkSettings {
    private List<AbstractPreferenceController> mExtendedControllers = new ArrayList();

    public List<AbstractPreferenceController> getExtendedControllers() {
        return this.mExtendedControllers;
    }

    private static final int getExtendedPreferenceScreenResId() {
        return C1994R$xml.extended_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getExtendedPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        this.mExtendedControllers = createExtenededControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        MotoPreferenceControllerListHelper.updateControllers(list, this.mExtendedControllers, true);
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        Class cls = ExtendedWifiCallingPreferenceController.class;
        if (use(cls) != null) {
            return (WifiCallingPreferenceController) use(cls);
        }
        return super.getWifiCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        Class cls = ExtendedVideoCallingPreferenceController.class;
        if (use(cls) != null) {
            return (VideoCallingPreferenceController) use(cls);
        }
        return super.getVideoCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public BackupCallingPreferenceController getBackupCallingPreferenceController() {
        return super.getBackupCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public OpenNetworkSelectPagePreferenceController getOpenNetworkSelectPagePrefController() {
        Class cls = ExtendedOpenNetworkSelectPagePreferenceController.class;
        if (use(cls) != null) {
            return (OpenNetworkSelectPagePreferenceController) use(cls);
        }
        return super.getOpenNetworkSelectPagePrefController();
    }

    /* access modifiers changed from: protected */
    public AutoSelectPreferenceController getAutoSelectPreferenceController() {
        Class cls = ExtendedAutoSelectPreferenceController.class;
        if (use(cls) != null) {
            return (AutoSelectPreferenceController) use(cls);
        }
        return super.getAutoSelectPreferenceController();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Context context = getContext();
        if (!MotoTelephonyFeature.isFtr6379Enabled(context, this.mSubId) || menuItem.getItemId() != C1985R$id.edit_sim_name) {
            return super.onOptionsItemSelected(menuItem);
        }
        Intent intent = new Intent("com.motorola.msimsettings.SIM_CUSTOMIZATION");
        intent.setComponent(new ComponentName("com.motorola.msimsettings", "com.motorola.msimsettings.activities.SimCustomizationActivity"));
        intent.putExtra("sim_slot", SubscriptionManager.getSlotIndex(this.mSubId));
        context.startActivity(intent);
        return true;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mExtendedControllers, this.mSettingsLifecycle, this.mFragmentManager);
        Optional.ofNullable((ExtendedEnabledNetworkModePreferenceController) use(ExtendedEnabledNetworkModePreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda0(this));
        Optional.ofNullable((ExtendedRoamingPreferenceController) use(ExtendedRoamingPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda2(this));
        Optional.ofNullable((ExtendedMobileDataPreferenceController) use(ExtendedMobileDataPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda1(this));
        Optional.ofNullable((MotoSimPreferencesController) use(MotoSimPreferencesController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda4(this));
        Optional.ofNullable((NrPreferenceController) use(NrPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda6(this));
        Optional.ofNullable((NrModePreferenceController) use(NrModePreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda5(this));
        Optional.ofNullable((ExtendedWifiCallingPreferenceController) use(ExtendedWifiCallingPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda3(this));
        Optional.ofNullable((Smart5gPreferenceController) use(Smart5gPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda7(this));
        Optional.ofNullable((VoNRPreferenceController) use(VoNRPreferenceController.class)).ifPresent(new ExtendedMobileNetworkSettings$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(ExtendedEnabledNetworkModePreferenceController extendedEnabledNetworkModePreferenceController) {
        extendedEnabledNetworkModePreferenceController.init(this.mLifecycle, this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(ExtendedRoamingPreferenceController extendedRoamingPreferenceController) {
        extendedRoamingPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(ExtendedMobileDataPreferenceController extendedMobileDataPreferenceController) {
        extendedMobileDataPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(MotoSimPreferencesController motoSimPreferencesController) {
        motoSimPreferencesController.init(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$4(NrPreferenceController nrPreferenceController) {
        nrPreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$5(NrModePreferenceController nrModePreferenceController) {
        nrModePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$6(ExtendedWifiCallingPreferenceController extendedWifiCallingPreferenceController) {
        extendedWifiCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$7(Smart5gPreferenceController smart5gPreferenceController) {
        smart5gPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$8(VoNRPreferenceController voNRPreferenceController) {
        voNRPreferenceController.init(this.mLifecycle, this.mSubId);
    }

    public static List<AbstractPreferenceController> createExtenededControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getExtendedPreferenceScreenResId(), createExtenededControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createExtenededControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isCarrierNeedsCustomPnt(context, i) || MotoTelephonyFeature.isFtr4808Enabled(context, i) || MotoTelephonyFeature.isFtr5084Enabled(context, i) || MotoMobileNetworkUtils.modemSupports5g(context, i)) {
            arrayList.add(new ExtendedEnabledNetworkModePreferenceController(context, "enabled_networks_key"));
        }
        if (MotoTelephonyFeature.isFtr5717Enabled(context, i)) {
            arrayList.add(new ExtendedRoamingPreferenceController(context, "button_roaming_key"));
        }
        if (MotoTelephonyFeature.isFtr6379Enabled(context, i)) {
            arrayList.add(new ExtendedMobileDataPreferenceController(context, "mobile_data_enable"));
        }
        if (MotoTelephonyFeature.isFtr5600Enabled(context, i) || MotoTelephonyFeature.isUscCarrier(context, i)) {
            arrayList.add(new ExtendedWifiCallingPreferenceController(context, "wifi_calling"));
        }
        return arrayList;
    }
}
