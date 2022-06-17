package com.motorola.settings.network.visible;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisibleMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private List<AbstractPreferenceController> mVisibleControllers = new ArrayList();

    private static final int getVisiblePreferenceScreenResId() {
        return C1994R$xml.visible_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getVisiblePreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mVisibleControllers = createVisibleControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mVisibleControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mVisibleControllers, true);
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

    public void onAttach(Context context) {
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mVisibleControllers, this.mSettingsLifecycle, this.mFragmentManager);
        Optional.ofNullable((VisibleRoamingPreferenceController) use(VisibleRoamingPreferenceController.class)).ifPresent(new VisibleMobileNetworkSettings$$ExternalSyntheticLambda2(this));
        Optional.ofNullable((VisibleCdmaSystemSelectPreferenceController) use(VisibleCdmaSystemSelectPreferenceController.class)).ifPresent(new VisibleMobileNetworkSettings$$ExternalSyntheticLambda0(this));
        Optional.ofNullable((VisibleEnabledNetworkModePreferenceController) use(VisibleEnabledNetworkModePreferenceController.class)).ifPresent(new VisibleMobileNetworkSettings$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(VisibleRoamingPreferenceController visibleRoamingPreferenceController) {
        visibleRoamingPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(VisibleCdmaSystemSelectPreferenceController visibleCdmaSystemSelectPreferenceController) {
        visibleCdmaSystemSelectPreferenceController.init(this.mPreferenceManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(VisibleEnabledNetworkModePreferenceController visibleEnabledNetworkModePreferenceController) {
        visibleEnabledNetworkModePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    public static List<AbstractPreferenceController> createVisibleControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getVisiblePreferenceScreenResId(), createVisibleControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createVisibleControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isRoamingSettingDisabled(context, i)) {
            arrayList.add(new VisibleRoamingPreferenceController(context, "button_roaming_key"));
            arrayList.add(new VisibleCdmaSystemSelectPreferenceController(context, "cdma_system_select_key"));
            arrayList.add(new VisibleEnabledNetworkModePreferenceController(context, "enabled_networks_key"));
        }
        return arrayList;
    }
}
