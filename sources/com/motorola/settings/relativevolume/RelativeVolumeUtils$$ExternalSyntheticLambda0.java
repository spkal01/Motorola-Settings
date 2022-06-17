package com.motorola.settings.relativevolume;

import android.content.pm.ApplicationInfo;
import java.util.Comparator;

public final /* synthetic */ class RelativeVolumeUtils$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ RelativeVolumeUtils$$ExternalSyntheticLambda0 INSTANCE = new RelativeVolumeUtils$$ExternalSyntheticLambda0();

    private /* synthetic */ RelativeVolumeUtils$$ExternalSyntheticLambda0() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((ApplicationInfo) obj).packageName.compareTo(((ApplicationInfo) obj2).packageName);
    }
}
