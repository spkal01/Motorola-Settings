package com.android.settings.notification;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;

public class ShowOnLockScreenNotificationPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private DevicePolicyManager mDpm;
    private final String mSettingKey;

    public boolean isAvailable() {
        return true;
    }

    public ShowOnLockScreenNotificationPreferenceController(Context context, String str) {
        super(context);
        this.mSettingKey = str;
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    /* access modifiers changed from: package-private */
    public void setDpm(DevicePolicyManager devicePolicyManager) {
        this.mDpm = devicePolicyManager;
    }

    public String getPreferenceKey() {
        return this.mSettingKey;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedListPreference restrictedListPreference = (RestrictedListPreference) preferenceScreen.findPreference(this.mSettingKey);
        restrictedListPreference.clearRestrictedItems();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Context context = this.mContext;
        int i = C1992R$string.lock_screen_notifs_show_all;
        String string = context.getString(i);
        String num = Integer.toString(i);
        arrayList.add(string);
        arrayList2.add(num);
        setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string, num, 4);
        Context context2 = this.mContext;
        int i2 = C1992R$string.lock_screen_notifs_show_alerting;
        String string2 = context2.getString(i2);
        String num2 = Integer.toString(i2);
        arrayList.add(string2);
        arrayList2.add(num2);
        setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string2, num2, 4);
        Context context3 = this.mContext;
        int i3 = C1992R$string.lock_screen_notifs_show_none;
        arrayList.add(context3.getString(i3));
        arrayList2.add(Integer.toString(i3));
        restrictedListPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]));
        restrictedListPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]));
        if (!adminAllowsNotifications() || !getLockscreenNotificationsEnabled()) {
            restrictedListPreference.setValue(Integer.toString(i3));
        } else if (!getLockscreenSilentNotificationsEnabled()) {
            restrictedListPreference.setValue(Integer.toString(i2));
        } else {
            restrictedListPreference.setValue(Integer.toString(i));
        }
        restrictedListPreference.setOnPreferenceChangeListener(this);
        refreshSummary(restrictedListPreference);
    }

    public CharSequence getSummary() {
        if (!adminAllowsNotifications() || !getLockscreenNotificationsEnabled()) {
            return this.mContext.getString(C1992R$string.lock_screen_notifs_show_none);
        }
        if (!getLockscreenSilentNotificationsEnabled()) {
            return this.mContext.getString(C1992R$string.lock_screen_notifs_show_alerting);
        }
        return this.mContext.getString(C1992R$string.lock_screen_notifs_show_all);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        int i = 0;
        int i2 = parseInt != C1992R$string.lock_screen_notifs_show_none ? 1 : 0;
        if (parseInt == C1992R$string.lock_screen_notifs_show_all) {
            i = 1;
        }
        Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", i);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_show_notifications", i2);
        refreshSummary(preference);
        return true;
    }

    private void setRestrictedIfNotificationFeaturesDisabled(RestrictedListPreference restrictedListPreference, CharSequence charSequence, CharSequence charSequence2, int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this.mContext, i, UserHandle.myUserId());
        if (checkIfKeyguardFeaturesDisabled != null && restrictedListPreference != null) {
            restrictedListPreference.addRestrictedItem(new RestrictedListPreference.RestrictedItem(charSequence, charSequence2, checkIfKeyguardFeaturesDisabled));
        }
    }

    private boolean adminAllowsNotifications() {
        return (this.mDpm.getKeyguardDisabledFeatures((ComponentName) null) & 4) == 0;
    }

    private boolean getLockscreenNotificationsEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1) != 0;
    }

    private boolean getLockscreenSilentNotificationsEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) != 0;
    }
}
