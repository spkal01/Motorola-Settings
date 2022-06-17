package com.android.settings.password;

import com.android.internal.widget.LockPatternChecker;
import com.android.settings.password.ChooseLockPassword;

/* renamed from: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C1221xcfed0645 implements LockPatternChecker.OnCheckCallback {
    public final /* synthetic */ ChooseLockPassword.ChooseLockPasswordFragment f$0;

    public /* synthetic */ C1221xcfed0645(ChooseLockPassword.ChooseLockPasswordFragment chooseLockPasswordFragment) {
        this.f$0 = chooseLockPasswordFragment;
    }

    public final void onChecked(boolean z, int i) {
        this.f$0.lambda$startCheckPassword$0(z, i);
    }
}
