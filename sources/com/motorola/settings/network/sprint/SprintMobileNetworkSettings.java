package com.motorola.settings.network.sprint;

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

public class SprintMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private List<AbstractPreferenceController> mSprintControllers = new ArrayList();

    private static final int getSprintPreferenceScreenResId() {
        return C1994R$xml.sprint_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getSprintPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mSprintControllers = createSprintControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mSprintControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mSprintControllers, true);
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
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mSprintControllers, this.mSettingsLifecycle, this.mFragmentManager);
        VideoCallingPreferenceController videoCallingPreferenceController = getVideoCallingPreferenceController();
        Optional.ofNullable((SprintVoLTEPreferenceController) use(SprintVoLTEPreferenceController.class)).ifPresent(new SprintMobileNetworkSettings$$ExternalSyntheticLambda2(this, videoCallingPreferenceController));
        Optional.ofNullable((Sprint4gCallingPreferenceController) use(Sprint4gCallingPreferenceController.class)).ifPresent(new SprintMobileNetworkSettings$$ExternalSyntheticLambda0(this, videoCallingPreferenceController));
        Optional.ofNullable((SprintAdvanceCallingPreferenceController) use(SprintAdvanceCallingPreferenceController.class)).ifPresent(new SprintMobileNetworkSettings$$ExternalSyntheticLambda1(this, videoCallingPreferenceController));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(VideoCallingPreferenceController videoCallingPreferenceController, SprintVoLTEPreferenceController sprintVoLTEPreferenceController) {
        sprintVoLTEPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(VideoCallingPreferenceController videoCallingPreferenceController, Sprint4gCallingPreferenceController sprint4gCallingPreferenceController) {
        sprint4gCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(VideoCallingPreferenceController videoCallingPreferenceController, SprintAdvanceCallingPreferenceController sprintAdvanceCallingPreferenceController) {
        sprintAdvanceCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    public static List<AbstractPreferenceController> createSprintControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getSprintPreferenceScreenResId(), createSprintControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createSprintControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isFtr6418Enabled(context, i)) {
            arrayList.add(new SprintVoLTEPreferenceController(context, "enhanced_4g_lte"));
            arrayList.add(new Sprint4gCallingPreferenceController(context, "4g_calling"));
            arrayList.add(new SprintAdvanceCallingPreferenceController(context, "advance_call"));
        }
        return arrayList;
    }
}
