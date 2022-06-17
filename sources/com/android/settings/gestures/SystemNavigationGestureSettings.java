package com.android.settings.gestures;

import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.ServiceManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.support.actionbar.HelpMenuController;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.utils.CandidateInfoExtra;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.utils.DisplayUtils;
import com.motorola.settings.widget.ExtendedRadioButtonPreferenceWithExtras;
import java.util.ArrayList;
import java.util.List;

public class SystemNavigationGestureSettings extends RadioButtonPickerFragment implements HelpResourceProvider {
    static final String KEY_SYSTEM_NAV_2BUTTONS = "system_nav_2buttons";
    static final String KEY_SYSTEM_NAV_3BUTTONS = "system_nav_3buttons";
    static final String KEY_SYSTEM_NAV_GESTURAL = "system_nav_gestural";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.system_navigation_gesture_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return SystemNavigationPreferenceController.isGestureAvailable(context);
        }
    };
    static final Intent TUTORIAL_INTENT_SYSTEM_NAV_GESTURAL = new Intent().setPackage("com.motorola.gesture").setAction("motorola.intent.action.SYSTEM_NAVIGATION_TUTORIAL_SETTINGS");
    private IOverlayManager mOverlayManager;
    private IllustrationPreference mVideoPreference;

    public int getMetricsCategory() {
        return 1374;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        SearchMenuController.init((InstrumentedPreferenceFragment) this);
        HelpMenuController.init((ObservablePreferenceFragment) this);
        FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).edit().putBoolean("pref_system_navigation_suggestion_complete", true).apply();
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        IllustrationPreference illustrationPreference = new IllustrationPreference(context);
        this.mVideoPreference = illustrationPreference;
        setIllustrationVideo(illustrationPreference, getDefaultKey());
    }

    public void updateCandidates() {
        String defaultKey = getDefaultKey();
        String systemDefaultKey = getSystemDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        preferenceScreen.addPreference(this.mVideoPreference);
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates != null) {
            for (CandidateInfo candidateInfo : candidates) {
                ExtendedRadioButtonPreferenceWithExtras extendedRadioButtonPreferenceWithExtras = new ExtendedRadioButtonPreferenceWithExtras(getPrefContext());
                bindPreference(extendedRadioButtonPreferenceWithExtras, candidateInfo.getKey(), candidateInfo, defaultKey);
                bindPreferenceExtra(extendedRadioButtonPreferenceWithExtras, candidateInfo.getKey(), candidateInfo, defaultKey, systemDefaultKey);
                preferenceScreen.addPreference(extendedRadioButtonPreferenceWithExtras);
            }
            mayCheckOnlyRadioButton();
        }
    }

    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if (candidateInfo instanceof CandidateInfoExtra) {
            radioButtonPreference.setSummary(((CandidateInfoExtra) candidateInfo).loadSummary());
            if (candidateInfo.getKey() == KEY_SYSTEM_NAV_GESTURAL) {
                radioButtonPreference.setExtraWidgetOnClickListener(new SystemNavigationGestureSettings$$ExternalSyntheticLambda0(this));
            } else if (candidateInfo.getKey() == KEY_SYSTEM_NAV_3BUTTONS && isCustNavBarAvailable()) {
                radioButtonPreference.setExtraWidgetOnClickListener(new SystemNavigationGestureSettings$$ExternalSyntheticLambda1(this));
            }
            bindPreferenceAction(radioButtonPreference, str, candidateInfo, str2, str3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$bindPreferenceExtra$0(View view) {
        startActivity(new Intent("com.android.settings.GESTURE_NAVIGATION_SETTINGS"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$bindPreferenceExtra$1(View view) {
        startActivity(new Intent("com.motorola.settings.THREE_BUTTON_NAVIGATION_SETTINGS"));
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.system_navigation_gesture_settings;
    }

    /* access modifiers changed from: protected */
    public List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.gestural")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C1992R$string.edge_to_edge_navigation_title), context.getText(C1992R$string.edge_to_edge_navigation_summary), KEY_SYSTEM_NAV_GESTURAL, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.twobutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C1992R$string.swipe_up_to_switch_apps_title), context.getText(C1992R$string.swipe_up_to_switch_apps_summary), KEY_SYSTEM_NAV_2BUTTONS, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.threebutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C1992R$string.legacy_navigation_title), context.getText(C1992R$string.legacy_navigation_summary), KEY_SYSTEM_NAV_3BUTTONS, true));
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public String getDefaultKey() {
        return getCurrentSystemNavigationMode(getContext());
    }

    /* access modifiers changed from: protected */
    public boolean setDefaultKey(String str) {
        setCurrentSystemNavigationMode(this.mOverlayManager, str);
        setIllustrationVideo(this.mVideoPreference, str);
        return true;
    }

    static String getCurrentSystemNavigationMode(Context context) {
        if (SystemNavigationPreferenceController.isGestureNavigationEnabled(context)) {
            return KEY_SYSTEM_NAV_GESTURAL;
        }
        return SystemNavigationPreferenceController.is2ButtonNavigationEnabled(context) ? KEY_SYSTEM_NAV_2BUTTONS : KEY_SYSTEM_NAV_3BUTTONS;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentSystemNavigationMode(android.content.om.IOverlayManager r4, java.lang.String r5) {
        /*
            r3 = this;
            r5.hashCode()
            int r0 = r5.hashCode()
            r1 = 1
            r2 = -1
            switch(r0) {
                case -1860313413: goto L_0x0023;
                case -1375361165: goto L_0x0018;
                case -117503078: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x002d
        L_0x000d:
            java.lang.String r0 = "system_nav_3buttons"
            boolean r5 = r5.equals(r0)
            if (r5 != 0) goto L_0x0016
            goto L_0x002d
        L_0x0016:
            r2 = 2
            goto L_0x002d
        L_0x0018:
            java.lang.String r0 = "system_nav_gestural"
            boolean r5 = r5.equals(r0)
            if (r5 != 0) goto L_0x0021
            goto L_0x002d
        L_0x0021:
            r2 = r1
            goto L_0x002d
        L_0x0023:
            java.lang.String r0 = "system_nav_2buttons"
            boolean r5 = r5.equals(r0)
            if (r5 != 0) goto L_0x002c
            goto L_0x002d
        L_0x002c:
            r2 = 0
        L_0x002d:
            switch(r2) {
                case 0: goto L_0x0040;
                case 1: goto L_0x0034;
                case 2: goto L_0x0031;
                default: goto L_0x0030;
            }
        L_0x0030:
            goto L_0x003d
        L_0x0031:
            java.lang.String r3 = "com.android.internal.systemui.navbar.threebutton"
            goto L_0x0042
        L_0x0034:
            int r3 = r3.getCurrGestureNavStyle()
            if (r3 != r1) goto L_0x003d
            java.lang.String r3 = "com.android.internal.systemui.navbar.hidegestural"
            goto L_0x0042
        L_0x003d:
            java.lang.String r3 = "com.android.internal.systemui.navbar.gestural"
            goto L_0x0042
        L_0x0040:
            java.lang.String r3 = "com.android.internal.systemui.navbar.twobutton"
        L_0x0042:
            r5 = -2
            r4.setEnabledExclusiveInCategory(r3, r5)     // Catch:{ RemoteException -> 0x0047 }
            return
        L_0x0047:
            r3 = move-exception
            java.lang.RuntimeException r3 = r3.rethrowFromSystemServer()
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.gestures.SystemNavigationGestureSettings.setCurrentSystemNavigationMode(android.content.om.IOverlayManager, java.lang.String):void");
    }

    private static void setIllustrationVideo(IllustrationPreference illustrationPreference, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1860313413:
                if (str.equals(KEY_SYSTEM_NAV_2BUTTONS)) {
                    c = 0;
                    break;
                }
                break;
            case -1375361165:
                if (str.equals(KEY_SYSTEM_NAV_GESTURAL)) {
                    c = 1;
                    break;
                }
                break;
            case -117503078:
                if (str.equals(KEY_SYSTEM_NAV_3BUTTONS)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                illustrationPreference.setLottieAnimationResId(C1991R$raw.lottie_system_nav_2_button);
                return;
            case 1:
                illustrationPreference.setLottieAnimationResId(C1991R$raw.lottie_system_nav_fully_gestural);
                return;
            case 2:
                illustrationPreference.setLottieAnimationResId(C1991R$raw.lottie_system_nav_3_button);
                return;
            default:
                return;
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_default;
    }

    private void bindPreferenceAction(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if ((radioButtonPreference instanceof ExtendedRadioButtonPreferenceWithExtras) && !DisplayUtils.isDesktopMode(getContext())) {
            ExtendedRadioButtonPreferenceWithExtras extendedRadioButtonPreferenceWithExtras = (ExtendedRadioButtonPreferenceWithExtras) radioButtonPreference;
            Intent intent = null;
            extendedRadioButtonPreferenceWithExtras.setActionButton((String) null);
            extendedRadioButtonPreferenceWithExtras.setActionButtonOnClickListener((View.OnClickListener) null);
            PackageManager packageManager = getContext().getPackageManager();
            str.hashCode();
            if (str.equals(KEY_SYSTEM_NAV_GESTURAL)) {
                intent = TUTORIAL_INTENT_SYSTEM_NAV_GESTURAL;
            }
            if (intent != null && packageManager.resolveActivity(intent, 131072) != null) {
                intent.addFlags(268435456);
                extendedRadioButtonPreferenceWithExtras.setActionButton(getString(C1992R$string.gesture_navigation_tutorial_button));
                extendedRadioButtonPreferenceWithExtras.setActionButtonOnClickListener(new SystemNavigationGestureSettings$$ExternalSyntheticLambda2(this, intent));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$bindPreferenceAction$2(Intent intent, View view) {
        startActivity(intent);
    }

    public static boolean isCustNavBarAvailable() {
        return Resources.getSystem().getBoolean(17891596);
    }

    private int getCurrGestureNavStyle() {
        if (isCustNavBarAvailable()) {
            return MotorolaSettings.Secure.getInt(getContext().getContentResolver(), "hide_gesture_pill", 0);
        }
        return 0;
    }
}
