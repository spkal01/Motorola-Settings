package com.android.settings.core;

import android.content.ComponentName;
import com.android.settings.core.CategoryMixin;
import java.util.Set;
import java.util.function.Consumer;

public final /* synthetic */ class CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ CategoryMixin.CategoriesUpdateTask f$0;
    public final /* synthetic */ Set f$1;

    public /* synthetic */ CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda1(CategoryMixin.CategoriesUpdateTask categoriesUpdateTask, Set set) {
        this.f$0 = categoriesUpdateTask;
        this.f$1 = set;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$getChangedCategories$1(this.f$1, (ComponentName) obj);
    }
}
