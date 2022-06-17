package com.android.settings.applications;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

public class GameSettingsPreferenceController extends BasePreferenceController {
    @VisibleForTesting
    final GameSettingsFeatureProvider mGameSettingsFeatureProvider;

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

    public GameSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mGameSettingsFeatureProvider = FeatureFactory.getFactory(context).getGameSettingsFeatureProvider();
    }

    GameSettingsPreferenceController(Context context, String str, GameSettingsFeatureProvider gameSettingsFeatureProvider) {
        super(context, str);
        this.mGameSettingsFeatureProvider = gameSettingsFeatureProvider;
    }

    public int getAvailabilityStatus() {
        return this.mGameSettingsFeatureProvider.isSupported(this.mContext) ? 0 : 3;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mGameSettingsFeatureProvider.launchGameSettings(this.mContext);
        return true;
    }
}
