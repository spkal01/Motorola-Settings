package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.BannerMessagePreference;

public class SmartAutoRotatePermissionController extends BasePreferenceController implements LifecycleObserver, OnResume {
    private final Intent mIntent;
    private BannerMessagePreference mPreference;

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

    public SmartAutoRotatePermissionController(Context context, String str) {
        super(context, str);
        String rotationResolverPackageName = context.getPackageManager().getRotationResolverPackageName();
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        this.mIntent = intent;
        intent.setData(Uri.parse("package:" + rotationResolverPackageName));
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        BannerMessagePreference bannerMessagePreference = (BannerMessagePreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = bannerMessagePreference;
        bannerMessagePreference.setPositiveButtonText(C1992R$string.auto_rotate_manage_permission_button).setPositiveButtonOnClickListener(new SmartAutoRotatePermissionController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        this.mContext.startActivity(this.mIntent);
    }

    public void onResume() {
        updateState(this.mPreference);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setVisible(isAvailable());
        }
    }

    public int getAvailabilityStatus() {
        return (!SmartAutoRotateController.isRotationResolverServiceAvailable(this.mContext) || SmartAutoRotateController.hasSufficientPermission(this.mContext)) ? 3 : 1;
    }
}
