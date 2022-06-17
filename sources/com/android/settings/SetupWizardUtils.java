package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sysprop.SetupWizardProperties;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.Arrays;

public class SetupWizardUtils {
    public static String getThemeString(Intent intent) {
        String stringExtra = intent.getStringExtra("theme");
        return stringExtra == null ? (String) SetupWizardProperties.theme().orElse("") : stringExtra;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0032, code lost:
        if (r0.equals("glif_light") == false) goto L_0x002c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007c, code lost:
        if (r0.equals("glif_light") == false) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00cf, code lost:
        if (r0.equals("glif_light") == false) goto L_0x00c9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getTheme(android.content.Context r14, android.content.Intent r15) {
        /*
            java.lang.String r0 = getThemeString(r15)
            if (r0 == 0) goto L_0x010b
            boolean r15 = com.google.android.setupcompat.util.WizardManagerHelper.isAnySetupWizard(r15)
            r1 = 5
            java.lang.String r2 = "glif_light"
            r3 = 4
            java.lang.String r4 = "glif_v3"
            r5 = 3
            java.lang.String r6 = "glif_v2"
            r7 = 2
            java.lang.String r8 = "glif"
            r9 = 1
            java.lang.String r10 = "glif_v3_light"
            r11 = 0
            java.lang.String r12 = "glif_v2_light"
            r13 = -1
            if (r15 == 0) goto L_0x00c2
            boolean r14 = com.google.android.setupdesign.util.ThemeHelper.isSetupWizardDayNightEnabled(r14)
            if (r14 == 0) goto L_0x006f
            int r14 = r0.hashCode()
            switch(r14) {
                case -2128555920: goto L_0x0059;
                case -1241052239: goto L_0x0050;
                case 3175618: goto L_0x0047;
                case 115650329: goto L_0x003e;
                case 115650330: goto L_0x0035;
                case 767685465: goto L_0x002e;
                default: goto L_0x002c;
            }
        L_0x002c:
            r1 = r13
            goto L_0x0061
        L_0x002e:
            boolean r14 = r0.equals(r2)
            if (r14 != 0) goto L_0x0061
            goto L_0x002c
        L_0x0035:
            boolean r14 = r0.equals(r4)
            if (r14 != 0) goto L_0x003c
            goto L_0x002c
        L_0x003c:
            r1 = r3
            goto L_0x0061
        L_0x003e:
            boolean r14 = r0.equals(r6)
            if (r14 != 0) goto L_0x0045
            goto L_0x002c
        L_0x0045:
            r1 = r5
            goto L_0x0061
        L_0x0047:
            boolean r14 = r0.equals(r8)
            if (r14 != 0) goto L_0x004e
            goto L_0x002c
        L_0x004e:
            r1 = r7
            goto L_0x0061
        L_0x0050:
            boolean r14 = r0.equals(r10)
            if (r14 != 0) goto L_0x0057
            goto L_0x002c
        L_0x0057:
            r1 = r9
            goto L_0x0061
        L_0x0059:
            boolean r14 = r0.equals(r12)
            if (r14 != 0) goto L_0x0060
            goto L_0x002c
        L_0x0060:
            r1 = r11
        L_0x0061:
            switch(r1) {
                case 0: goto L_0x006c;
                case 1: goto L_0x0069;
                case 2: goto L_0x0066;
                case 3: goto L_0x006c;
                case 4: goto L_0x0069;
                case 5: goto L_0x0066;
                default: goto L_0x0064;
            }
        L_0x0064:
            goto L_0x010b
        L_0x0066:
            int r14 = com.android.settings.C1993R$style.GlifTheme_DayNight
            return r14
        L_0x0069:
            int r14 = com.android.settings.C1993R$style.GlifV3Theme_DayNight
            return r14
        L_0x006c:
            int r14 = com.android.settings.C1993R$style.GlifV2Theme_DayNight
            return r14
        L_0x006f:
            int r14 = r0.hashCode()
            switch(r14) {
                case -2128555920: goto L_0x00a3;
                case -1241052239: goto L_0x009a;
                case 3175618: goto L_0x0091;
                case 115650329: goto L_0x0088;
                case 115650330: goto L_0x007f;
                case 767685465: goto L_0x0078;
                default: goto L_0x0076;
            }
        L_0x0076:
            r1 = r13
            goto L_0x00ab
        L_0x0078:
            boolean r14 = r0.equals(r2)
            if (r14 != 0) goto L_0x00ab
            goto L_0x0076
        L_0x007f:
            boolean r14 = r0.equals(r4)
            if (r14 != 0) goto L_0x0086
            goto L_0x0076
        L_0x0086:
            r1 = r3
            goto L_0x00ab
        L_0x0088:
            boolean r14 = r0.equals(r6)
            if (r14 != 0) goto L_0x008f
            goto L_0x0076
        L_0x008f:
            r1 = r5
            goto L_0x00ab
        L_0x0091:
            boolean r14 = r0.equals(r8)
            if (r14 != 0) goto L_0x0098
            goto L_0x0076
        L_0x0098:
            r1 = r7
            goto L_0x00ab
        L_0x009a:
            boolean r14 = r0.equals(r10)
            if (r14 != 0) goto L_0x00a1
            goto L_0x0076
        L_0x00a1:
            r1 = r9
            goto L_0x00ab
        L_0x00a3:
            boolean r14 = r0.equals(r12)
            if (r14 != 0) goto L_0x00aa
            goto L_0x0076
        L_0x00aa:
            r1 = r11
        L_0x00ab:
            switch(r1) {
                case 0: goto L_0x00bf;
                case 1: goto L_0x00bc;
                case 2: goto L_0x00b9;
                case 3: goto L_0x00b6;
                case 4: goto L_0x00b3;
                case 5: goto L_0x00b0;
                default: goto L_0x00ae;
            }
        L_0x00ae:
            goto L_0x010b
        L_0x00b0:
            int r14 = com.android.settings.C1993R$style.GlifTheme_Light
            return r14
        L_0x00b3:
            int r14 = com.android.settings.C1993R$style.GlifV3Theme
            return r14
        L_0x00b6:
            int r14 = com.android.settings.C1993R$style.GlifV2Theme
            return r14
        L_0x00b9:
            int r14 = com.android.settings.C1993R$style.GlifTheme
            return r14
        L_0x00bc:
            int r14 = com.android.settings.C1993R$style.GlifV3Theme_Light
            return r14
        L_0x00bf:
            int r14 = com.android.settings.C1993R$style.GlifV2Theme_Light
            return r14
        L_0x00c2:
            int r14 = r0.hashCode()
            switch(r14) {
                case -2128555920: goto L_0x00f6;
                case -1241052239: goto L_0x00ed;
                case 3175618: goto L_0x00e4;
                case 115650329: goto L_0x00db;
                case 115650330: goto L_0x00d2;
                case 767685465: goto L_0x00cb;
                default: goto L_0x00c9;
            }
        L_0x00c9:
            r1 = r13
            goto L_0x00fe
        L_0x00cb:
            boolean r14 = r0.equals(r2)
            if (r14 != 0) goto L_0x00fe
            goto L_0x00c9
        L_0x00d2:
            boolean r14 = r0.equals(r4)
            if (r14 != 0) goto L_0x00d9
            goto L_0x00c9
        L_0x00d9:
            r1 = r3
            goto L_0x00fe
        L_0x00db:
            boolean r14 = r0.equals(r6)
            if (r14 != 0) goto L_0x00e2
            goto L_0x00c9
        L_0x00e2:
            r1 = r5
            goto L_0x00fe
        L_0x00e4:
            boolean r14 = r0.equals(r8)
            if (r14 != 0) goto L_0x00eb
            goto L_0x00c9
        L_0x00eb:
            r1 = r7
            goto L_0x00fe
        L_0x00ed:
            boolean r14 = r0.equals(r10)
            if (r14 != 0) goto L_0x00f4
            goto L_0x00c9
        L_0x00f4:
            r1 = r9
            goto L_0x00fe
        L_0x00f6:
            boolean r14 = r0.equals(r12)
            if (r14 != 0) goto L_0x00fd
            goto L_0x00c9
        L_0x00fd:
            r1 = r11
        L_0x00fe:
            switch(r1) {
                case 0: goto L_0x0108;
                case 1: goto L_0x0105;
                case 2: goto L_0x0102;
                case 3: goto L_0x0108;
                case 4: goto L_0x0105;
                case 5: goto L_0x0102;
                default: goto L_0x0101;
            }
        L_0x0101:
            goto L_0x010b
        L_0x0102:
            int r14 = com.android.settings.C1993R$style.GlifTheme
            return r14
        L_0x0105:
            int r14 = com.android.settings.C1993R$style.GlifV3Theme
            return r14
        L_0x0108:
            int r14 = com.android.settings.C1993R$style.GlifV2Theme
            return r14
        L_0x010b:
            int r14 = com.android.settings.C1993R$style.GlifTheme
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.SetupWizardUtils.getTheme(android.content.Context, android.content.Intent):int");
    }

    public static int getTransparentTheme(Context context, Intent intent) {
        int i;
        int theme = getTheme(context, intent);
        if (ThemeHelper.isSetupWizardDayNightEnabled(context)) {
            i = C1993R$style.GlifV2Theme_DayNight_Transparent;
        } else {
            i = C1993R$style.GlifV2Theme_Light_Transparent;
        }
        if (theme == C1993R$style.GlifV3Theme_DayNight) {
            return C1993R$style.GlifV3Theme_DayNight_Transparent;
        }
        if (theme == C1993R$style.GlifV3Theme_Light) {
            return C1993R$style.GlifV3Theme_Light_Transparent;
        }
        if (theme == C1993R$style.GlifV2Theme_DayNight) {
            return C1993R$style.GlifV2Theme_DayNight_Transparent;
        }
        if (theme == C1993R$style.GlifV2Theme_Light) {
            return C1993R$style.GlifV2Theme_Light_Transparent;
        }
        if (theme == C1993R$style.GlifTheme_DayNight) {
            return C1993R$style.SetupWizardTheme_DayNight_Transparent;
        }
        if (theme == C1993R$style.GlifTheme_Light) {
            return C1993R$style.SetupWizardTheme_Light_Transparent;
        }
        if (theme == C1993R$style.GlifV3Theme) {
            return C1993R$style.GlifV3Theme_Transparent;
        }
        if (theme == C1993R$style.GlifV2Theme) {
            return C1993R$style.GlifV2Theme_Transparent;
        }
        return theme == C1993R$style.GlifTheme ? C1993R$style.SetupWizardTheme_Transparent : i;
    }

    public static void copySetupExtras(Intent intent, Intent intent2) {
        WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
    }

    public static Bundle copyLifecycleExtra(Bundle bundle, Bundle bundle2) {
        for (String str : Arrays.asList(new String[]{"firstRun", "isSetupFlow"})) {
            bundle2.putBoolean(str, bundle.getBoolean(str, false));
        }
        return bundle2;
    }
}
