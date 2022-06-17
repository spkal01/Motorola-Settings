package com.motorola.settings.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settingslib.applications.ApplicationsState;
import java.util.List;

public final class CloneAppUtils {
    public static void addCloneUserHandles(UserManager userManager, List<UserHandle> list) {
        if (Build.IS_PRC_PRODUCT && userManager != null && list != null) {
            for (UserInfo userInfo : userManager.getAppCloneProfiles()) {
                list.add(UserHandle.of(userInfo.id));
            }
        }
    }

    public static boolean isSystemAppOrUninstalledInCloneUser(ApplicationInfo applicationInfo) {
        if (!Build.IS_PRC_PRODUCT || applicationInfo == null || !UserHandle.isAppCloneUser(UserHandle.getUserId(applicationInfo.uid))) {
            return false;
        }
        boolean isSystemApp = isSystemApp(applicationInfo);
        boolean z = (applicationInfo.flags & 8388608) == 0;
        if (isSystemApp || z) {
            return true;
        }
        return false;
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo == null || (applicationInfo.flags & 129) == 0) ? false : true;
    }

    public static void collectCloneApps(UserManager userManager, PackageManager packageManager, List<PackageInfo> list) {
        if (Build.IS_PRC_PRODUCT) {
            for (UserInfo userInfo : userManager.getAppCloneProfiles()) {
                list.addAll(packageManager.getInstalledPackagesAsUser(1, userInfo.id));
            }
        }
    }

    public static boolean isCloneAppByUid(int i) {
        if (!Build.IS_PRC_PRODUCT) {
            return false;
        }
        return UserHandle.isAppCloneUser(UserHandle.getUserId(i));
    }

    public static boolean isCloneAppByAppEntry(ApplicationsState.AppEntry appEntry) {
        ApplicationInfo applicationInfo;
        if (appEntry == null || (applicationInfo = appEntry.info) == null) {
            return false;
        }
        return isCloneAppByUid(applicationInfo.uid);
    }
}
