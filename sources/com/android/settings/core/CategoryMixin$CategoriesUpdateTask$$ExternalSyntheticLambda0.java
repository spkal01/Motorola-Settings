package com.android.settings.core;

import android.content.ComponentName;
import com.android.settings.core.CategoryMixin;
import com.android.settingslib.drawer.Tile;
import java.util.Set;
import java.util.function.BiConsumer;

public final /* synthetic */ class CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ CategoryMixin.CategoriesUpdateTask f$0;
    public final /* synthetic */ Set f$1;

    public /* synthetic */ CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda0(CategoryMixin.CategoriesUpdateTask categoriesUpdateTask, Set set) {
        this.f$0 = categoriesUpdateTask;
        this.f$1 = set;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.lambda$getChangedCategories$0(this.f$1, (ComponentName) obj, (Tile) obj2);
    }
}
