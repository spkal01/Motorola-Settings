package com.android.settings.core;

import com.android.settings.core.CategoryMixin;
import java.util.Set;
import java.util.function.Consumer;

public final /* synthetic */ class CategoryMixin$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ Set f$0;

    public /* synthetic */ CategoryMixin$$ExternalSyntheticLambda0(Set set) {
        this.f$0 = set;
    }

    public final void accept(Object obj) {
        ((CategoryMixin.CategoryListener) obj).onCategoriesChanged(this.f$0);
    }
}
