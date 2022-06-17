package com.motorola.settings.relativevolume;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;

public class RelativeVolumeSlice implements CustomSliceable {
    private static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private Context mContext;
    private RelativeVolumeWorker mWorker;

    public Intent getIntent() {
        return null;
    }

    public RelativeVolumeSlice(Context context) {
        this.mContext = context;
    }

    public Slice getSlice() {
        if (!isVisible()) {
            return null;
        }
        RelativeVolumeWorker worker = getWorker();
        ListBuilder accentColor = new ListBuilder(this.mContext, getUri(), -1).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        if (worker == null) {
            if (DEBUG) {
                Log.e("RV-RelativeVolumeSlice", "Unable to get the slice worker.");
            }
            accentColor.setIsError(true);
            return accentColor.build();
        }
        int appCount = RelativeVolumeUtils.getAppCount(worker.getResults());
        boolean z = DEBUG;
        if (z) {
            Log.d("RV-RelativeVolumeSlice", "getSlice called apCount: " + appCount);
        }
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C1983R$drawable.ic_media_stream);
        CharSequence text = this.mContext.getText(C1992R$string.soundsettings_relativevolume_title);
        SliceAction createDeeplink = SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, getSliceIntent(), 67108864), createWithResource, 0, text);
        if (appCount == 0) {
            if (z) {
                Log.d("RV-RelativeVolumeSlice", "getSlice no app volume found");
            }
            accentColor.setIsError(true);
            return accentColor.build();
        }
        accentColor.addRow(new ListBuilder.RowBuilder().setTitle(text).setSubtitle(buildAppVolumeSummary(appCount)).setTitleItem(createEmptyIcon(), 0).setPrimaryAction(createDeeplink));
        return accentColor.build();
    }

    private CharSequence buildAppVolumeSummary(int i) {
        return this.mContext.getResources().getQuantityString(C1990R$plurals.soundsettings_relativevolume_summary, i, new Object[]{Integer.valueOf(i)});
    }

    private Intent getSliceIntent() {
        return new Intent().setAction("android.settings.SOUND_SETTINGS").addFlags(268435456);
    }

    private IconCompat createEmptyIcon() {
        return IconCompat.createWithBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
    }

    public Uri getUri() {
        return CustomSliceRegistry.VOLUME_RELATIVE_URI;
    }

    public Class getBackgroundWorkerClass() {
        return RelativeVolumeWorker.class;
    }

    private RelativeVolumeWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (RelativeVolumeWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }

    private boolean isVisible() {
        return RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext.getApplicationContext());
    }
}
