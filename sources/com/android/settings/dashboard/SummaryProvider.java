package com.android.settings.dashboard;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.C1992R$string;
import com.android.settings.backup.BackupSettingsHelper;

public class SummaryProvider extends ContentProvider {
    public boolean onCreate() {
        return true;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        str.hashCode();
        if (str.equals("backup")) {
            bundle2.putString("com.android.settings.summary", new BackupSettingsHelper(getContext()).getSummary());
        } else if (str.equals("user")) {
            Context context = getContext();
            UserInfo userInfo = ((UserManager) context.getSystemService(UserManager.class)).getUserInfo(UserHandle.myUserId());
            bundle2.putString("com.android.settings.summary", context.getString(C1992R$string.users_summary, new Object[]{userInfo.name}));
        } else {
            throw new IllegalArgumentException("Unknown Uri format: " + str2);
        }
        return bundle2;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException();
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }
}
