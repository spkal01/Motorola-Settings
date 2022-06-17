package com.motorola.settings.relativevolume;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.settings.relativevolume.AppVolumeAdapter;
import java.util.List;

public class RelativeVolumePreference extends Preference {
    private static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private static final String TAG = ("RV-" + RelativeVolumePreference.class.getSimpleName());
    private AppVolumeAdapter mAdapter;
    private AppVolumeCallback mAppVolumeCallback;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mExpanded = true;
    private boolean mIsMasterSeekBarTouching = false;

    public interface AppVolumeCallback {
        void onAppValueChanged(int i, boolean z);

        void onSafeVolumeAction(int i);

        void onUserAttemptChange(long j);
    }

    public RelativeVolumePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public RelativeVolumePreference(Context context) {
        super(context, (AttributeSet) null);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setLayoutResource(C1987R$layout.preference_relative_volume);
        this.mAdapter = new AppVolumeAdapter(this.mContext, (List<AppVolumeState>) null);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(C1985R$id.expand_indicator);
        final RecyclerView recyclerView = (RecyclerView) preferenceViewHolder.itemView.findViewById(C1985R$id.data);
        ((RelativeLayout) preferenceViewHolder.itemView.findViewById(C1985R$id.header)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (RelativeVolumePreference.this.mExpanded) {
                    recyclerView.setVisibility(8);
                    imageView.setImageResource(C1983R$drawable.ic_expand_less);
                    boolean unused = RelativeVolumePreference.this.mExpanded = false;
                    return;
                }
                recyclerView.setVisibility(0);
                imageView.setImageResource(C1983R$drawable.ic_expand);
                boolean unused2 = RelativeVolumePreference.this.mExpanded = true;
            }
        });
        ((TextView) preferenceViewHolder.itemView.findViewById(C1985R$id.title)).setText(C1992R$string.soundsettings_relativevolume_title);
        recyclerView.setAdapter(this.mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
    }

    public List<AppVolumeState> getData() {
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            return appVolumeAdapter.getData();
        }
        return null;
    }

    public void updateAllData(List<AppVolumeState> list) {
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            appVolumeAdapter.setData(list);
            if (DEBUG) {
                Log.d(TAG, "updateData  ");
            }
        } else if (DEBUG) {
            Log.d(TAG, "updateData error, adapter not init");
        }
    }

    public void updateWhenMediaVolumeChange(int i) {
        if (DEBUG) {
            String str = TAG;
            Log.d(str, "updateWhenMediaVolumeChange to: " + i);
        }
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            appVolumeAdapter.onMusicVolumeChanged(i);
        }
    }

    public boolean isUserTracking() {
        return this.mIsMasterSeekBarTouching;
    }

    public void updateMasterSeekBarState(boolean z) {
        this.mIsMasterSeekBarTouching = z;
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            appVolumeAdapter.updateUserAttempt(SystemClock.uptimeMillis());
        }
    }

    public long getUserAttemptMills() {
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            return appVolumeAdapter.getUserAttemptMills();
        }
        return 0;
    }

    public void setAppVolumeCallBack(AppVolumeCallback appVolumeCallback) {
        this.mAppVolumeCallback = appVolumeCallback;
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            appVolumeAdapter.setCallBack(appVolumeCallback);
        }
    }

    public void updateMusicVolume(int i) {
        AppVolumeCallback appVolumeCallback = this.mAppVolumeCallback;
        if (appVolumeCallback != null) {
            appVolumeCallback.onAppValueChanged(i, true);
            this.mAppVolumeCallback.onUserAttemptChange(SystemClock.uptimeMillis());
        }
    }

    public void onSafeVolumeAction(int i) {
        AppVolumeCallback appVolumeCallback = this.mAppVolumeCallback;
        if (appVolumeCallback != null) {
            appVolumeCallback.onSafeVolumeAction(i);
        }
    }

    public void setVolumeStateCallback(AppVolumeAdapter.VolumeStateCallback volumeStateCallback) {
        AppVolumeAdapter appVolumeAdapter = this.mAdapter;
        if (appVolumeAdapter != null) {
            appVolumeAdapter.setVolumeStateCallback(volumeStateCallback);
        }
    }
}
