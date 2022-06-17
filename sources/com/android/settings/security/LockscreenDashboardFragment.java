package com.android.settings.security;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.AmbientDisplayAlwaysOnPreferenceController;
import com.android.settings.display.AmbientDisplayNotificationsPreferenceController;
import com.android.settings.gestures.DoubleTapScreenPreferenceController;
import com.android.settings.gestures.PickupGesturePreferenceController;
import com.android.settings.gestures.TapScreenGesturePreferenceController;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.OwnerInfoPreferenceController;
import com.android.settings.security.screenlock.LockScreenPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.motorola.settings.display.AmbientDisplayPreferenceCategoryController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LockscreenDashboardFragment extends DashboardFragment implements OwnerInfoPreferenceController.OwnerInfoCallback {
    static final String KEY_ADD_USER_FROM_LOCK_SCREEN = "security_lockscreen_add_users_when_locked";
    static final String KEY_LOCK_SCREEN_NOTIFICATON = "security_setting_lock_screen_notif";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE = "security_setting_lock_screen_notif_work";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER = "security_setting_lock_screen_notif_work_header";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.security_lockscreen_settings) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new LockScreenNotificationPreferenceController(context));
            arrayList.add(new OwnerInfoPreferenceController(context, (ObservablePreferenceFragment) null));
            return arrayList;
        }

        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(LockscreenDashboardFragment.KEY_ADD_USER_FROM_LOCK_SCREEN);
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return new LockScreenPreferenceController(context, "anykey").isAvailable();
        }
    };
    private AmbientDisplayConfiguration mConfig;
    private OwnerInfoPreferenceController mOwnerInfoPreferenceController;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "LockscreenDashboardFragment";
    }

    public int getMetricsCategory() {
        return 882;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.security_lockscreen_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_lockscreen;
    }

    public void onAttach(Context context) {
        Class cls = PickupGesturePreferenceController.class;
        Class cls2 = DoubleTapScreenPreferenceController.class;
        Class cls3 = AmbientDisplayNotificationsPreferenceController.class;
        Class cls4 = AmbientDisplayAlwaysOnPreferenceController.class;
        super.onAttach(context);
        ((AmbientDisplayAlwaysOnPreferenceController) use(cls4)).setConfig(getConfig(context));
        ((AmbientDisplayNotificationsPreferenceController) use(cls3)).setConfig(getConfig(context));
        ((DoubleTapScreenPreferenceController) use(cls2)).setConfig(getConfig(context));
        ((PickupGesturePreferenceController) use(cls)).setConfig(getConfig(context));
        ((AmbientDisplayPreferenceCategoryController) use(AmbientDisplayPreferenceCategoryController.class)).setChildren(Arrays.asList(new AbstractPreferenceController[]{use(cls4), use(TapScreenGesturePreferenceController.class), use(cls3), use(cls2), use(cls)}));
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, KEY_LOCK_SCREEN_NOTIFICATON, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE);
        settingsLifecycle.addObserver(lockScreenNotificationPreferenceController);
        arrayList.add(lockScreenNotificationPreferenceController);
        OwnerInfoPreferenceController ownerInfoPreferenceController = new OwnerInfoPreferenceController(context, this);
        this.mOwnerInfoPreferenceController = ownerInfoPreferenceController;
        arrayList.add(ownerInfoPreferenceController);
        return arrayList;
    }

    public void onOwnerInfoUpdated() {
        OwnerInfoPreferenceController ownerInfoPreferenceController = this.mOwnerInfoPreferenceController;
        if (ownerInfoPreferenceController != null) {
            ownerInfoPreferenceController.updateSummary();
        }
    }

    private AmbientDisplayConfiguration getConfig(Context context) {
        if (this.mConfig == null) {
            this.mConfig = new AmbientDisplayConfiguration(context);
        }
        return this.mConfig;
    }
}
