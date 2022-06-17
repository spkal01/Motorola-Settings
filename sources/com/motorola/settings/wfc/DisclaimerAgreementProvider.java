package com.motorola.settings.wfc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.util.Log;

public class DisclaimerAgreementProvider extends ContentProvider {
    public static final String TAG = DisclaimerAgreementProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher;
    private SharedPreferences prefs;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sUriMatcher = uriMatcher;
        uriMatcher.addURI("com.motorola.settings.wfc.provider", "wfc_disclaimer_prefs", 1);
    }

    public boolean onCreate() {
        this.prefs = getContext().getSharedPreferences("wfc_disclaimer_prefs", 0);
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    public Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        boolean z;
        if (bundle != null) {
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"agreed"});
            if (sUriMatcher.match(uri) == 1) {
                int i = bundle.getInt("sub_id_key", 0);
                PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
                if (configForSubId.getBoolean("show_wfc_location_privacy_policy_bool")) {
                    z = this.prefs.getBoolean("key_has_agreed_location_disclaimer" + i, false);
                } else {
                    z = true;
                }
                if (configForSubId.getBoolean("moto_show_customized_wfc_help_and_dialog_bool")) {
                    if (z) {
                        if (this.prefs.getBoolean("key_has_agreed_advantages_disclaimer" + i, false)) {
                            z = true;
                        }
                    }
                    z = false;
                }
                matrixCursor.addRow(new Object[]{Integer.valueOf(z ? 1 : 0)});
                return matrixCursor;
            }
            throw new IllegalArgumentException("Unknown Uri format: " + uri);
        }
        Log.i(TAG, "query: queryArgs parameter is null");
        throw new IllegalArgumentException("Null queryArgs. It should not be null.");
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }
}
