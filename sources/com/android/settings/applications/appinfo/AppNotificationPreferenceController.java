package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;

public class AppNotificationPreferenceController extends AppInfoPreferenceControllerBase {
    private final NotificationBackend mBackend = new NotificationBackend();
    private String mChannelId = null;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppNotificationPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void setParentFragment(AppInfoDashboardFragment appInfoDashboardFragment) {
        super.setParentFragment(appInfoDashboardFragment);
        if (appInfoDashboardFragment != null && appInfoDashboardFragment.getActivity() != null && appInfoDashboardFragment.getActivity().getIntent() != null) {
            this.mChannelId = appInfoDashboardFragment.getActivity().getIntent().getStringExtra(":settings:fragment_args_key");
        }
    }

    public void updateState(Preference preference) {
        ThreadUtils.postOnBackgroundThread((Runnable) new AppNotificationPreferenceController$$ExternalSyntheticLambda1(this, preference));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(Preference preference) {
        ThreadUtils.postOnMainThread(new AppNotificationPreferenceController$$ExternalSyntheticLambda0(preference, getNotificationSummary(this.mParent.getAppEntry(), this.mContext, this.mBackend)));
    }

    /* access modifiers changed from: protected */
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppNotificationSettings.class;
    }

    /* access modifiers changed from: protected */
    public Bundle getArguments() {
        if (this.mChannelId == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString(":settings:fragment_args_key", this.mChannelId);
        return bundle;
    }

    private CharSequence getNotificationSummary(ApplicationsState.AppEntry appEntry, Context context, NotificationBackend notificationBackend) {
        return (appEntry == null || appEntry.info == null) ? "" : getNotificationSummary(notificationBackend.loadAppRow(context, context.getPackageManager(), appEntry.info), context);
    }

    public static CharSequence getNotificationSummary(NotificationBackend.AppRow appRow, Context context) {
        if (appRow == null) {
            return "";
        }
        if (appRow.banned) {
            return context.getText(C1992R$string.notifications_disabled);
        }
        int i = appRow.channelCount;
        if (i == 0) {
            return NotificationBackend.getSentSummary(context, appRow.sentByApp, false);
        }
        int i2 = appRow.blockedChannelCount;
        if (i == i2) {
            return context.getText(C1992R$string.notifications_disabled);
        }
        if (i2 == 0) {
            return NotificationBackend.getSentSummary(context, appRow.sentByApp, false);
        }
        int i3 = C1992R$string.notifications_enabled_with_info;
        Resources resources = context.getResources();
        int i4 = C1990R$plurals.notifications_categories_off;
        int i5 = appRow.blockedChannelCount;
        return context.getString(i3, new Object[]{NotificationBackend.getSentSummary(context, appRow.sentByApp, false), resources.getQuantityString(i4, i5, new Object[]{Integer.valueOf(i5)})});
    }
}
