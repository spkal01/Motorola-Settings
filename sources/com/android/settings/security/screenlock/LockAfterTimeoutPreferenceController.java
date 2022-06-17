package com.android.settings.security.screenlock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1992R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.display.TimeoutListPreference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.security.trustagent.TrustAgentManager;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;

public class LockAfterTimeoutPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final DevicePolicyManager mDPM;
    private final LockPatternUtils mLockPatternUtils;
    private final TrustAgentManager mTrustAgentManager;
    private final int mUserId;

    public String getPreferenceKey() {
        return "lock_after_timeout";
    }

    public LockAfterTimeoutPreferenceController(Context context, int i, LockPatternUtils lockPatternUtils) {
        super(context);
        this.mUserId = i;
        this.mLockPatternUtils = lockPatternUtils;
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mTrustAgentManager = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getTrustAgentManager();
    }

    public boolean isAvailable() {
        if (!this.mLockPatternUtils.isSecure(this.mUserId)) {
            return false;
        }
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
        if (keyguardStoredPasswordQuality == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
            return true;
        }
        return false;
    }

    public void updateState(Preference preference) {
        TimeoutListPreference timeoutListPreference = (TimeoutListPreference) preference;
        setupLockAfterPreference(timeoutListPreference);
        updateLockAfterPreferenceSummary(timeoutListPreference);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", Integer.parseInt((String) obj));
            updateState(preference);
            return true;
        } catch (NumberFormatException e) {
            Log.e("PrefControllerMixin", "could not persist lockAfter timeout setting", e);
            return true;
        }
    }

    private void setupLockAfterPreference(TimeoutListPreference timeoutListPreference) {
        long j = Settings.Secure.getLong(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", 5000);
        if (!timeoutListPreference.isTimeoutInDefaultList(j)) {
            timeoutListPreference.addCurrentSelectionToTimeoutsList(j);
        } else {
            timeoutListPreference.resetTimeoutList();
        }
        timeoutListPreference.setValue(String.valueOf(j));
        if (this.mDPM != null) {
            timeoutListPreference.removeUnusableTimeouts(Math.max(0, this.mDPM.getMaximumTimeToLock((ComponentName) null, UserHandle.myUserId()) - ((long) Math.max(0, Settings.System.getInt(this.mContext.getContentResolver(), "screen_off_timeout", 0)))), RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext), true);
        }
    }

    private void updateLockAfterPreferenceSummary(TimeoutListPreference timeoutListPreference) {
        CharSequence charSequence;
        if (timeoutListPreference.isDisabledByAdmin()) {
            charSequence = this.mContext.getText(C1992R$string.disabled_by_policy_title);
        } else {
            long j = Settings.Secure.getLong(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", 5000);
            CharSequence[] entries = timeoutListPreference.getEntries();
            CharSequence[] entryValues = timeoutListPreference.getEntryValues();
            int i = 0;
            for (int i2 = 0; i2 < entryValues.length; i2++) {
                if (j >= Long.valueOf(entryValues[i2].toString()).longValue()) {
                    i = i2;
                }
            }
            CharSequence charSequence2 = null;
            if (timeoutListPreference.isDevicePolicyEnforced() && timeoutListPreference.getMaxTimeout() > 0 && timeoutListPreference.getMaxTimeout() <= j) {
                charSequence2 = timeoutListPreference.getMaxTimeoutString();
            }
            CharSequence activeTrustAgentLabel = this.mTrustAgentManager.getActiveTrustAgentLabel(this.mContext, this.mLockPatternUtils);
            if (TextUtils.isEmpty(activeTrustAgentLabel)) {
                Context context = this.mContext;
                int i3 = C1992R$string.lock_after_timeout_summary;
                Object[] objArr = new Object[1];
                if (charSequence2 == null) {
                    charSequence2 = entries[i];
                }
                objArr[0] = charSequence2;
                charSequence = context.getString(i3, objArr);
            } else if (charSequence2 == null && Long.valueOf(entryValues[i].toString()).longValue() == 0) {
                charSequence = this.mContext.getString(C1992R$string.lock_immediately_summary_with_exception, new Object[]{activeTrustAgentLabel});
            } else {
                Context context2 = this.mContext;
                int i4 = C1992R$string.lock_after_timeout_summary_with_exception;
                Object[] objArr2 = new Object[2];
                if (charSequence2 == null) {
                    charSequence2 = entries[i];
                }
                objArr2[0] = charSequence2;
                objArr2[1] = activeTrustAgentLabel;
                charSequence = context2.getString(i4, objArr2);
            }
        }
        timeoutListPreference.setSummary(charSequence);
    }
}
