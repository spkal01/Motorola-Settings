package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.TwoTargetPreference;

public abstract class DefaultAppPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final PackageManager mPackageManager;
    protected int mUserId = UserHandle.myUserId();
    protected final UserManager mUserManager;

    /* access modifiers changed from: protected */
    public abstract DefaultAppInfo getDefaultAppInfo();

    /* access modifiers changed from: protected */
    public abstract Intent getSettingIntent(DefaultAppInfo defaultAppInfo);

    /* access modifiers changed from: protected */
    public boolean showLabelAsTitle() {
        return false;
    }

    public DefaultAppPreferenceController(Context context) {
        super(context);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    public void updateState(Preference preference) {
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (preference instanceof TwoTargetPreference) {
            ((TwoTargetPreference) preference).setIconSize(1);
        }
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            if (showLabelAsTitle()) {
                preference.setTitle(defaultAppLabel);
            } else {
                preference.setSummary(defaultAppLabel);
            }
            preference.setIcon(Utils.getSafeIcon(getDefaultAppIcon()));
        } else {
            Log.d("DefaultAppPrefControl", "No default app");
            if (showLabelAsTitle()) {
                preference.setTitle(C1992R$string.app_list_preference_none);
            } else {
                preference.setSummary(C1992R$string.app_list_preference_none);
            }
            preference.setIcon((Drawable) null);
        }
        mayUpdateGearIcon(defaultAppInfo, preference);
    }

    private void mayUpdateGearIcon(DefaultAppInfo defaultAppInfo, Preference preference) {
        if (preference instanceof GearPreference) {
            Intent settingIntent = getSettingIntent(defaultAppInfo);
            if (settingIntent != null) {
                ((GearPreference) preference).setOnGearClickListener(new DefaultAppPreferenceController$$ExternalSyntheticLambda0(this, settingIntent));
            } else {
                ((GearPreference) preference).setOnGearClickListener((GearPreference.OnGearClickListener) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$mayUpdateGearIcon$0(Intent intent, GearPreference gearPreference) {
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void startActivity(Intent intent) {
        this.mContext.startActivity(intent);
    }

    public Drawable getDefaultAppIcon() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadIcon();
        }
        return null;
    }

    public CharSequence getDefaultAppLabel() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadLabel();
        }
        return null;
    }
}
