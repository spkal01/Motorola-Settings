package com.motorola.settings.network;

import androidx.fragment.app.FragmentManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.function.Consumer;

public final /* synthetic */ class MotoPreferenceControllerListHelper$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ FragmentManager f$1;
    public final /* synthetic */ Lifecycle f$2;

    public /* synthetic */ MotoPreferenceControllerListHelper$$ExternalSyntheticLambda0(int i, FragmentManager fragmentManager, Lifecycle lifecycle) {
        this.f$0 = i;
        this.f$1 = fragmentManager;
        this.f$2 = lifecycle;
    }

    public final void accept(Object obj) {
        MotoPreferenceControllerListHelper.lambda$initControllers$0(this.f$0, this.f$1, this.f$2, (AbstractPreferenceController) obj);
    }
}
