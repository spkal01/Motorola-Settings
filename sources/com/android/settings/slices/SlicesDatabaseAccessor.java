package com.android.settings.slices;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Pair;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceData;
import java.util.ArrayList;
import java.util.List;

public class SlicesDatabaseAccessor {
    public static final String[] SELECT_COLUMNS_ALL = {"key", "title", "summary", "screentitle", "keywords", "icon", "fragment", "controller", "slice_type", "unavailable_slice_subtitle"};
    private final Context mContext;
    private final SlicesDatabaseHelper mHelper;

    public SlicesDatabaseAccessor(Context context) {
        this.mContext = context;
        this.mHelper = SlicesDatabaseHelper.getInstance(context);
    }

    public SliceData getSliceDataFromUri(Uri uri) {
        Pair<Boolean, String> pathData = SliceBuilderUtils.getPathData(uri);
        if (pathData != null) {
            Cursor indexedSliceData = getIndexedSliceData((String) pathData.second);
            try {
                SliceData buildSliceData = buildSliceData(indexedSliceData, uri, ((Boolean) pathData.first).booleanValue());
                if (indexedSliceData != null) {
                    indexedSliceData.close();
                }
                return buildSliceData;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        } else {
            throw new IllegalStateException("Invalid Slices uri: " + uri);
        }
        throw th;
    }

    public SliceData getSliceDataFromKey(String str) {
        Cursor indexedSliceData = getIndexedSliceData(str);
        try {
            SliceData buildSliceData = buildSliceData(indexedSliceData, (Uri) null, false);
            if (indexedSliceData != null) {
                indexedSliceData.close();
            }
            return buildSliceData;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public List<Uri> getSliceUris(String str, boolean z) {
        verifyIndexing();
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("public_slice");
        sb.append(z ? "=1" : "=0");
        Cursor query = this.mHelper.getReadableDatabase().query("slices_index", new String[]{"slice_uri"}, sb.toString(), (String[]) null, (String) null, (String) null, (String) null);
        try {
            if (!query.moveToFirst()) {
                query.close();
                return arrayList;
            }
            do {
                Uri parse = Uri.parse(query.getString(0));
                if (TextUtils.isEmpty(str) || TextUtils.equals(str, parse.getAuthority())) {
                    arrayList.add(parse);
                }
            } while (query.moveToNext());
            query.close();
            return arrayList;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private Cursor getIndexedSliceData(String str) {
        verifyIndexing();
        String buildKeyMatchWhereClause = buildKeyMatchWhereClause();
        Cursor query = this.mHelper.getReadableDatabase().query("slices_index", SELECT_COLUMNS_ALL, buildKeyMatchWhereClause, new String[]{str}, (String) null, (String) null, (String) null);
        int count = query.getCount();
        if (count == 0) {
            query.close();
            throw new IllegalStateException("Invalid Slices key from path: " + str);
        } else if (count <= 1) {
            query.moveToFirst();
            return query;
        } else {
            query.close();
            throw new IllegalStateException("Should not match more than 1 slice with path: " + str);
        }
    }

    private String buildKeyMatchWhereClause() {
        return "key" + " = ?";
    }

    private static SliceData buildSliceData(Cursor cursor, Uri uri, boolean z) {
        String string = cursor.getString(cursor.getColumnIndex("key"));
        String string2 = cursor.getString(cursor.getColumnIndex("title"));
        String string3 = cursor.getString(cursor.getColumnIndex("summary"));
        String string4 = cursor.getString(cursor.getColumnIndex("screentitle"));
        String string5 = cursor.getString(cursor.getColumnIndex("keywords"));
        int i = cursor.getInt(cursor.getColumnIndex("icon"));
        String string6 = cursor.getString(cursor.getColumnIndex("fragment"));
        String string7 = cursor.getString(cursor.getColumnIndex("controller"));
        int i2 = cursor.getInt(cursor.getColumnIndex("slice_type"));
        String string8 = cursor.getString(cursor.getColumnIndex("unavailable_slice_subtitle"));
        if (z) {
            i2 = 0;
        }
        return new SliceData.Builder().setKey(string).setTitle(string2).setSummary(string3).setScreenTitle(string4).setKeywords(string5).setIcon(i).setFragmentName(string6).setPreferenceControllerClassName(string7).setUri(uri).setSliceType(i2).setUnavailableSliceSubtitle(string8).build();
    }

    private void verifyIndexing() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().indexSliceData(this.mContext);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
