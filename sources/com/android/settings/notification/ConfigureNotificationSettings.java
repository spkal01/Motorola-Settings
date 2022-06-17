package com.android.settings.notification;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ConfigureNotificationSettings extends DashboardFragment implements OnActivityResultListener {
    static final String KEY_SWIPE_DOWN = "gesture_swipe_down_fingerprint_notifications";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.configure_notification_settings) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ConfigureNotificationSettings.buildPreferenceControllers(context, (Application) null, (Fragment) null);
        }

        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(ConfigureNotificationSettings.KEY_SWIPE_DOWN);
            return nonIndexableKeys;
        }
    };
    private NotificationAssistantPreferenceController mNotificationAssistantPreferenceController;
    private RingtonePreference mRequestPreference;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ConfigNotiSettings";
    }

    public int getMetricsCategory() {
        return 337;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.configure_notification_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_configure_notifications;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, activity != null ? activity.getApplication() : null, this);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        NotificationAssistantPreferenceController notificationAssistantPreferenceController = (NotificationAssistantPreferenceController) use(NotificationAssistantPreferenceController.class);
        this.mNotificationAssistantPreferenceController = notificationAssistantPreferenceController;
        notificationAssistantPreferenceController.setFragment(this);
        this.mNotificationAssistantPreferenceController.setBackend(new NotificationBackend());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Application application, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ShowOnLockScreenNotificationPreferenceController(context, "lock_screen_notifications"));
        arrayList.add(new NotificationRingtonePreferenceController(context) {
            public String getPreferenceKey() {
                return "notification_default_ringtone";
            }
        });
        arrayList.add(new EmergencyBroadcastPreferenceController(context, "app_and_notif_cell_broadcast_settings"));
        return arrayList;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof RingtonePreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        writePreferenceClickMetric(preference);
        RingtonePreference ringtonePreference = (RingtonePreference) preference;
        this.mRequestPreference = ringtonePreference;
        ringtonePreference.onPrepareRingtonePickerIntent(ringtonePreference.getIntent());
        getActivity().startActivityForResultAsUser(this.mRequestPreference.getIntent(), 200, (Bundle) null, UserHandle.of(this.mRequestPreference.getUserId()));
        return true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }

    /* access modifiers changed from: protected */
    public void enableNAS(ComponentName componentName) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        NotificationAssistantPreferenceController notificationAssistantPreferenceController = (NotificationAssistantPreferenceController) use(NotificationAssistantPreferenceController.class);
        notificationAssistantPreferenceController.setNotificationAssistantGranted(componentName);
        notificationAssistantPreferenceController.updateState(preferenceScreen.findPreference(notificationAssistantPreferenceController.getPreferenceKey()));
    }
}
