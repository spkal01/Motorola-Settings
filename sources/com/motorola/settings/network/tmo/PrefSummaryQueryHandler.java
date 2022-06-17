package com.motorola.settings.network.tmo;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import androidx.preference.Preference;

public class PrefSummaryQueryHandler extends AsyncQueryHandler {
    private Preference mPref;
    private String[] mProjection = {"value"};

    public PrefSummaryQueryHandler(ContentResolver contentResolver, Preference preference) {
        super(contentResolver);
        this.mPref = preference;
    }

    public void queryAndUpdatePrefSummary(Uri uri) {
        super.startQuery(50, (Object) null, uri, this.mProjection, (String) null, (String[]) null, (String) null);
    }

    /* access modifiers changed from: protected */
    public void onQueryComplete(int i, Object obj, Cursor cursor) {
        int columnIndex;
        String string;
        if (i == 50) {
            String str = null;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst() && (columnIndex = cursor.getColumnIndex("value")) >= 0 && (string = cursor.getString(columnIndex)) != null) {
                        str = string;
                    }
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            Preference preference = this.mPref;
            if (preference != null) {
                preference.setSummary((CharSequence) str);
            }
        }
    }
}
