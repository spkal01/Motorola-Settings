package com.motorola.settings.utils;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;

public class MotoCdromUtils {
    public static final long FUNCTION_CDROM = getLongUsbFunctionCdromByReflection();
    public static final String USB_FUNCTION_CDROM = getUsbFunctionCdromByReflection();

    private static String getUsbFunctionCdromByReflection() {
        try {
            return (String) UsbManager.class.getDeclaredField("USB_FUNCTION_CDROM").get((Object) null);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static long getLongUsbFunctionCdromByReflection() {
        try {
            return UsbManager.class.getDeclaredField("FUNCTION_CDROM").getLong((Object) null);
        } catch (Throwable unused) {
            return 2147483648L;
        }
    }

    public static boolean isCdromFeatureSupported(Context context) {
        return context.getResources().getBoolean(C1980R$bool.config_moto_usb_cdrom_mode) && !TextUtils.isEmpty(USB_FUNCTION_CDROM) && FUNCTION_CDROM != 2147483648L;
    }
}
