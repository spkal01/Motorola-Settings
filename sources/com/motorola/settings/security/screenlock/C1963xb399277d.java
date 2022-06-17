package com.motorola.settings.security.screenlock;

import com.android.internal.widget.LockPatternChecker;
import com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern;

/* renamed from: com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C1963xb399277d implements LockPatternChecker.OnCheckCallback {
    public final /* synthetic */ PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment f$0;

    public /* synthetic */ C1963xb399277d(PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment privacySpaceChooseLockPatternFragment) {
        this.f$0 = privacySpaceChooseLockPatternFragment;
    }

    public final void onChecked(boolean z, int i) {
        this.f$0.lambda$startCheckPassword$0(z, i);
    }
}
