package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import com.android.settings.C1978R$array;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.display.DisplayDensityConfiguration;
import com.android.settingslib.display.DisplayDensityUtils;
import com.motorola.settings.display.PreviewTabLayoutPreferenceFragment;
import java.util.Arrays;

public class ScreenZoomSettings extends PreviewTabLayoutPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };
    private int mDefaultDensity;
    private int[] mValues;

    public int getMetricsCategory() {
        return 339;
    }

    /* access modifiers changed from: protected */
    public int getActivityLayoutResId() {
        if (getActivity().isInMultiWindowMode()) {
            return C1987R$layout.display_scale_activity_multi;
        }
        return C1987R$layout.display_scale_activity;
    }

    /* access modifiers changed from: protected */
    public int getPreviewSampleResIds() {
        return C1987R$layout.screen_zoom_preview_1;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DisplayDensityUtils displayDensityUtils = new DisplayDensityUtils(getContext());
        int currentIndex = displayDensityUtils.getCurrentIndex();
        if (currentIndex < 0) {
            int i = getResources().getDisplayMetrics().densityDpi;
            this.mValues = new int[]{i};
            this.mEntries = new String[]{getString(DisplayDensityUtils.SUMMARY_DEFAULT)};
            this.mInitialIndex = 0;
            this.mDefaultDensity = i;
        } else {
            this.mValues = displayDensityUtils.getValues();
            this.mEntries = displayDensityUtils.getEntries();
            this.mInitialIndex = currentIndex;
            this.mDefaultDensity = displayDensityUtils.getDefaultDensity();
        }
        TypedArray obtainTypedArray = getContext().getResources().obtainTypedArray(C1978R$array.entries_display_size_icon);
        int min = Math.min(obtainTypedArray.length(), this.mEntries.length);
        this.mEntries = (String[]) Arrays.copyOf(this.mEntries, min);
        this.mValues = Arrays.copyOf(this.mValues, min);
        this.mIconRes = new int[min];
        for (int i2 = 0; i2 < min; i2++) {
            this.mIconRes[i2] = obtainTypedArray.getResourceId(i2, 0);
        }
        obtainTypedArray.recycle();
        getActivity().setTitle(C1992R$string.screen_zoom_title);
    }

    /* access modifiers changed from: protected */
    public Configuration createConfig(Configuration configuration, int i) {
        Configuration configuration2 = new Configuration(configuration);
        configuration2.densityDpi = this.mValues[i];
        return configuration2;
    }

    /* access modifiers changed from: protected */
    public void commit() {
        int i = this.mValues[this.mCurrentIndex];
        if (i == this.mDefaultDensity) {
            DisplayDensityConfiguration.clearForcedDisplayDensity(0);
        } else {
            DisplayDensityConfiguration.setForcedDisplayDensity(0, i);
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_display_size;
    }
}
