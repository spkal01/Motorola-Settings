package com.motorola.settings.network.vzw;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VzwMobileNetworkSettings extends ExtendedMobileNetworkSettings {
    private static VzwSelectNetworkPreferenceController mVzwSelectNetworkPreferenceController;
    private List<AbstractPreferenceController> mVzwControllers = new ArrayList();

    private static final int getVzwPreferenceScreenResId() {
        return C1994R$xml.vzw_mobile_network_settings;
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        MotoPreferenceControllerListHelper.addMotoXmlRes(getContext(), getPreferenceManager(), getPreferenceScreen(), getVzwPreferenceScreenResId());
    }

    /* access modifiers changed from: protected */
    public void createMotoPreferenceControllers(Context context, int i) {
        super.createMotoPreferenceControllers(context, i);
        this.mVzwControllers = createVzwControllers(context, i);
    }

    /* access modifiers changed from: protected */
    public void updateControllers(List<AbstractPreferenceController> list) {
        List<AbstractPreferenceController> extendedControllers = getExtendedControllers();
        MotoPreferenceControllerListHelper.updateControllers(extendedControllers, this.mVzwControllers, false);
        MotoPreferenceControllerListHelper.updateControllers(list, extendedControllers, true);
        MotoPreferenceControllerListHelper.updateControllers(list, this.mVzwControllers, true);
    }

    /* access modifiers changed from: protected */
    public WifiCallingPreferenceController getWifiCallingPreferenceController() {
        return super.getWifiCallingPreferenceController();
    }

    /* access modifiers changed from: protected */
    public VideoCallingPreferenceController getVideoCallingPreferenceController() {
        Class cls = VzwVideoCallingPreferenceController.class;
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
        super.onAttach(context);
        MotoPreferenceControllerListHelper.initControllers(context, this.mSubId, this.mVzwControllers, this.mSettingsLifecycle, this.mFragmentManager);
        Optional.ofNullable((VzwVideoCallingPreferenceController) use(VzwVideoCallingPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda7(this));
        VideoCallingPreferenceController videoCallingPreferenceController = getVideoCallingPreferenceController();
        Optional.ofNullable((VzwEnhanced4gLtePreferenceController) use(VzwEnhanced4gLtePreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda10(this, videoCallingPreferenceController));
        Optional.ofNullable((VzwEnhanced4gCallingPreferenceController) use(VzwEnhanced4gCallingPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda9(this, videoCallingPreferenceController));
        Optional.ofNullable((VzwAdvanceCallingPreferenceController) use(VzwAdvanceCallingPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda8(this, videoCallingPreferenceController));
        Optional.ofNullable((VzwNetworkExtendersPreferenceController) use(VzwNetworkExtendersPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda3(this));
        Optional.ofNullable((VzwPreferredNetworkModePreferenceController) use(VzwPreferredNetworkModePreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda5(this));
        Optional.ofNullable((VzwEnabledNetworkModePreferenceController) use(VzwEnabledNetworkModePreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda1(this));
        Optional.ofNullable((VzwSelectNetworkPreferenceController) use(VzwSelectNetworkPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda6(this));
        Optional.ofNullable((VzwCdmaSystemSelectPreferenceController) use(VzwCdmaSystemSelectPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda0(this));
        Optional.ofNullable((VzwNwOperatorsPreferenceCategoryController) use(VzwNwOperatorsPreferenceCategoryController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda4(this));
        Optional.ofNullable((VzwMobileDataPreferenceController) use(VzwMobileDataPreferenceController.class)).ifPresent(new VzwMobileNetworkSettings$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(VzwVideoCallingPreferenceController vzwVideoCallingPreferenceController) {
        vzwVideoCallingPreferenceController.init(this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(VideoCallingPreferenceController videoCallingPreferenceController, VzwEnhanced4gLtePreferenceController vzwEnhanced4gLtePreferenceController) {
        vzwEnhanced4gLtePreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$2(VideoCallingPreferenceController videoCallingPreferenceController, VzwEnhanced4gCallingPreferenceController vzwEnhanced4gCallingPreferenceController) {
        vzwEnhanced4gCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$3(VideoCallingPreferenceController videoCallingPreferenceController, VzwAdvanceCallingPreferenceController vzwAdvanceCallingPreferenceController) {
        vzwAdvanceCallingPreferenceController.init(this.mSubId).addListener(videoCallingPreferenceController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$4(VzwNetworkExtendersPreferenceController vzwNetworkExtendersPreferenceController) {
        vzwNetworkExtendersPreferenceController.init(this.mSubId, getFragmentManager());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$5(VzwPreferredNetworkModePreferenceController vzwPreferredNetworkModePreferenceController) {
        vzwPreferredNetworkModePreferenceController.init(getLifecycle(), this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$6(VzwEnabledNetworkModePreferenceController vzwEnabledNetworkModePreferenceController) {
        vzwEnabledNetworkModePreferenceController.init(getLifecycle(), this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$7(VzwSelectNetworkPreferenceController vzwSelectNetworkPreferenceController) {
        vzwSelectNetworkPreferenceController.init(getLifecycle(), this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$8(VzwCdmaSystemSelectPreferenceController vzwCdmaSystemSelectPreferenceController) {
        vzwCdmaSystemSelectPreferenceController.init(getPreferenceManager(), this.mSubId);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$9(VzwNwOperatorsPreferenceCategoryController vzwNwOperatorsPreferenceCategoryController) {
        vzwNwOperatorsPreferenceCategoryController.init(getLifecycle(), this.mSubId).setChildren(Arrays.asList(new AbstractPreferenceController[]{this.mAutoSelectPreferenceController, mVzwSelectNetworkPreferenceController}));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$10(VzwMobileDataPreferenceController vzwMobileDataPreferenceController) {
        vzwMobileDataPreferenceController.init(this.mFragmentManager, this.mSubId);
    }

    public static List<AbstractPreferenceController> createVzwControllers(Context context, int i) {
        return MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, getVzwPreferenceScreenResId(), createVzwControllersFromCode(context, i));
    }

    private static List<AbstractPreferenceController> createVzwControllersFromCode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if (MotoTelephonyFeature.isFtr4896Enabled(context, i)) {
            arrayList.add(new VzwMobileDataPreferenceController(context, "mobile_data_enable"));
        }
        if (MotoTelephonyFeature.isVzwVtSupported(context, i)) {
            arrayList.add(new VzwVideoCallingPreferenceController(context, "video_calling_key"));
        }
        if (MotoTelephonyFeature.isVzwVtSupported(context, i) || MotoTelephonyFeature.isCdmaLessDevice(context, i)) {
            arrayList.add(new VzwEnhanced4gLtePreferenceController(context, "enhanced_4g_lte"));
            arrayList.add(new VzwEnhanced4gCallingPreferenceController(context, "4g_calling"));
            arrayList.add(new VzwAdvanceCallingPreferenceController(context, "advance_call"));
        }
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(context, i)) {
            arrayList.add(new VzwPreferredNetworkModePreferenceController(context, "preferred_network_mode_key"));
            arrayList.add(new VzwEnabledNetworkModePreferenceController(context, "enabled_networks_key"));
            VzwSelectNetworkPreferenceController vzwSelectNetworkPreferenceController = new VzwSelectNetworkPreferenceController(context, "button_select_network_key", "network_operators_category_key");
            mVzwSelectNetworkPreferenceController = vzwSelectNetworkPreferenceController;
            arrayList.add(vzwSelectNetworkPreferenceController);
            arrayList.add(new VzwCdmaSystemSelectPreferenceController(context, "cdma_system_select_key"));
            arrayList.add(new VzwNwOperatorsPreferenceCategoryController(context, "network_operators_category_key"));
        }
        return arrayList;
    }
}
