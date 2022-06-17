package com.android.settings.core;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.dashboard.CategoryManager;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryMixin implements LifecycleObserver {
    /* access modifiers changed from: private */
    public static final ArraySet<ComponentName> sTileDenylist = new ArraySet<>();
    private int mCategoriesUpdateTaskCount;
    private final List<CategoryListener> mCategoryListeners = new ArrayList();
    /* access modifiers changed from: private */
    public final Context mContext;
    private final PackageReceiver mPackageReceiver = new PackageReceiver();

    public interface CategoryHandler {
        CategoryMixin getCategoryMixin();
    }

    public interface CategoryListener {
        void onCategoriesChanged(Set<String> set);
    }

    static /* synthetic */ int access$108(CategoryMixin categoryMixin) {
        int i = categoryMixin.mCategoriesUpdateTaskCount;
        categoryMixin.mCategoriesUpdateTaskCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$110(CategoryMixin categoryMixin) {
        int i = categoryMixin.mCategoriesUpdateTaskCount;
        categoryMixin.mCategoriesUpdateTaskCount = i - 1;
        return i;
    }

    public CategoryMixin(Context context) {
        this.mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
        updateCategories();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mPackageReceiver);
    }

    public void addCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.add(categoryListener);
    }

    public void removeCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.remove(categoryListener);
    }

    public void updateCategories() {
        updateCategories(false);
    }

    /* access modifiers changed from: package-private */
    public void addToDenylist(ComponentName componentName) {
        sTileDenylist.add(componentName);
    }

    /* access modifiers changed from: package-private */
    public void removeFromDenylist(ComponentName componentName) {
        sTileDenylist.remove(componentName);
    }

    /* access modifiers changed from: package-private */
    public void onCategoriesChanged(Set<String> set) {
        this.mCategoryListeners.forEach(new CategoryMixin$$ExternalSyntheticLambda0(set));
    }

    /* access modifiers changed from: private */
    public void updateCategories(boolean z) {
        if (this.mCategoriesUpdateTaskCount < 2) {
            new CategoriesUpdateTask().execute(new Boolean[]{Boolean.valueOf(z)});
        }
    }

    private class CategoriesUpdateTask extends AsyncTask<Boolean, Void, Set<String>> {
        private final CategoryManager mCategoryManager;
        private Map<ComponentName, Tile> mPreviousTileMap;

        CategoriesUpdateTask() {
            CategoryMixin.access$108(CategoryMixin.this);
            this.mCategoryManager = CategoryManager.get(CategoryMixin.this.mContext);
        }

        /* access modifiers changed from: protected */
        public Set<String> doInBackground(Boolean... boolArr) {
            this.mPreviousTileMap = this.mCategoryManager.getTileByComponentMap();
            this.mCategoryManager.reloadAllCategories(CategoryMixin.this.mContext);
            this.mCategoryManager.updateCategoryFromDenylist(CategoryMixin.sTileDenylist);
            return getChangedCategories(boolArr[0].booleanValue());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Set<String> set) {
            if (set == null || !set.isEmpty()) {
                CategoryMixin.this.onCategoriesChanged(set);
            }
            CategoryMixin.access$110(CategoryMixin.this);
        }

        private Set<String> getChangedCategories(boolean z) {
            if (!z) {
                return null;
            }
            ArraySet arraySet = new ArraySet();
            Map<ComponentName, Tile> tileByComponentMap = this.mCategoryManager.getTileByComponentMap();
            tileByComponentMap.forEach(new CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda0(this, arraySet));
            ArraySet arraySet2 = new ArraySet(this.mPreviousTileMap.keySet());
            arraySet2.removeAll(tileByComponentMap.keySet());
            arraySet2.forEach(new CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda1(this, arraySet));
            return arraySet;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$0(Set set, ComponentName componentName, Tile tile) {
            Tile tile2 = this.mPreviousTileMap.get(componentName);
            if (tile2 == null) {
                Log.i("CategoryMixin", "Tile added: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            } else if (!TextUtils.equals(tile.getTitle(CategoryMixin.this.mContext), tile2.getTitle(CategoryMixin.this.mContext)) || !TextUtils.equals(tile.getSummary(CategoryMixin.this.mContext), tile2.getSummary(CategoryMixin.this.mContext))) {
                Log.i("CategoryMixin", "Tile changed: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$1(Set set, ComponentName componentName) {
            Log.i("CategoryMixin", "Tile removed: " + componentName.flattenToShortString());
            set.add(this.mPreviousTileMap.get(componentName).getCategory());
        }
    }

    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            CategoryMixin.this.updateCategories(true);
        }
    }
}
