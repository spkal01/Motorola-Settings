package com.motorola.extensions.preference;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;
import com.motorola.extensions.preference.DynamicPreferenceDataObserver;

public class DynamicTwoStatePreferenceDelegator implements DynamicPreferenceDataObserver.IAutoRefresh, Preference.OnPreferenceChangeListener {
    private static final String TAG = "DynamicTwoStatePreferenceDelegator";
    private boolean mAutoRefresh;
    Context mContext;
    private boolean mInterceptor;
    private Intent mInterceptorIntent;
    private int mMaxLines = 1;
    private Boolean mNewValue;
    private DynamicPreferenceDataObserver mObserver;
    private boolean mOffInterceptor;
    private boolean mOnInterceptor;
    private Uri mPreferenceUri;
    private TwoStatePreference mTwoStatePreference;

    public interface DelegatorHelper {
        DynamicTwoStatePreferenceDelegator getDelegator();
    }

    public DynamicTwoStatePreferenceDelegator(Context context, TwoStatePreference twoStatePreference) {
        this.mTwoStatePreference = twoStatePreference;
        this.mContext = context;
    }

    public void setPreferenceDataUri(Uri uri) {
        this.mPreferenceUri = uri;
    }

    public void setIntent(Intent intent) {
        this.mInterceptorIntent = intent;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        checkObserver();
    }

    public void onDetached() {
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

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
        if (this.mMaxLines > 1 && textView != null) {
            textView.setSingleLine(false);
            textView.setMaxLines(this.mMaxLines);
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
                        int columnIndex = query.getColumnIndex("enabled");
                        boolean z = true;
                        if (columnIndex >= 0) {
                            this.mTwoStatePreference.setEnabled(query.getInt(columnIndex) != 0);
                        }
                        int columnIndex2 = query.getColumnIndex("value");
                        if (columnIndex2 >= 0) {
                            int i = query.getInt(columnIndex2);
                            TwoStatePreference twoStatePreference = this.mTwoStatePreference;
                            if (i == 0) {
                                z = false;
                            }
                            twoStatePreference.setChecked(z);
                        }
                        int columnIndex3 = query.getColumnIndex("summary");
                        if (columnIndex3 >= 0 && (string = query.getString(columnIndex3)) != null) {
                            this.mTwoStatePreference.setSummary((CharSequence) string);
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

    public void setOnInterceptor(boolean z) {
        this.mOnInterceptor = z;
    }

    public void setOffInterceptor(boolean z) {
        this.mOffInterceptor = z;
    }

    public void setInterceptor(boolean z) {
        this.mInterceptor = z;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mNewValue = (Boolean) obj;
        if (getLaunchIntent() == null) {
            return persistNewValue();
        }
        this.mTwoStatePreference.getPreferenceManager().showDialog(this.mTwoStatePreference);
        return false;
    }

    private boolean persistNewValue() {
        try {
            if (this.mPreferenceUri == null) {
                return true;
            }
            ContentValues contentValues = new ContentValues();
            ContentResolver contentResolver = getContext().getContentResolver();
            contentValues.put("value", this.mNewValue);
            if (contentResolver.update(this.mPreferenceUri, contentValues, (String) null, (String[]) null) > 0) {
                return true;
            }
            return false;
        } catch (Throwable th) {
            Log.w(TAG, "Error on update", th);
            return false;
        }
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i != getRequestCode()) {
            return false;
        }
        if (i2 != -1 || !persistNewValue()) {
            return true;
        }
        this.mTwoStatePreference.setChecked(this.mNewValue.booleanValue());
        return true;
    }

    public void setMaxLines(int i) {
        this.mMaxLines = i;
    }

    private Context getContext() {
        return this.mContext;
    }

    private String getKey() {
        return this.mTwoStatePreference.getKey();
    }

    public int getRequestCode() {
        return Math.abs(getKey().hashCode());
    }

    public Intent getLaunchIntent() {
        if (this.mInterceptorIntent == null || (!this.mInterceptor && ((!this.mNewValue.booleanValue() || !this.mOnInterceptor) && (this.mNewValue.booleanValue() || !this.mOffInterceptor)))) {
            return null;
        }
        Intent intent = new Intent(this.mInterceptorIntent);
        intent.putExtra("value", this.mNewValue);
        return intent;
    }
}
