package com.motorola.extensions.internal;

import android.content.ComponentName;
import android.content.Context;
import androidx.preference.Preference;

public class PreferenceLoader {
    public static Preference getPreferenceInstance(Context context, String str) {
        try {
            Object newInstance = loadClass(context, str).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            if (newInstance instanceof Preference) {
                return (Preference) newInstance;
            }
            throw new IllegalArgumentException(str + " is not an instance of Preference");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> loadClass(Context context, String str) throws ClassNotFoundException {
        String packageName = context.getPackageName();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        if (unflattenFromString != null) {
            packageName = unflattenFromString.getPackageName();
            str = unflattenFromString.getClassName();
        }
        try {
            if (!context.getPackageName().equals(packageName)) {
                context = context.createPackageContext(packageName, 3);
            }
            return Class.forName(str, true, context.getClassLoader());
        } catch (Exception e) {
            throw new ClassNotFoundException("failed to load class : ", e);
        }
    }
}
