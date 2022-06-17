package com.android.settingslib.utils;

import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class AppCloneUtils {
    public static boolean IS_PRC_PRODUCT = isPrcProduct();
    private static Method sGetUserId;
    private static Method sIsAppCloneUser;
    private static Class<?> sUserHandleClass;

    static {
        initUserHandle();
    }

    public static List<UserHandle> getUserProfileExceptClone(UserManager userManager) {
        if (!IS_PRC_PRODUCT) {
            return userManager.getUserProfiles();
        }
        return (List) userManager.getUserProfiles().stream().filter(AppCloneUtils$$ExternalSyntheticLambda0.INSTANCE).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getUserProfileExceptClone$0(UserHandle userHandle) {
        return !isAppCloneUser(userHandle.getIdentifier());
    }

    private static void initUserHandle() {
        try {
            Class<?> cls = Class.forName("android.os.UserHandle");
            sUserHandleClass = cls;
            Class cls2 = Integer.TYPE;
            sIsAppCloneUser = cls.getMethod("isAppCloneUser", new Class[]{cls2});
            sGetUserId = sUserHandleClass.getMethod("getUserId", new Class[]{cls2});
        } catch (Exception e) {
            Log.e("AppCloneUtils", "init user handle exception: " + e);
        }
    }

    private static boolean isPrcProduct() {
        try {
            return Class.forName("android.os.Build").getField("IS_PRC_PRODUCT").getBoolean((Object) null);
        } catch (Exception e) {
            Log.e("AppCloneUtils", "get IS_PRC_PRODUCT from Build exception: " + e);
            return false;
        }
    }

    private static boolean isAppCloneUser(int i) {
        Method method = sIsAppCloneUser;
        if (method == null) {
            return false;
        }
        try {
            return ((Boolean) method.invoke((Object) null, new Object[]{Integer.valueOf(i)})).booleanValue();
        } catch (Exception e) {
            Log.e("AppCloneUtils", "check isAppCloneUser from UserHandle exception: " + e);
            return false;
        }
    }
}
