package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class FaceProfileStatusPreferenceController extends FaceStatusPreferenceController {
    private static final String KEY_FACE_SETTINGS = "face_settings_profile";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 2;
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

    public FaceProfileStatusPreferenceController(Context context) {
        super(context, KEY_FACE_SETTINGS);
    }

    public FaceProfileStatusPreferenceController(Context context, String str) {
        super(context, str);
    }

    public FaceProfileStatusPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_FACE_SETTINGS, lifecycle);
    }

    public FaceProfileStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    /* access modifiers changed from: protected */
    public boolean isUserSupported() {
        int i = this.mProfileChallengeUserId;
        return i != -10000 && this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i);
    }

    /* access modifiers changed from: protected */
    public int getUserId() {
        return this.mProfileChallengeUserId;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setTitle((CharSequence) this.mContext.getResources().getString(C1992R$string.security_settings_face_profile_preference_title));
    }
}
