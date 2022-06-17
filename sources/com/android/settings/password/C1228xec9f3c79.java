package com.android.settings.password;

import com.android.internal.widget.LockPatternChecker;
import com.android.settings.password.ChooseLockPattern;

/* renamed from: com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C1228xec9f3c79 implements LockPatternChecker.OnCheckCallback {
    public final /* synthetic */ ChooseLockPattern.ChooseLockPatternFragment f$0;

    public /* synthetic */ C1228xec9f3c79(ChooseLockPattern.ChooseLockPatternFragment chooseLockPatternFragment) {
        this.f$0 = chooseLockPatternFragment;
    }

    public final void onChecked(boolean z, int i) {
        this.f$0.lambda$startCheckPassword$0(z, i);
    }
}
