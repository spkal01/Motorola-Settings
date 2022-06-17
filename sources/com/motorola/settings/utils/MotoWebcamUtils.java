package com.motorola.settings.utils;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;

public class MotoWebcamUtils {
    public static final long FUNCTION_WEBCAM = getLongUsbFunctionByReflection();
    public static final String USB_FUNCTION_WEBCAM = getUsbFunctionByReflection();

    private static String getUsbFunctionByReflection() {
        try {
            return (String) UsbManager.class.getDeclaredField("USB_FUNCTION_WEBCAM").get((Object) null);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static long getLongUsbFunctionByReflection() {
        try {
            return UsbManager.class.getDeclaredField("FUNCTION_WEBCAM").getLong((Object) null);
        } catch (Throwable unused) {
            return 2147483648L;
        }
    }

    public static boolean isWebcamFeatureSupported(Context context) {
        return context.getResources().getBoolean(C1980R$bool.config_moto_usb_webcam_mode) && !TextUtils.isEmpty(USB_FUNCTION_WEBCAM) && FUNCTION_WEBCAM != 2147483648L;
    }
}
