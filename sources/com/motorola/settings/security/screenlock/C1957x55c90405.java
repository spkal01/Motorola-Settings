package com.motorola.settings.security.screenlock;

import com.android.internal.widget.LockPatternChecker;
import com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPassword;

/* renamed from: com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPassword$PrivacySpaceChooseLockPasswordFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C1957x55c90405 implements LockPatternChecker.OnCheckCallback {
    public final /* synthetic */ PrivacySpaceChooseLockPassword.PrivacySpaceChooseLockPasswordFragment f$0;

    public /* synthetic */ C1957x55c90405(PrivacySpaceChooseLockPassword.PrivacySpaceChooseLockPasswordFragment privacySpaceChooseLockPasswordFragment) {
        this.f$0 = privacySpaceChooseLockPasswordFragment;
    }

    public final void onChecked(boolean z, int i) {
        this.f$0.lambda$startCheckPassword$0(z, i);
    }
}
