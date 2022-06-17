package com.motorola.settings.biometrics.face;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

public final /* synthetic */ class MotoFaceLicensesMenuController$$ExternalSyntheticLambda0 implements MenuItem.OnMenuItemClickListener {
    public final /* synthetic */ Context f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ MotoFaceLicensesMenuController$$ExternalSyntheticLambda0(Context context, Intent intent) {
        this.f$0 = context;
        this.f$1 = intent;
    }

    public final boolean onMenuItemClick(MenuItem menuItem) {
        return this.f$0.startActivity(this.f$1);
    }
}
