package com.motorola.settings.network.tmo;

import android.content.Context;
import com.android.settings.C1994R$xml;
import com.android.settings.network.telephony.BackupCallingPreferenceController;
import com.android.settings.network.telephony.CallingPreferenceCategoryController;
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

public class TmoMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private List<AbstractPreferenceController> mTmoControllers = new ArrayList();

    private static final int getTmoPreferenceScreenResId() {
        return C1994R$xml.tmo_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getTmoPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mTmoControllers = createTmoControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mTmoControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mTmoControllers, true);
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        return super.getWifiCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        Class cls = TmoVideoCallingPreferenceController.class;
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
        return super.getOpenNetworkSelectPagePrefController();
    }

    /* access modifiers changed from: protected */
    public AutoSelectPreferenceController getAutoSelectPreferenceController() {
        return super.getAutoSelectPreferenceController();
    }

    public void onAttach(Context context) {
        Class cls = TmoVoNRPreferenceController.class;
        Class cls2 = Tmo5GToggleEnablerController.class;
        Class cls3 = Tmo2GToggleEnablerController.class;
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mTmoControllers, this.mSettingsLifecycle, this.mFragmentManager);
        WifiCallingPreferenceController wifiCallingPreferenceController = getWifiCallingPreferenceController();
        VideoCallingPreferenceController videoCallingPreferenceController = getVideoCallingPreferenceController();
        TmoWiFiCallingPreferenceController tmoWiFiCallingPreferenceController = (TmoWiFiCallingPreferenceController) use(TmoWiFiCallingPreferenceController.class);
        TmoVoNRPreferenceController tmoVoNRPreferenceController = (TmoVoNRPreferenceController) use(cls);
        ArrayList arrayList = new ArrayList();
        arrayList.add((Tmo2GToggleEnablerController) use(cls3));
        arrayList.add((Tmo5GToggleEnablerController) use(cls2));
        arrayList.add(wifiCallingPreferenceController);
        arrayList.add(videoCallingPreferenceController);
        if (tmoWiFiCallingPreferenceController != null) {
            arrayList.add(tmoWiFiCallingPreferenceController);
        }
        Optional.ofNullable((Tmo2GToggleEnablerController) use(cls3)).ifPresent(new TmoMobileNetworkSettings$$ExternalSyntheticLambda0(this));
        Optional.ofNullable((Tmo5GToggleEnablerController) use(cls2)).ifPresent(new TmoMobileNetworkSettings$$ExternalSyntheticLambda1(this));
        Optional.ofNullable((TmoVideoCallingPreferenceController) use(TmoVideoCallingPreferenceController.class)).ifPresent(new TmoMobileNetworkSettings$$ExternalSyntheticLambda2(this));
        if (tmoVoNRPreferenceController != null) {
            arrayList.add(tmoVoNRPreferenceController);
        }
        Optional.ofNullable((TmoVoNRPreferenceController) use(cls)).ifPresent(new TmoMobileNetworkSettings$$ExternalSyntheticLambda3(this));
        Optional.ofNullable((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).ifPresent(new TmoMobileNetworkSettings$$ExternalSyntheticLambda4(arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(Tmo2GToggleEnablerController tmo2GToggleEnablerController) {
        tmo2GToggleEnablerController.init(this.mSubId, this.mFragmentManager);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(Tmo5GToggleEnablerController tmo5GToggleEnablerController) {
        tmo5GToggleEnablerController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(TmoVideoCallingPreferenceController tmoVideoCallingPreferenceController) {
        tmoVideoCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(TmoVoNRPreferenceController tmoVoNRPreferenceController) {
        tmoVoNRPreferenceController.init(this.mSubId, this.mFragmentManager, this.mLifecycle);
    }

    public static List<AbstractPreferenceController> createTmoControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getTmoPreferenceScreenResId(), createTmoControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createTmoControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isFtr5379Enabled(context, i)) {
            arrayList.add(new TmoEnabledNetworkModePreferenceController(context, "enabled_networks_key"));
        }
        if (MotoTelephonyFeature.isTmoVtSupported(context, i)) {
            arrayList.add(new TmoVideoCallingPreferenceController(context, "video_calling_key"));
        }
        return arrayList;
    }
}
