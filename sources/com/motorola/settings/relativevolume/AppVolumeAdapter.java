package com.motorola.settings.relativevolume;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1987R$layout;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.settings.relativevolume.RelativeVolumePreference;
import com.motorola.settings.relativevolume.RelativeVolumeUtils;
import java.util.Iterator;
import java.util.List;

public class AppVolumeAdapter extends RecyclerView.Adapter<AppVolumeHolder> {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    /* access modifiers changed from: private */
    public RelativeVolumePreference.AppVolumeCallback mAppVolumeCallback;
    private List<AppVolumeState> mAppVolumeStates;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mMaxMusicVolume = 15;
    /* access modifiers changed from: private */
    public int mMinMusicVolume = 0;
    private long mUserAttemptMills;
    /* access modifiers changed from: private */
    public VolumeStateCallback mVolumeStateCallback;

    public interface VolumeStateCallback {
        void onAppVolumeChanged(String str, int i, int i2, double d, int i3);

        void onMusicVolumeChanged(int i);

        void onTrackingStateChanged(boolean z);
    }

    private double getNewRatio(int i, int i2) {
        if (i2 == 0) {
            return 0.0d;
        }
        if (i >= i2) {
            return 1.0d;
        }
        return ((double) i) / ((double) i2);
    }

    public AppVolumeAdapter(Context context, List<AppVolumeState> list) {
        this.mContext = context;
        this.mAppVolumeStates = list;
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        this.mAudioManager = audioManager;
        if (audioManager != null) {
            this.mMaxMusicVolume = audioManager.getStreamMaxVolume(3);
            this.mMinMusicVolume = this.mAudioManager.getStreamMinVolumeInt(3);
        }
    }

    public void setData(List<AppVolumeState> list) {
        this.mAppVolumeStates = updateAllLastPercentage(list);
        notifyDataSetChanged();
    }

    public List<AppVolumeState> getData() {
        return this.mAppVolumeStates;
    }

