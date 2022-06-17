package com.motorola.extensions.preference;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

public class DynamicPreferenceDataObserver {
    private final Context mContext;
    private DataObserver mObserver;
    private Uri mUri;

    public interface IAutoRefresh {
        void refresh();

        void setAutoRefresh(Uri uri, boolean z);
    }

    public DynamicPreferenceDataObserver(Context context) {
        this.mContext = context;
    }

    public void start(Uri uri, IAutoRefresh iAutoRefresh) {
        if (uri != null) {
            stop();
            this.mObserver = new DataObserver(new Handler(Looper.getMainLooper()), iAutoRefresh);
            this.mUri = uri;
            this.mContext.getContentResolver().registerContentObserver(uri, false, this.mObserver);
            return;
        }
        throw new IllegalArgumentException("uri can not be null");
    }

    public void stop() {
        if (this.mObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
            this.mObserver = null;
        }
    }

    public void onDetached() {
        if (this.mUri != null) {
            stop();
            Uri.Builder buildUpon = this.mUri.buildUpon();
            buildUpon.appendQueryParameter("listen", "false");
            this.mContext.getContentResolver().update(buildUpon.build(), new ContentValues(), (String) null, (String[]) null);
        }
    }

    private class DataObserver extends ContentObserver {
        IAutoRefresh mOnAutoRefresh;

        public DataObserver(Handler handler, IAutoRefresh iAutoRefresh) {
            super(handler);
            this.mOnAutoRefresh = iAutoRefresh;
        }

        public void onChange(boolean z) {
            IAutoRefresh iAutoRefresh = this.mOnAutoRefresh;
            if (iAutoRefresh != null) {
                iAutoRefresh.refresh();
            }
        }
    }
}
