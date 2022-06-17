package com.motorola.settings.network.emara;

import android.content.Context;
import com.android.settings.C1994R$xml;
import com.android.settings.network.telephony.BackupCallingPreferenceController;
import com.android.settings.network.telephony.VideoCallingPreferenceController;
import com.android.settings.network.telephony.WifiCallingPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.motorola.settings.network.ExtendedMobileNetworkSettings;
import com.motorola.settings.network.MotoPreferenceControllerListHelper;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.emara.jio.JioCallsDefaultSubscriptionController;
import com.motorola.settings.network.emara.jio.JioEnabledNetworkModePreferenceController;
import com.motorola.settings.network.emara.jio.JioMobileNetworkSwitchController;
import com.motorola.settings.network.emara.jio.JioSmsSubscriptionController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmaraMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private List<AbstractPreferenceController> mEmaraControllers = new ArrayList();

    private static final int getEmaraPreferenceScreenResId() {
        return C1994R$xml.emara_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getEmaraPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        return super.getWifiCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        return super.getVideoCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public BackupCallingPreferenceController getBackupCallingPreferenceController() {
        return super.getBackupCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public OpenNetworkSelectPagePreferenceController getOpenNetworkSelectPagePrefController() {
        return super.getOpenNetworkSelectPagePrefController();
    }

    /* access modifiers changed from: protected */
    public AutoSelectPreferenceController getAutoSelectPreferenceController() {
        return super.getAutoSelectPreferenceController();
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mEmaraControllers = createEmaraControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mEmaraControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mEmaraControllers, true);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mEmaraControllers, this.mSettingsLifecycle, this.mFragmentManager);
        Optional.ofNullable((EmaraRoamingPreferenceController) use(EmaraRoamingPreferenceController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda0(this));
        Optional.ofNullable((NationalDataRoamingController) use(NationalDataRoamingController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda1(this));
        Optional.ofNullable((JioSmsSubscriptionController) use(JioSmsSubscriptionController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda5(this));
        Optional.ofNullable((JioCallsDefaultSubscriptionController) use(JioCallsDefaultSubscriptionController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda2(this));
        Optional.ofNullable((JioMobileNetworkSwitchController) use(JioMobileNetworkSwitchController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda4(this));
        Optional.ofNullable((JioEnabledNetworkModePreferenceController) use(JioEnabledNetworkModePreferenceController.class)).ifPresent(new EmaraMobileNetworkSettings$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(EmaraRoamingPreferenceController emaraRoamingPreferenceController) {
        emaraRoamingPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(NationalDataRoamingController nationalDataRoamingController) {
        nationalDataRoamingController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(JioSmsSubscriptionController jioSmsSubscriptionController) {
        jioSmsSubscriptionController.init(this.mLifecycle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(JioCallsDefaultSubscriptionController jioCallsDefaultSubscriptionController) {
        jioCallsDefaultSubscriptionController.init(this.mLifecycle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$4(JioMobileNetworkSwitchController jioMobileNetworkSwitchController) {
        jioMobileNetworkSwitchController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$5(JioEnabledNetworkModePreferenceController jioEnabledNetworkModePreferenceController) {
        jioEnabledNetworkModePreferenceController.init(this.mLifecycle, this.mFragmentManager, this.mSubId);
    }

    public static List<AbstractPreferenceController> createEmaraControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getEmaraPreferenceScreenResId(), createEmaraControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createEmaraControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isFtr4705Enabled(context, i)) {
            arrayList.add(new EmaraRoamingPreferenceController(context, "button_roaming_key"));
        }
        if (MotoTelephonyFeature.isFtr7270Enabled(context, i)) {
            arrayList.add(new JioSmsSubscriptionController(context, "sms_preference"));
            arrayList.add(new JioCallsDefaultSubscriptionController(context, "calls_preference"));
            arrayList.add(new JioMobileNetworkSwitchController(context, "use_sim_switch"));
            arrayList.add(new JioEnabledNetworkModePreferenceController(context, "enabled_networks_key"));
        }
        return arrayList;
    }
}