    public AppVolumeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        return new AppVolumeHolder(LayoutInflater.from(context).inflate(i, viewGroup, false), context);
    }

    public void onBindViewHolder(final AppVolumeHolder appVolumeHolder, int i) {
        final AppVolumeState appVolumeState = this.mAppVolumeStates.get(i);
        if (appVolumeState != null) {
            MySeekBarChangeListener mySeekBarChangeListener = new MySeekBarChangeListener(this.mContext, appVolumeState);
            Drawable drawable = appVolumeState.icon;
            if (drawable != null) {
                if (DEBUG) {
                    Log.d("RV-AppVolumeAdapter", "bindView drawable app: " + appVolumeState.packageName);
                }
                appVolumeHolder.mIconView.setImageDrawable(drawable);
            }
            appVolumeHolder.mIconView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    int i;
                    int streamMusicVolume = RelativeVolumeUtils.getStreamMusicVolume(AppVolumeAdapter.this.mContext.getApplicationContext());
                    if (streamMusicVolume != 0) {
                        int progress = appVolumeHolder.mSeekBar.getProgress();
                        boolean z = progress <= 0;
                        if (z) {
                            i = Math.min(streamMusicVolume, appVolumeState.lastSetLevel);
                        } else {
                            i = AppVolumeAdapter.this.mMinMusicVolume;
                        }
                        AppVolumeAdapter.this.onIconClick(appVolumeState, streamMusicVolume, progress, z, i);
                        appVolumeHolder.mSeekBar.setProgress(i);
                    } else if (AppVolumeAdapter.DEBUG) {
                        Log.d("RV-AppVolumeAdapter", "unable to mute/unmute app when music volume is 0");
                    }
                }
            });
            appVolumeHolder.mSeekBar.setMax(this.mMaxMusicVolume);
            appVolumeHolder.mSeekBar.setMin(this.mMinMusicVolume);
            boolean z = this.mAudioManager.getStreamVolume(3) > 0;
            appVolumeHolder.mSeekBar.setProgress(appVolumeState.appLevel);
            if (DEBUG) {
                Log.d("RV-AppVolumeAdapter", "bindView setProgress: " + appVolumeState.appLevel + " for app: " + appVolumeState.packageName + " lastSetLevel: " + appVolumeState.lastSetLevel + " isEnable: " + z);
            }
            appVolumeHolder.mSeekBar.setEnabled(z);
            appVolumeHolder.mSeekBar.setOnSeekBarChangeListener(mySeekBarChangeListener);
        } else if (DEBUG) {
            Log.d("RV-AppVolumeAdapter", "null appvolume state found ");
        }
    }

    /* access modifiers changed from: private */
    public void onIconClick(AppVolumeState appVolumeState, int i, int i2, boolean z, int i3) {
        int i4;
        this.mUserAttemptMills = 0;
        RelativeVolumePreference.AppVolumeCallback appVolumeCallback = this.mAppVolumeCallback;
        if (appVolumeCallback != null) {
            appVolumeCallback.onUserAttemptChange(0);
        }
        boolean needUpdateMaster = needUpdateMaster(appVolumeState.packageUid, i2, i3, i, appVolumeState.ratio);
        int targetMasterVolume = getTargetMasterVolume(appVolumeState.packageUid, needUpdateMaster, i3, i);
        if (z) {
            i4 = i;
        } else {
            i4 = getMaxAppLevel();
        }
        updateAppVolumeSettings(appVolumeState, i2, i3, RelativeVolumeUtils.TouchType.START_TOUCH.ordinal(), i4, targetMasterVolume, needUpdateMaster);
        if (z) {
            if (DEBUG) {
                Log.d("RV-AppVolumeAdapter", "restore for app: " + appVolumeState.packageName + " to lastSetLevel: " + appVolumeState.lastSetLevel);
            }
        } else if (DEBUG) {
            Log.d("RV-AppVolumeAdapter", "press mute for app: " + appVolumeState.packageName + " lastSetLevel: " + appVolumeState.lastSetLevel);
        }
    }

    public int getItemViewType(int i) {
        return C1987R$layout.preference_volume_slider;
    }

    public int getItemCount() {
        if (!CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            return this.mAppVolumeStates.size();
        }
        return 0;
    }

    public static class AppVolumeHolder extends RecyclerView.ViewHolder {
        private Context mCtx;
        public ImageView mIconView;
        public SeekBar mSeekBar;
        private TextView mTitleView;
        private LinearLayout mWidgetFrame;

        public AppVolumeHolder(View view, Context context) {
            super(view);
            Context context2;
            this.mCtx = context;
            this.mIconView = (ImageView) view.findViewById(16908294);
            this.mSeekBar = (SeekBar) view.findViewById(16909418);
            TextView textView = (TextView) view.findViewById(16908310);
            this.mTitleView = textView;
            textView.setVisibility(8);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(16908312);
            this.mWidgetFrame = linearLayout;
            linearLayout.setVisibility(8);
            if (this.mIconView != null && (context2 = this.mCtx) != null) {
                this.mIconView.setMaxHeight((int) context2.getResources().getDimension(C1982R$dimen.app_volume_item_icon_height));
                this.mIconView.setMaxWidth((int) this.mCtx.getResources().getDimension(C1982R$dimen.app_volume_item_icon_width));
            }
        }
    }

    public List<AppVolumeState> updateAllLastPercentage(List<AppVolumeState> list) {
        if (!CollectionUtils.isEmpty(list) && !CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            for (AppVolumeState next : list) {
                String str = next.packageName;
                Iterator<AppVolumeState> it = this.mAppVolumeStates.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    AppVolumeState next2 = it.next();
                    if (str.equals(next2.packageName)) {
                        int i = next2.appLevel;
                        if (i > 0) {
                            next.lastSetLevel = i;
                        } else {
                            int i2 = next2.lastSetLevel;
                            if (i2 > 0) {
                                next.lastSetLevel = i2;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public void updateAppVolumeSettings(AppVolumeState appVolumeState, int i, int i2, int i3, int i4, int i5, boolean z) {
        AppVolumeState appVolumeState2 = appVolumeState;
        int i6 = i2;
        int i7 = i5;
        if (i != i6) {
            if (DEBUG) {
                Log.d("RV-AppVolumeAdapter", "updateAppVolumeSettings app: " + appVolumeState2.packageName + " lastProgress: " + i + " progress: " + i2 + " uid: " + appVolumeState2.packageUid + " ratio: " + appVolumeState2.ratio + " fromMasterVolume: " + i4 + " targetMasterVolume: " + i7);
            }
            RelativeVolumePreference.AppVolumeCallback appVolumeCallback = this.mAppVolumeCallback;
            if (appVolumeCallback != null) {
                appVolumeCallback.onAppValueChanged(i7, z);
            }
            double d = ((double) i6) / (((double) this.mMaxMusicVolume) * 1.0d);
            updateLevelAndRatio(appVolumeState2.packageName, i2, i7);
            VolumeStateCallback volumeStateCallback = this.mVolumeStateCallback;
            if (volumeStateCallback != null) {
                volumeStateCallback.onAppVolumeChanged(appVolumeState2.packageName, appVolumeState2.packageUid, i2, d, i3);
            }
        }
    }

    private void updateLevelAndRatio(String str, int i, int i2) {
        if (str == null || i <= 0) {
            if (DEBUG) {
                Log.d("RV-AppVolumeAdapter", "do not update ratio when app muted: " + str);
            }
        } else if (!CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            for (AppVolumeState next : this.mAppVolumeStates) {
                if (str.equals(next.packageName)) {
                    next.appLevel = i;
                    next.storedPercentage = RelativeVolumeUtils.getStoredPctFromUiPct(this.mContext, ((double) i) / (((double) this.mMaxMusicVolume) * 1.0d), i2);
                    if (DEBUG) {
                        Log.d("RV-AppVolumeAdapter", "update stored Percentage for: " + str + " stored Percentage: " + next.storedPercentage);
                    }
                    if (i > 0) {
                        next.lastSetLevel = i;
                    }
                } else {
                    next.storedPercentage = RelativeVolumeUtils.getStoredPctFromUiPct(this.mContext, next.percentage, i2);
                }
                next.ratio = getNewRatio(next.appLevel, i2);
                if (DEBUG) {
                    Log.d("RV-AppVolumeAdapter", "update ratio for: " + next.packageName + " ratio: " + next.ratio + " stored Percentage: " + next.storedPercentage);
                }
            }
        }
    }

    private int getMaxAppLevel() {
        int i = 0;
        if (!CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            for (AppVolumeState appVolumeState : this.mAppVolumeStates) {
                int i2 = appVolumeState.appLevel;
                if (i2 > i) {
                    i = i2;
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    public int getTargetMasterVolume(int i, boolean z, int i2, int i3) {
        int i4;
        if (!z || CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            return i3;
        }
        int i5 = i2;
        for (AppVolumeState next : this.mAppVolumeStates) {
            if (i != next.packageUid && (i4 = next.appLevel) > i2) {
                i5 = i4;
            }
        }
        return i5;
    }

    /* access modifiers changed from: private */
    public boolean needUpdateMaster(int i, int i2, int i3, int i4, double d) {
        if (DEBUG) {
            Log.d("RV-AppVolumeAdapter", "needUpdateMaster uid: " + i + " lastSetLevel: " + i2 + " appLevel: " + i3 + " masterVolume: " + i4 + " ratio: " + d);
        }
        if (i3 > i4) {
            return true;
        }
        if (i2 == -1 || d < 1.0d || CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            return false;
        }
        for (AppVolumeState next : this.mAppVolumeStates) {
            if (i != next.packageUid && next.appLevel > i2) {
                return false;
            }
        }
        return true;
    }

    public class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int mLastMasterVolume = -1;
        private int mLastProgress = -1;
        private AppVolumeState mMode;
        private RelativeVolumeUtils.TouchType mTouchType;
        private boolean mTracking;

        public MySeekBarChangeListener(Context context, AppVolumeState appVolumeState) {
            this.mMode = appVolumeState;
            this.mLastProgress = appVolumeState.appLevel;
            this.mTouchType = RelativeVolumeUtils.TouchType.END_TOUCH;
            this.mLastMasterVolume = RelativeVolumeUtils.getStreamMusicVolume(context.getApplicationContext());
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                if (this.mTouchType == RelativeVolumeUtils.TouchType.START_TOUCH) {
                    this.mTouchType = RelativeVolumeUtils.TouchType.ON_TOUCH;
                }
                onAppProgressChange(i);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            this.mTracking = true;
            if (AppVolumeAdapter.this.mVolumeStateCallback != null) {
                AppVolumeAdapter.this.mVolumeStateCallback.onTrackingStateChanged(this.mTracking);
            }
            this.mTouchType = RelativeVolumeUtils.TouchType.START_TOUCH;
        }

        private void onAppProgressChange(int i) {
            int streamMusicVolume = RelativeVolumeUtils.getStreamMusicVolume(AppVolumeAdapter.this.mContext.getApplicationContext());
            AppVolumeAdapter appVolumeAdapter = AppVolumeAdapter.this;
            AppVolumeState appVolumeState = this.mMode;
            boolean access$500 = appVolumeAdapter.needUpdateMaster(appVolumeState.packageUid, this.mLastProgress, i, streamMusicVolume, appVolumeState.ratio);
            int access$600 = AppVolumeAdapter.this.getTargetMasterVolume(this.mMode.packageUid, access$500, i, streamMusicVolume);
            if (this.mLastMasterVolume == -1) {
                this.mLastMasterVolume = streamMusicVolume;
            }
            AppVolumeAdapter.this.updateAppVolumeSettings(this.mMode, this.mLastProgress, i, this.mTouchType.ordinal(), this.mLastMasterVolume, access$600, access$500);
            this.mLastProgress = i;
            this.mLastMasterVolume = access$600;
            long uptimeMillis = SystemClock.uptimeMillis();
            AppVolumeAdapter.this.updateUserAttempt(uptimeMillis);
            if (AppVolumeAdapter.this.mAppVolumeCallback != null) {
                AppVolumeAdapter.this.mAppVolumeCallback.onUserAttemptChange(uptimeMillis);
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            this.mTracking = false;
            int progress = seekBar.getProgress();
            this.mTouchType = RelativeVolumeUtils.TouchType.END_TOUCH;
            onAppProgressChange(progress);
            if (AppVolumeAdapter.this.mVolumeStateCallback != null) {
                AppVolumeAdapter.this.mVolumeStateCallback.onTrackingStateChanged(this.mTracking);
            }
        }
    }

    public long getUserAttemptMills() {
        return this.mUserAttemptMills;
    }

    public void updateUserAttempt(long j) {
        this.mUserAttemptMills = j;
    }

    public void setCallBack(RelativeVolumePreference.AppVolumeCallback appVolumeCallback) {
        this.mAppVolumeCallback = appVolumeCallback;
    }

    public void setVolumeStateCallback(VolumeStateCallback volumeStateCallback) {
        this.mVolumeStateCallback = volumeStateCallback;
    }

    public void updateWhenMediaVolumeChange(int i) {
        if (DEBUG) {
            Log.d("RV-AppVolumeAdapter", "updateWhenMediaVolumeChange to: " + i);
        }
        if (!CollectionUtils.isEmpty(this.mAppVolumeStates)) {
            for (AppVolumeState next : this.mAppVolumeStates) {
                double uiPctFromStoredPct = RelativeVolumeUtils.getUiPctFromStoredPct(this.mMaxMusicVolume, next.storedPercentage, i);
                if (next.storedPercentage == 0.0d) {
                    uiPctFromStoredPct = 0.0d;
                }
                if (DEBUG) {
                    Log.d("RV-AppVolumeAdapter", "updateWhenMediaVolumeChange for app: " + next.packageName + " storedPercentage: " + next.storedPercentage + " uipercentage: " + next.percentage + " newPercentage: " + uiPctFromStoredPct);
                }
                next.percentage = uiPctFromStoredPct;
                int round = (int) Math.round(((double) this.mMaxMusicVolume) * uiPctFromStoredPct);
                next.appLevel = round;
                if (round > 0) {
                    next.lastSetLevel = round;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void onMusicVolumeChanged(int i) {
        updateWhenMediaVolumeChange(i);
        VolumeStateCallback volumeStateCallback = this.mVolumeStateCallback;
        if (volumeStateCallback != null) {
            volumeStateCallback.onMusicVolumeChanged(i);
        }
    }
}
