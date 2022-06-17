package com.motorola.settings.utils;

import android.content.Context;
import android.os.UserManager;

public class PrivacySpaceUtils {
    public static boolean isPrivacySpaceUser(Context context, int i) {
        int privacySpaceUserId = ((UserManager) context.getSystemService("user")).getPrivacySpaceUserId();
        return privacySpaceUserId > 0 && i == privacySpaceUserId;
    }
}
