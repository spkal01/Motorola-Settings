package com.motorola.settings.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class Smart5gPreferenceController extends TelephonyTogglePreferenceController {
    private static final String AUTHORITY = "com.motorola.smart5g.dynamicprefprovider";
    private static final String LOG_TAG = "Smart5gPreferenceController";
    private SwitchPreference mPreference;
    private boolean mSmart5gEnabled;
    private boolean mVisible;

    private String getTypeForPath() {
        return "checkbox_preference";
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public Smart5gPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = switchPreference;
        if (switchPreference != null) {
            super.displayPreference(preferenceScreen);
        }
    }

    public void init(int i) {
        this.mSubId = i;
        this.mSmart5gEnabled = false;
        this.mVisible = false;
        setProperties();
    }

    public int getAvailabilityStatus(int i) {
        return this.mVisible ? 0 : 2;
    }

    public boolean setChecked(boolean z) {
        Uri uri = getUri();
        if (uri == null) {
            return false;
        }
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put("value", Boolean.valueOf(z));
            contentResolver.update(uri, contentValues, (String) null, (String[]) null);
            return true;
        } catch (Throwable th) {
            MotoMnsLog.logw(LOG_TAG, "Error on update: " + th.toString());
            return false;
        }
    }

    public boolean isChecked() {
        return this.mSmart5gEnabled;
    }

    private Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(AUTHORITY);
        builder.appendPath(getTypeForPath());
        builder.appendPath(Integer.toString(this.mSubId));
        return builder.build();
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0072  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setProperties() {
        /*
            r5 = this;
            java.lang.String r0 = "Smart5gPreferenceController"
            java.lang.String r1 = "setProperties: +"
            com.motorola.settings.network.MotoMnsLog.logd(r0, r1)
            android.net.Uri r1 = r5.getUri()
            if (r1 == 0) goto L_0x007d
            android.content.Context r2 = r5.mContext
            android.content.ContentResolver r2 = r2.getContentResolver()
            r3 = 0
            android.database.Cursor r1 = r2.query(r1, r3, r3, r3)
            r2 = 0
            if (r1 == 0) goto L_0x0067
            boolean r3 = r1.moveToFirst()     // Catch:{ all -> 0x0076 }
            if (r3 == 0) goto L_0x0067
            java.lang.String r3 = "visible"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x0076 }
            r4 = 1
            if (r3 < 0) goto L_0x0035
            int r3 = r1.getInt(r3)     // Catch:{ all -> 0x0076 }
            if (r3 == 0) goto L_0x0032
            r3 = r4
            goto L_0x0033
        L_0x0032:
            r3 = r2
        L_0x0033:
            r5.mVisible = r3     // Catch:{ all -> 0x0076 }
        L_0x0035:
            java.lang.String r3 = "value"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x0076 }
            if (r3 < 0) goto L_0x0046
            int r3 = r1.getInt(r3)     // Catch:{ all -> 0x0076 }
            if (r3 == 0) goto L_0x0044
            r2 = r4
        L_0x0044:
            r5.mSmart5gEnabled = r2     // Catch:{ all -> 0x0076 }
        L_0x0046:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0076 }
            r2.<init>()     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = "visible:"
            r2.append(r3)     // Catch:{ all -> 0x0076 }
            boolean r3 = r5.mVisible     // Catch:{ all -> 0x0076 }
            r2.append(r3)     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = ", smart5gEnabled:"
            r2.append(r3)     // Catch:{ all -> 0x0076 }
            boolean r5 = r5.mSmart5gEnabled     // Catch:{ all -> 0x0076 }
            r2.append(r5)     // Catch:{ all -> 0x0076 }
            java.lang.String r5 = r2.toString()     // Catch:{ all -> 0x0076 }
            com.motorola.settings.network.MotoMnsLog.logd(r0, r5)     // Catch:{ all -> 0x0076 }
            goto L_0x0070
        L_0x0067:
            r5.mVisible = r2     // Catch:{ all -> 0x0076 }
            r5.mSmart5gEnabled = r2     // Catch:{ all -> 0x0076 }
            java.lang.String r5 = "Cannot find the provider"
            com.motorola.settings.network.MotoMnsLog.loge(r0, r5)     // Catch:{ all -> 0x0076 }
        L_0x0070:
            if (r1 == 0) goto L_0x007d
            r1.close()
            goto L_0x007d
        L_0x0076:
            r5 = move-exception
            if (r1 == 0) goto L_0x007c
            r1.close()
        L_0x007c:
            throw r5
        L_0x007d:
            java.lang.String r5 = "setProperties: -"
            com.motorola.settings.network.MotoMnsLog.logd(r0, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.network.Smart5gPreferenceController.setProperties():void");
    }
}
