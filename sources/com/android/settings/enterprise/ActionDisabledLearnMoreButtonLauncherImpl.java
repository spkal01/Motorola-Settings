package com.android.settings.enterprise;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.Settings;
import com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd;
import com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher;
import java.util.Objects;

public final class ActionDisabledLearnMoreButtonLauncherImpl extends ActionDisabledLearnMoreButtonLauncher {
    private final Activity mActivity;
    private final AlertDialog.Builder mBuilder;

    ActionDisabledLearnMoreButtonLauncherImpl(Activity activity, AlertDialog.Builder builder) {
        Objects.requireNonNull(activity, "activity cannot be null");
        this.mActivity = activity;
        Objects.requireNonNull(builder, "builder cannot be null");
        this.mBuilder = builder;
    }

    public void setLearnMoreButton(Runnable runnable) {
        Objects.requireNonNull(runnable, "action cannot be null");
        this.mBuilder.setNeutralButton(C1992R$string.learn_more, new C0930xa9f31308(runnable));
    }

    /* access modifiers changed from: protected */
    public void launchShowAdminPolicies(Context context, UserHandle userHandle, ComponentName componentName) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(userHandle, "user cannot be null");
        Objects.requireNonNull(componentName, "admin cannot be null");
        this.mActivity.startActivityAsUser(new Intent().setClass(this.mActivity, DeviceAdminAdd.class).putExtra("android.app.extra.DEVICE_ADMIN", componentName).putExtra("android.app.extra.CALLED_FROM_SUPPORT_DIALOG", true), userHandle);
    }

    /* access modifiers changed from: protected */
    public void launchShowAdminSettings(Context context) {
        Objects.requireNonNull(context, "context cannot be null");
        this.mActivity.startActivity(new Intent().setClass(this.mActivity, Settings.DeviceAdminSettingsActivity.class).addFlags(268435456));
    }

    /* access modifiers changed from: protected */
    public void finishSelf() {
        this.mActivity.finish();
    }
}
