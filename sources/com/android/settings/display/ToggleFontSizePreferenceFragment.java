package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.C1978R$array;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.motorola.settings.display.PreviewTabLayoutPreferenceFragment;

public class ToggleFontSizePreferenceFragment extends PreviewTabLayoutPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };
    private float[] mValues;

    public int getMetricsCategory() {
        return 340;
    }

    /* access modifiers changed from: protected */
    public int getActivityLayoutResId() {
        if (getActivity().isInMultiWindowMode()) {
            return C1987R$layout.font_scale_activity_multi;
        }
        return C1987R$layout.font_scale_activity;
    }

    /* access modifiers changed from: protected */
    public int getPreviewSampleResIds() {
        return C1987R$layout.font_size_preview;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getContext().getResources();
        ContentResolver contentResolver = getContext().getContentResolver();
        this.mEntries = resources.getStringArray(C1978R$array.entries_font_size);
        TypedArray obtainTypedArray = resources.obtainTypedArray(C1978R$array.entries_font_size_icon);
        int length = obtainTypedArray.length();
        this.mIconRes = new int[length];
        for (int i = 0; i < length; i++) {
            this.mIconRes[i] = obtainTypedArray.getResourceId(i, 0);
        }
        obtainTypedArray.recycle();
        String[] stringArray = resources.getStringArray(C1978R$array.entryvalues_font_size);
        this.mInitialIndex = fontSizeValueToIndex(Settings.System.getFloat(contentResolver, "font_scale", 1.0f), stringArray);
        this.mValues = new float[stringArray.length];
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            this.mValues[i2] = Float.parseFloat(stringArray[i2]);
        }
        getActivity().setTitle(C1992R$string.title_font_size);
    }

    /* access modifiers changed from: protected */
    public Configuration createConfig(Configuration configuration, int i) {
        Configuration configuration2 = new Configuration(configuration);
        configuration2.fontScale = this.mValues[i];
        return configuration2;
    }

    /* access modifiers changed from: protected */
    public void commit() {
        if (getContext() != null) {
            Settings.System.putFloat(getContext().getContentResolver(), "font_scale", this.mValues[this.mCurrentIndex]);
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_font_size;
    }

    public static int fontSizeValueToIndex(float f, String[] strArr) {
        float parseFloat = Float.parseFloat(strArr[0]);
        int i = 1;
        while (i < strArr.length) {
            float parseFloat2 = Float.parseFloat(strArr[i]);
            if (f < parseFloat + ((parseFloat2 - parseFloat) * 0.5f)) {
                return i - 1;
            }
            i++;
            parseFloat = parseFloat2;
        }
        return strArr.length - 1;
    }
}
