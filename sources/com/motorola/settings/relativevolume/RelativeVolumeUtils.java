package com.motorola.settings.relativevolume;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C1983R$drawable;
import com.motorola.multivolume.AppVolumeState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeVolumeUtils {
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final boolean RV_FEATURE_ON = SystemProperties.getBoolean("ro.audio.relative_volume", false);

    public enum TouchType {
        START_TOUCH,
        ON_TOUCH,
        END_TOUCH,
        NO_TOUCH
    }

    public static double correctPercentage(double d) {
        if (d < 0.0d) {
            return 0.0d;
        }
        if (d > 1.0d) {
            return 1.0d;
        }
        return d;
    }

    public static List<AppVolumeState> loadIconForApps(Context context, List<AppVolumeState> list) {
        if (context == null) {
            if (DEBUG) {
                Log.e("RV-RelativeVolumeUtils", "context is null, return origin list");
            }
            return list;
        }
        ArrayList arrayList = new ArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            PackageManager packageManager = context.getPackageManager();
            for (AppVolumeState next : list) {
                int i = next.packageUid;
                next.icon = getIcon(context, getApplicationForUid(packageManager, i), packageManager, i);
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static List<AppVolumeState> getPlayingApps(List<AppVolumeState> list) {
        ArrayList arrayList = new ArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            for (AppVolumeState next : list) {
                if (next.shouldBeVisible || next.playing || next.foreground || next.foregroundSettings) {
                    arrayList.add(next);
                }
            }
        }
        return sortAppVolumeStates(arrayList);
    }

    private static Drawable getIcon(Context context, ApplicationInfo applicationInfo, PackageManager packageManager, int i) {
        Drawable drawable;
        if (applicationInfo == null) {
            return null;
        }
        try {
            Drawable loadIcon = applicationInfo.loadIcon(packageManager);
            if (loadIcon == null) {
                loadIcon = ContextCompat.getDrawable(context, applicationInfo.icon);
            }
            UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
            if (loadIcon != null) {
                drawable = packageManager.getUserBadgedIcon(loadIcon, userHandleForUid);
            } else {
                drawable = context.getDrawable(C1983R$drawable.ic_media_stream);
            }
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ApplicationInfo getApplicationForUid(PackageManager packageManager, int i) {
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        if (installedApplications == null) {
            return null;
        }
        List list = (List) installedApplications.stream().filter(new RelativeVolumeUtils$$ExternalSyntheticLambda1(i)).collect(Collectors.toList());
        if (list.size() == 0) {
            if (DEBUG) {
                Log.d("RV-RelativeVolumeUtils", "getApplicationForUid failed packageUid= " + i);
            }
            return null;
        } else if (list.size() > 1) {
            return (ApplicationInfo) list.stream().min(RelativeVolumeUtils$$ExternalSyntheticLambda0.INSTANCE).get();
        } else {
            return (ApplicationInfo) list.get(0);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApplicationForUid$0(int i, ApplicationInfo applicationInfo) {
        return applicationInfo.uid == i;
    }

    public static int getStreamMusicVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        if (audioManager != null) {
            return audioManager.getStreamVolume(3);
        }
        return 0;
    }

    public static int getMaxMusicVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(3);
        }
        return 15;
    }

    public static boolean isAppExist(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("RV-RelativeVolumeUtils", "Adaptive volume not install on this device");
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return true;
        }
        return false;
    }

    public static boolean isRelativeVolumeFeatureOn(Context context) {
        try {
            boolean isAppExist = isAppExist(context, "com.motorola.dynamicvolume");
            if (DEBUG) {
                Log.d("RV-RelativeVolumeUtils", "isDynamicVolumeAppExist: " + isAppExist);
            }
            if (!RV_FEATURE_ON || !isAppExist) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e("RV-RelativeVolumeUtils", "isRelativeVolumeFeatureOn: ", e);
            return false;
        }
    }

    public static List<AppVolumeState> sortAppVolumeStates(List<AppVolumeState> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<AppVolumeState>() {
                public int compare(AppVolumeState appVolumeState, AppVolumeState appVolumeState2) {
                    boolean z = appVolumeState.foreground;
                    if (z && !appVolumeState2.foreground) {
                        return -1;
                    }
                    if (!z && appVolumeState2.foreground) {
                        return 1;
                    }
                    boolean z2 = appVolumeState.foregroundSettings;
                    if (z2 && !appVolumeState2.foregroundSettings) {
                        return -1;
                    }
                    if (!z2 && appVolumeState2.foregroundSettings) {
                        return 1;
                    }
                    boolean z3 = appVolumeState.playing;
                    if (z3 && !appVolumeState2.playing) {
                        return -1;
                    }
                    if (!z3 && appVolumeState2.playing) {
                        return 1;
                    }
                    long j = appVolumeState.timeInMills;
                    long j2 = appVolumeState2.timeInMills;
                    if (j == j2) {
                        return 0;
                    }
                    if (j > j2) {
                        return -1;
                    }
                    return 1;
                }
            });
        }
        return list;
    }

    public static double getStoredPctFromUiPct(Context context, double d, int i) {
        return getStoredPctFromUiPct(getMaxMusicVolume(context), d, i);
    }

    public static double getStoredPctFromUiPct(int i, double d, int i2) {
        if (i2 == 0) {
            return 0.0d;
        }
        return ((double) ((int) Math.round(correctPercentage(d) * ((double) i)))) / (((double) i2) * 1.0d);
    }

    public static double getUiPctFromStoredPct(int i, double d, int i2) {
        if (i2 == 0) {
            return 0.0d;
        }
        double correctPercentage = correctPercentage(d);
        int round = (int) Math.round(((double) i2) * correctPercentage);
        if (correctPercentage > 0.0d && round == 0) {
            round = 1;
        }
        if (i == 0) {
            return 0.0d;
        }
        return ((double) round) / (((double) i) * 1.0d);
    }

    public static int getAppCount(List<AppVolumeState> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    public static int getMusicLevel(int i) {
        return i / 100;
    }

    public static String appVolumesToString(List<AppVolumeState> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (!CollectionUtils.isEmpty(list)) {
            stringBuffer.append("[");
            for (AppVolumeState next : list) {
                stringBuffer.append(" package: ");
                stringBuffer.append(next.packageName);
                stringBuffer.append(" appLevel: ");
                stringBuffer.append(next.appLevel);
                stringBuffer.append(" percentage: ");
                stringBuffer.append(next.percentage);
                stringBuffer.append(" playing: ");
                stringBuffer.append(next.playing);
                stringBuffer.append(" shouldBeVisible: ");
                stringBuffer.append(next.shouldBeVisible);
                stringBuffer.append(" storedPercentage: ");
                stringBuffer.append(next.storedPercentage);
                stringBuffer.append(" foreground: ");
                stringBuffer.append(next.foreground);
                stringBuffer.append(" foregroundSettings: ");
                stringBuffer.append(next.foregroundSettings);
                stringBuffer.append("\n");
            }
            stringBuffer.append("]");
        }
        return stringBuffer.toString();
    }
}
