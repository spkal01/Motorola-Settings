package com.android.settings.gestures;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.motorola.settings.widget.ExtendedIllustrationPreference;

public class OneHandedSettings extends AccessibilityShortcutPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.one_handed_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return OneHandedSettingsUtils.isSupportOneHandedMode();
        }
    };
    private String mFeatureName;
    private OneHandedSettingsUtils mUtils;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return null;
    }

    public int getMetricsCategory() {
        return 1841;
    }

    /* access modifiers changed from: protected */
    public String getShortcutPreferenceKey() {
        return "one_handed_shortcuts_preference";
    }

    /* access modifiers changed from: protected */
    public boolean showGeneralCategory() {
        return true;
    }

    /* access modifiers changed from: protected */
    /* renamed from: updatePreferenceStates */
    public void lambda$onStart$0() {
        int i;
        int i2;
        OneHandedSettingsUtils.setUserId(UserHandle.myUserId());
        super.updatePreferenceStates();
        ExtendedIllustrationPreference extendedIllustrationPreference = (ExtendedIllustrationPreference) getPreferenceScreen().findPreference("one_handed_header");
        if (extendedIllustrationPreference != null) {
            boolean isSwipeDownNotificationEnabled = OneHandedSettingsUtils.isSwipeDownNotificationEnabled(getContext());
            if (isSwipeDownNotificationEnabled) {
                i = C1991R$raw.lottie_swipe_for_notifications;
            } else {
                i = C1991R$raw.lottie_one_hand_mode;
            }
            extendedIllustrationPreference.setLottieAnimationResId(i);
            if (isSwipeDownNotificationEnabled) {
                i2 = C1983R$drawable.ic_img_one_handed_mode_02;
            } else {
                i2 = C1983R$drawable.ic_img_one_handed_mode_01;
            }
            extendedIllustrationPreference.setFallbackResource(i2);
        }
    }

    public int getDialogMetricsCategory(int i) {
        int dialogMetricsCategory = super.getDialogMetricsCategory(i);
        if (dialogMetricsCategory == 0) {
            return 1841;
        }
        return dialogMetricsCategory;
    }

    public void onStart() {
        super.onStart();
        OneHandedSettingsUtils oneHandedSettingsUtils = new OneHandedSettingsUtils(getContext());
        this.mUtils = oneHandedSettingsUtils;
        oneHandedSettingsUtils.registerToggleAwareObserver(new OneHandedSettings$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1(Uri uri) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new OneHandedSettings$$ExternalSyntheticLambda1(this));
        }
    }

    public void onStop() {
        super.onStop();
        this.mUtils.unregisterToggleAwareObserver();
    }

    /* access modifiers changed from: protected */
    public ComponentName getComponentName() {
        return AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME;
    }

    /* access modifiers changed from: protected */
    public CharSequence getLabelName() {
        return this.mFeatureName;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.one_handed_settings;
    }

    public void onCreate(Bundle bundle) {
        this.mFeatureName = getContext().getString(C1992R$string.one_handed_title);
        super.onCreate(bundle);
    }
}
