package com.motorola.settings.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;
import com.motorola.internal.app.MotoDesktopManager;

public class MotoReadyForUtils {
    public static final long FUNCTION_READYFOR = getLongUsbFunctionByReflection();
    public static final String USB_FUNCTION_READYFOR = getUsbFunctionByReflection();

    private static String getUsbFunctionByReflection() {
        try {
            return (String) UsbManager.class.getDeclaredField("USB_FUNCTION_READYFOR").get((Object) null);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static long getLongUsbFunctionByReflection() {
        try {
            return UsbManager.class.getDeclaredField("FUNCTION_READYFOR").getLong((Object) null);
        } catch (Throwable unused) {
            return 2147483648L;
        }
    }

    public static boolean isReadyForFeatureSupported(Context context) {
        return MotoDesktopManager.isReadyForAvailable(context) && !TextUtils.isEmpty(USB_FUNCTION_READYFOR) && FUNCTION_READYFOR != 2147483648L;
    }

    public static void startReadyForConnect(Context context) {
        try {
            Intent intent = new Intent("com.motorola.mobiledesktop.action.EXTERNAL_SETUP");
            intent.setPackage("com.motorola.mobiledesktop");
            intent.addFlags(268435456);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("MotoReadyForUtils", e.getMessage());
        }
    }
}
