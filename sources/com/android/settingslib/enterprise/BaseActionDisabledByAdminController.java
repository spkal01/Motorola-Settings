package com.android.settingslib.enterprise;

import com.android.internal.util.Preconditions;
import com.android.settingslib.RestrictedLockUtils;
import java.util.Objects;

abstract class BaseActionDisabledByAdminController implements ActionDisabledByAdminController {
    protected RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    protected int mEnforcementAdminUserId;
    protected ActionDisabledLearnMoreButtonLauncher mLauncher;
    protected final DeviceAdminStringProvider mStringProvider;

    BaseActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        this.mStringProvider = deviceAdminStringProvider;
    }

    public final void initialize(ActionDisabledLearnMoreButtonLauncher actionDisabledLearnMoreButtonLauncher) {
        Objects.requireNonNull(actionDisabledLearnMoreButtonLauncher, "launcher cannot be null");
        this.mLauncher = actionDisabledLearnMoreButtonLauncher;
    }

    public final void updateEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, int i) {
        assertInitialized();
        this.mEnforcementAdminUserId = i;
        Objects.requireNonNull(enforcedAdmin, "admin cannot be null");
        this.mEnforcedAdmin = enforcedAdmin;
    }

    /* access modifiers changed from: protected */
    public final void assertInitialized() {
        Preconditions.checkState(this.mLauncher != null, "must call initialize() first");
    }
}
