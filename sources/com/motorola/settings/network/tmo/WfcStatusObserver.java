package com.motorola.settings.network.tmo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SubscriptionManager;
import androidx.preference.Preference;
import com.motorola.settings.network.MotoMnsLog;

public class WfcStatusObserver extends ContentObserver {
    private final String AUTHORITY = "com.motorola.carriersettingsext.dynamicprefprovider";
    private final String KEY_DYNAMIC_WIFI_CALLING_PREF = "dynamic_wifi_calling_pref";
    private final String KEY_DYNAMIC_WIFI_CALLING_PREF_SLOT1 = "dynamic_wifi_calling_pref_slot1";
    private final String PREFERENCE = "preference";
    private final String SCHEME = "content";
    private Context mContext;
    private String mKey;
    private Preference mListenerPref;
    private ContentResolver mResolver;
    private int mSubId;
    private Uri mUri;

    public WfcStatusObserver(Context context, int i) {
        super(new Handler());
        this.mContext = context;
        this.mResolver = context.getContentResolver();
        this.mSubId = i;
        if (SubscriptionManager.getSlotIndex(i) == 1) {
            this.mKey = "dynamic_wifi_calling_pref_slot1";
        } else {
            this.mKey = "dynamic_wifi_calling_pref";
        }
        this.mUri = getUri("content", "com.motorola.carriersettingsext.dynamicprefprovider", "preference", this.mKey);
    }

    private Uri getUri(String str, String str2, String str3, String str4) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(str);
        builder.authority(str2);
        builder.appendPath(str3);
        builder.appendPath(str4);
        return builder.build();
    }

    public void onChange(boolean z) {
        super.onChange(z);
        refreshPreference(this.mListenerPref);
    }

    public void register() {
        try {
            this.mResolver.registerContentObserver(this.mUri, false, this);
        } catch (SecurityException unused) {
            MotoMnsLog.loge("WfcStatusObserver", "cannot find the provider");
        }
    }

    public void unregister() {
        this.mResolver.unregisterContentObserver(this);
    }

    public void refreshPreference(Preference preference) {
        this.mListenerPref = preference;
        new PrefSummaryQueryHandler(this.mContext.getContentResolver(), preference).queryAndUpdatePrefSummary(this.mUri);
    }
}
