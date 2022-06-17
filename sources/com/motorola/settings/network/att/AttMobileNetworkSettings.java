package com.motorola.settings.network.att;

import android.content.Context;
import android.content.Intent;
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

public class AttMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private List<AbstractPreferenceController> mAttControllers = new ArrayList();

    private static final int getAttPreferenceScreenResId() {
        return C1994R$xml.att_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getAttPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mAttControllers = createAttControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mAttControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mAttControllers, true);
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        return super.getWifiCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        Class cls = AttVideoCallingPreferenceController.class;
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
        Class cls = AttOpenNetworkSelectPagePreferenceController.class;
        if (use(cls) != null) {
            return (OpenNetworkSelectPagePreferenceController) use(cls);
        }
        return super.getOpenNetworkSelectPagePrefController();
    }

    /* access modifiers changed from: protected */
    public AutoSelectPreferenceController getAutoSelectPreferenceController() {
        Class cls = AttAutoSelectPreferenceController.class;
        if (use(cls) != null) {
            return (AutoSelectPreferenceController) use(cls);
        }
        return super.getAutoSelectPreferenceController();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        AttMobileNetworkDiagnosticsController attMobileNetworkDiagnosticsController;
        super.onActivityResult(i, i2, intent);
        if (i == 102 && (attMobileNetworkDiagnosticsController = (AttMobileNetworkDiagnosticsController) use(AttMobileNetworkDiagnosticsController.class)) != null) {
            attMobileNetworkDiagnosticsController.onActivityResult(i, i2, intent);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mAttControllers, this.mSettingsLifecycle, this.mFragmentManager);
        Optional.ofNullable((AttOpenNetworkSelectPagePreferenceController) use(AttOpenNetworkSelectPagePreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda7(this));
        Optional.ofNullable((AttAutoSelectPreferenceController) use(AttAutoSelectPreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda3(this));
        Optional.ofNullable((AttRoamingPreferenceController) use(AttRoamingPreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda8(this));
        Optional.ofNullable((AttMobileDataPreferenceController) use(AttMobileDataPreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda5(this));
        Optional.ofNullable((AttMobileNetworkDiagnosticsController) use(AttMobileNetworkDiagnosticsController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda6(this));
        Optional.ofNullable((AttVideoCallingPreferenceController) use(AttVideoCallingPreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda9(this));
        Optional.ofNullable((AttDisable2GController) use(AttDisable2GController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda4(this));
        Optional.ofNullable((Att2GEnablePreferenceController) use(Att2GEnablePreferenceController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda1(this));
        WifiCallingPreferenceController wifiCallingPreferenceController = getWifiCallingPreferenceController();
        VideoCallingPreferenceController videoCallingPreferenceController = getVideoCallingPreferenceController();
        AttWfcPreferenceController attWfcPreferenceController = (AttWfcPreferenceController) use(AttWfcPreferenceController.class);
        if (attWfcPreferenceController != null) {
            Optional.ofNullable((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda0(wifiCallingPreferenceController, videoCallingPreferenceController, attWfcPreferenceController));
        }
        Optional.ofNullable((Att5GSAModeController) use(Att5GSAModeController.class)).ifPresent(new AttMobileNetworkSettings$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(AttOpenNetworkSelectPagePreferenceController attOpenNetworkSelectPagePreferenceController) {
        attOpenNetworkSelectPagePreferenceController.init(this.mLifecycle, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(AttAutoSelectPreferenceController attAutoSelectPreferenceController) {
        attAutoSelectPreferenceController.init(this.mLifecycle, this.mSubId).addListener(getOpenNetworkSelectPagePrefController());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(AttRoamingPreferenceController attRoamingPreferenceController) {
        attRoamingPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(AttMobileDataPreferenceController attMobileDataPreferenceController) {
        attMobileDataPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$4(AttMobileNetworkDiagnosticsController attMobileNetworkDiagnosticsController) {
        attMobileNetworkDiagnosticsController.setFragment(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$5(AttVideoCallingPreferenceController attVideoCallingPreferenceController) {
        attVideoCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$6(AttDisable2GController attDisable2GController) {
        attDisable2GController.init(this.mLifecycle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$7(Att2GEnablePreferenceController att2GEnablePreferenceController) {
        att2GEnablePreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$9(Att5GSAModeController att5GSAModeController) {
        att5GSAModeController.init(this.mLifecycle);
    }

    public static List<AbstractPreferenceController> createAttControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getAttPreferenceScreenResId(), createAttControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createAttControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.needShowOperatorSelectionforPtn(context, i)) {
            arrayList.add(new AttAutoSelectPreferenceController(context, "auto_select_key"));
            arrayList.add(new AttOpenNetworkSelectPagePreferenceController(context, "choose_network_key"));
        }
        if (MotoTelephonyFeature.isFtr5261Enabled(context, i)) {
            arrayList.add(new AttMobileDataPreferenceController(context, "mobile_data_enable"));
        }
        if (MotoTelephonyFeature.isFtr5261Enabled(context, i) || MotoTelephonyFeature.isFtr6342Enabled(context, i)) {
            arrayList.add(new AttRoamingPreferenceController(context, "button_roaming_key"));
        }
        if (MotoTelephonyFeature.isAttVtSupported(context, i)) {
            arrayList.add(new AttVideoCallingPreferenceController(context, "video_calling_key"));
        }
        if (MotoTelephonyFeature.isFtr5083Enabled(context, i)) {
            arrayList.add(new Att2GEnablePreferenceController(context, "enable_2g"));
        }
        return arrayList;
    }
}
