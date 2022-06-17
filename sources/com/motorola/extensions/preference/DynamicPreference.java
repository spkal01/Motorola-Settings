package com.motorola.extensions.preference;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import com.motorola.extensions.preference.DynamicPreferenceDataObserver;

public class DynamicPreference extends Preference implements DynamicPreferenceDataObserver.IAutoRefresh {
    private boolean mAutoRefresh;
    private DynamicPreferenceDataObserver mObserver;
    private Uri mPreferenceUri;

    public DynamicPreference(Context context) {
        super(context);
    }

    public DynamicPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DynamicPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void onAttached() {
        super.onAttached();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        checkObserver();
    }

    public void onDetached() {
        super.onDetached();
        DynamicPreferenceDataObserver dynamicPreferenceDataObserver = this.mObserver;
        if (dynamicPreferenceDataObserver != null) {
            dynamicPreferenceDataObserver.onDetached();
            this.mObserver = null;
        }
    }

    private void checkObserver() {
        if (this.mPreferenceUri != null && this.mAutoRefresh && this.mObserver == null) {
            DynamicPreferenceDataObserver dynamicPreferenceDataObserver = new DynamicPreferenceDataObserver(getContext());
            this.mObserver = dynamicPreferenceDataObserver;
            dynamicPreferenceDataObserver.start(this.mPreferenceUri, this);
        }
    }

    public void setAutoRefresh(Uri uri, boolean z) {
        this.mPreferenceUri = uri;
        this.mAutoRefresh = z;
        DynamicPreferenceDataObserver dynamicPreferenceDataObserver = this.mObserver;
        if (dynamicPreferenceDataObserver != null) {
            dynamicPreferenceDataObserver.stop();
            this.mObserver = null;
        }
        checkObserver();
    }

    public void refresh() {
        String string;
        if (this.mPreferenceUri != null) {
            Cursor query = getContext().getContentResolver().query(this.mPreferenceUri, (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        int columnIndex = query.getColumnIndex("visible");
                        boolean z = true;
                        if (columnIndex >= 0) {
                            setVisible(query.getInt(columnIndex) != 0);
                        }
                        int columnIndex2 = query.getColumnIndex("enabled");
                        if (columnIndex2 >= 0) {
                            if (query.getInt(columnIndex2) == 0) {
                                z = false;
                            }
                            setEnabled(z);
                        }
                        int columnIndex3 = query.getColumnIndex("value");
                        if (columnIndex3 >= 0 && (string = query.getString(columnIndex3)) != null) {
                            setSummary((CharSequence) string);
                        }
                    }
                } catch (Throwable th) {
                    query.close();
                    throw th;
                }
            }
            if (query != null) {
                query.close();
            }
        }
    }
}
