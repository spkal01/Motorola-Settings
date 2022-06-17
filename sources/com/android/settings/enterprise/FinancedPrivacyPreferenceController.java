package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Objects;

public class FinancedPrivacyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final String mPreferenceKey;
    private final PrivacyPreferenceControllerHelper mPrivacyPreferenceControllerHelper;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public FinancedPrivacyPreferenceController(Context context) {
        this(context, "financed_privacy");
        Objects.requireNonNull(context);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public FinancedPrivacyPreferenceController(Context context, String str) {
        this(context, new PrivacyPreferenceControllerHelper(context), str);
        Objects.requireNonNull(context);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @VisibleForTesting
    FinancedPrivacyPreferenceController(Context context, PrivacyPreferenceControllerHelper privacyPreferenceControllerHelper, String str) {
        super(context);
        Objects.requireNonNull(context);
        Objects.requireNonNull(privacyPreferenceControllerHelper);
        this.mPrivacyPreferenceControllerHelper = privacyPreferenceControllerHelper;
        this.mPreferenceKey = str;
    }

    public void updateState(Preference preference) {
        this.mPrivacyPreferenceControllerHelper.updateState(preference);
    }

    public boolean isAvailable() {
        return this.mPrivacyPreferenceControllerHelper.isFinancedDevice();
    }

    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }
}
