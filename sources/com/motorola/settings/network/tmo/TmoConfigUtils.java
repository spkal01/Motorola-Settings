package com.motorola.settings.network.tmo;

import android.os.Bundle;

public class TmoConfigUtils {
    private static final String TAG = "TmoConfigUtils";

    private static Bundle buildTmoConfigQueryArgs(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("subId", i);
        return bundle;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0059, code lost:
        if (r1 == null) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x005b, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0080, code lost:
        if (r1 == null) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0089, code lost:
        return "1".equals(r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean getConfigFromTMOConfig(android.content.Context r4, java.lang.String r5, int r6) {
        /*
            java.lang.String r0 = "0"
            r1 = 0
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ Exception -> 0x0061 }
            android.net.Uri r2 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x0061 }
            android.os.Bundle r6 = buildTmoConfigQueryArgs(r6)     // Catch:{ Exception -> 0x0061 }
            android.database.Cursor r1 = r4.query(r2, r1, r6, r1)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r4 = "config"
            if (r1 == 0) goto L_0x0040
            boolean r6 = r1.moveToFirst()     // Catch:{ Exception -> 0x0061 }
            if (r6 == 0) goto L_0x0040
            r6 = 0
            java.lang.String r6 = r1.getString(r6)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r2 = TAG     // Catch:{ Exception -> 0x0061 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0061 }
            r3.<init>()     // Catch:{ Exception -> 0x0061 }
            r3.append(r4)     // Catch:{ Exception -> 0x0061 }
            r3.append(r5)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r4 = ":"
            r3.append(r4)     // Catch:{ Exception -> 0x0061 }
            r3.append(r6)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r4 = r3.toString()     // Catch:{ Exception -> 0x0061 }
            com.motorola.settings.network.MotoMnsLog.logd(r2, r4)     // Catch:{ Exception -> 0x0061 }
            r0 = r6
            goto L_0x0059
        L_0x0040:
            java.lang.String r6 = TAG     // Catch:{ Exception -> 0x0061 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0061 }
            r2.<init>()     // Catch:{ Exception -> 0x0061 }
            r2.append(r4)     // Catch:{ Exception -> 0x0061 }
            r2.append(r5)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r4 = "not found"
            r2.append(r4)     // Catch:{ Exception -> 0x0061 }
            java.lang.String r4 = r2.toString()     // Catch:{ Exception -> 0x0061 }
            com.motorola.settings.network.MotoMnsLog.logd(r6, r4)     // Catch:{ Exception -> 0x0061 }
        L_0x0059:
            if (r1 == 0) goto L_0x0083
        L_0x005b:
            r1.close()
            goto L_0x0083
        L_0x005f:
            r4 = move-exception
            goto L_0x008a
        L_0x0061:
            r4 = move-exception
            java.lang.String r6 = TAG     // Catch:{ all -> 0x005f }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r2.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r3 = "failed to get"
            r2.append(r3)     // Catch:{ all -> 0x005f }
            r2.append(r5)     // Catch:{ all -> 0x005f }
            java.lang.String r5 = "config :"
            r2.append(r5)     // Catch:{ all -> 0x005f }
            r2.append(r4)     // Catch:{ all -> 0x005f }
            java.lang.String r4 = r2.toString()     // Catch:{ all -> 0x005f }
            com.motorola.settings.network.MotoMnsLog.logd(r6, r4)     // Catch:{ all -> 0x005f }
            if (r1 == 0) goto L_0x0083
            goto L_0x005b
        L_0x0083:
            java.lang.String r4 = "1"
            boolean r4 = r4.equals(r0)
            return r4
        L_0x008a:
            if (r1 == 0) goto L_0x008f
            r1.close()
        L_0x008f:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.network.tmo.TmoConfigUtils.getConfigFromTMOConfig(android.content.Context, java.lang.String, int):boolean");
    }
}
