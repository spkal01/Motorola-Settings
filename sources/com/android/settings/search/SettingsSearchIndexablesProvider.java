package com.android.settings.search;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import androidx.slice.SliceViewManager;
import com.android.settings.C1992R$string;
import com.android.settings.dashboard.CategoryManager;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.dashboard.DashboardFragmentRegistry;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.drawer.ActivityTile;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableData;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SettingsSearchIndexablesProvider extends SearchIndexablesProvider {
    private static final Collection<String> INVALID_KEYS;
    private Map<String, Boolean> mSearchEnabledByCategoryKeyMap;

    static {
        ArraySet arraySet = new ArraySet();
        INVALID_KEYS = arraySet;
        arraySet.add((Object) null);
        arraySet.add("");
    }

    public boolean onCreate() {
        this.mSearchEnabledByCategoryKeyMap = new ArrayMap();
        return true;
    }

    public Cursor queryXmlResources(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        for (SearchIndexableResource next : getSearchIndexableResourcesFromProvider(getContext())) {
            Object[] objArr = new Object[SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS.length];
            objArr[0] = Integer.valueOf(next.rank);
            objArr[1] = Integer.valueOf(next.xmlResId);
            objArr[2] = next.className;
            objArr[3] = Integer.valueOf(next.iconResId);
            objArr[4] = next.intentAction;
            objArr[5] = next.intentTargetPackage;
            objArr[6] = null;
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    public Cursor queryRawData(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        for (SearchIndexableRaw createIndexableRawColumnObjects : getSearchIndexableRawFromProvider(getContext())) {
            matrixCursor.addRow(createIndexableRawColumnObjects(createIndexableRawColumnObjects));
        }
        return matrixCursor;
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
        for (String str : getNonIndexableKeysFromProvider(getContext())) {
            Object[] objArr = new Object[SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS.length];
            objArr[0] = str;
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    public Cursor queryDynamicRawData(String[] strArr) {
        Context context = getContext();
        ArrayList<SearchIndexableRaw> arrayList = new ArrayList<>();
        for (SearchIndexableData next : FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues()) {
            arrayList.addAll(getDynamicSearchIndexableRawData(context, next));
            Indexable$SearchIndexProvider searchIndexProvider = next.getSearchIndexProvider();
            if (searchIndexProvider instanceof BaseSearchIndexProvider) {
                refreshSearchEnabledState(context, (BaseSearchIndexProvider) searchIndexProvider);
            }
        }
        arrayList.addAll(getInjectionIndexableRawData(context));
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        for (SearchIndexableRaw createIndexableRawColumnObjects : arrayList) {
            matrixCursor.addRow(createIndexableRawColumnObjects(createIndexableRawColumnObjects));
        }
        return matrixCursor;
    }

    public Cursor querySiteMapPairs() {
        Object obj;
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.SITE_MAP_COLUMNS);
        Context context = getContext();
        for (DashboardCategory next : FeatureFactory.getFactory(context).getDashboardFeatureProvider(context).getAllCategories()) {
            String str = DashboardFragmentRegistry.CATEGORY_KEY_TO_PARENT_MAP.get(next.key);
            if (str != null) {
                for (Tile next2 : next.getTiles()) {
                    String str2 = null;
                    if (next2.getMetaData() != null) {
                        str2 = next2.getMetaData().getString("com.android.settings.FRAGMENT_CLASS");
                    }
                    if (str2 == null) {
                        str2 = next2.getComponentName();
                        obj = next2.getTitle(getContext());
                    } else {
                        obj = "";
                    }
                    if (str2 != null) {
                        matrixCursor.newRow().add("parent_class", str).add("child_class", str2).add("child_title", obj);
                    }
                }
            }
        }
        for (String next3 : CustomSiteMapRegistry.CUSTOM_SITE_MAP.keySet()) {
            matrixCursor.newRow().add("parent_class", CustomSiteMapRegistry.CUSTOM_SITE_MAP.get(next3)).add("child_class", next3);
        }
        return matrixCursor;
    }

    public Cursor querySliceUriPairs() {
        Uri uri;
        SliceViewManager instance = SliceViewManager.getInstance(getContext());
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.SLICE_URI_PAIRS_COLUMNS);
        String string = getContext().getString(C1992R$string.config_non_public_slice_query_uri);
        if (!TextUtils.isEmpty(string)) {
            uri = Uri.parse(string);
        } else {
            uri = new Uri.Builder().scheme("content").authority("com.android.settings.slices").build();
        }
        Uri build = new Uri.Builder().scheme("content").authority("android.settings.slices").build();
        Collection<Uri> sliceDescendants = instance.getSliceDescendants(uri);
        sliceDescendants.addAll(instance.getSliceDescendants(build));
        for (Uri next : sliceDescendants) {
            matrixCursor.newRow().add("key", next.getLastPathSegment()).add("slice_uri", next);
        }
        return matrixCursor;
    }

    private List<String> getNonIndexableKeysFromProvider(Context context) {
        Collection<SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableData next : providerValues) {
            System.currentTimeMillis();
            Indexable$SearchIndexProvider searchIndexProvider = next.getSearchIndexProvider();
            try {
                List<String> nonIndexableKeys = searchIndexProvider.getNonIndexableKeys(context);
                if (nonIndexableKeys != null && !nonIndexableKeys.isEmpty()) {
                    if (nonIndexableKeys.removeAll(INVALID_KEYS)) {
                        Log.v("SettingsSearchProvider", searchIndexProvider + " tried to add an empty non-indexable key");
                    }
                    arrayList.addAll(nonIndexableKeys);
                }
            } catch (Exception e) {
                if (System.getProperty("debug.com.android.settings.search.crash_on_error") == null) {
                    Log.e("SettingsSearchProvider", "Error trying to get non-indexable keys from: " + next.getTargetClass().getName(), e);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        return arrayList;
    }

    private List<SearchIndexableResource> getSearchIndexableResourcesFromProvider(Context context) {
        String str;
        Collection<SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableData next : providerValues) {
            List<SearchIndexableResource> xmlResourcesToIndex = next.getSearchIndexProvider().getXmlResourcesToIndex(context, true);
            if (xmlResourcesToIndex != null) {
                for (SearchIndexableResource next2 : xmlResourcesToIndex) {
                    if (TextUtils.isEmpty(next2.className)) {
                        str = next.getTargetClass().getName();
                    } else {
                        str = next2.className;
                    }
                    next2.className = str;
                }
                arrayList.addAll(xmlResourcesToIndex);
            }
        }
        return arrayList;
    }

    private List<SearchIndexableRaw> getSearchIndexableRawFromProvider(Context context) {
        Collection<SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableData next : providerValues) {
            List<SearchIndexableRaw> rawDataToIndex = next.getSearchIndexProvider().getRawDataToIndex(context, true);
            if (rawDataToIndex != null) {
                for (SearchIndexableRaw searchIndexableRaw : rawDataToIndex) {
                    searchIndexableRaw.className = next.getTargetClass().getName();
                }
                arrayList.addAll(rawDataToIndex);
            }
        }
        return arrayList;
    }

    private List<SearchIndexableRaw> getDynamicSearchIndexableRawData(Context context, SearchIndexableData searchIndexableData) {
        List<SearchIndexableRaw> dynamicRawDataToIndex = searchIndexableData.getSearchIndexProvider().getDynamicRawDataToIndex(context, true);
        if (dynamicRawDataToIndex == null) {
            return new ArrayList();
        }
        for (SearchIndexableRaw searchIndexableRaw : dynamicRawDataToIndex) {
            searchIndexableRaw.className = searchIndexableData.getTargetClass().getName();
        }
        return dynamicRawDataToIndex;
    }

    /* access modifiers changed from: package-private */
    public List<SearchIndexableRaw> getInjectionIndexableRawData(Context context) {
        DashboardFeatureProvider dashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        ArrayList arrayList = new ArrayList();
        String packageName = context.getPackageName();
        for (DashboardCategory next : dashboardFeatureProvider.getAllCategories()) {
            if (!this.mSearchEnabledByCategoryKeyMap.containsKey(next.key) || this.mSearchEnabledByCategoryKeyMap.get(next.key).booleanValue()) {
                for (Tile next2 : next.getTiles()) {
                    if (isEligibleForIndexing(packageName, next2)) {
                        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                        CharSequence title = next2.getTitle(context);
                        String str = null;
                        String charSequence = TextUtils.isEmpty(title) ? null : title.toString();
                        searchIndexableRaw.title = charSequence;
                        if (!TextUtils.isEmpty(charSequence)) {
                            searchIndexableRaw.key = dashboardFeatureProvider.getDashboardKeyForTile(next2);
                            CharSequence summary = next2.getSummary(context);
                            if (!TextUtils.isEmpty(summary)) {
                                str = summary.toString();
                            }
                            searchIndexableRaw.summaryOn = str;
                            searchIndexableRaw.summaryOff = str;
                            searchIndexableRaw.className = DashboardFragmentRegistry.CATEGORY_KEY_TO_PARENT_MAP.get(next2.getCategory());
                            arrayList.add(searchIndexableRaw);
                        }
                    }
                }
            } else {
                Log.i("SettingsSearchProvider", "Skip indexing category: " + next.key);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public void refreshSearchEnabledState(Context context, BaseSearchIndexProvider baseSearchIndexProvider) {
        DashboardCategory tilesByCategory;
        String name = baseSearchIndexProvider.getClass().getName();
        int lastIndexOf = name.lastIndexOf("$");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        String str = DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(name);
        if (str != null && (tilesByCategory = CategoryManager.get(context).getTilesByCategory(context, str)) != null) {
            this.mSearchEnabledByCategoryKeyMap.put(tilesByCategory.key, Boolean.valueOf(baseSearchIndexProvider.isPageSearchEnabled(context)));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isEligibleForIndexing(String str, Tile tile) {
        return !TextUtils.equals(str, tile.getPackageName()) || !(tile instanceof ActivityTile);
    }

    private static Object[] createIndexableRawColumnObjects(SearchIndexableRaw searchIndexableRaw) {
        Object[] objArr = new Object[SearchIndexablesContract.INDEXABLES_RAW_COLUMNS.length];
        objArr[1] = searchIndexableRaw.title;
        objArr[2] = searchIndexableRaw.summaryOn;
        objArr[3] = searchIndexableRaw.summaryOff;
        objArr[4] = searchIndexableRaw.entries;
        objArr[5] = searchIndexableRaw.keywords;
        objArr[6] = searchIndexableRaw.screenTitle;
        objArr[7] = searchIndexableRaw.className;
        objArr[8] = Integer.valueOf(searchIndexableRaw.iconResId);
        objArr[9] = searchIndexableRaw.intentAction;
        objArr[10] = searchIndexableRaw.intentTargetPackage;
        objArr[11] = searchIndexableRaw.intentTargetClass;
        objArr[12] = searchIndexableRaw.key;
        objArr[13] = Integer.valueOf(searchIndexableRaw.userId);
        return objArr;
    }
}
