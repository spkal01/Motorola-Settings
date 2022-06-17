package com.motorola.settings.accounts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.XmlSerializerAndParser;
import android.util.Log;
import android.util.TypedXmlPullParser;
import android.util.TypedXmlSerializer;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

public class AuthenticatorDescriptionExtCache extends RegisteredServicesCache<AuthenticatorDescriptionExt> {
    private static AuthenticatorDescriptionExtCache instance;
    private static HashMap<String, Integer> sAttributeMap;
    private static final MySerializer sSerializer = new MySerializer();

    static {
        HashMap<String, Integer> hashMap = new HashMap<>();
        sAttributeMap = hashMap;
        hashMap.put("accountType", 1);
        sAttributeMap.put("hideAccount", 2);
        sAttributeMap.put("hideAuthenticator", 3);
        sAttributeMap.put("showSyncOption", 4);
        sAttributeMap.put("removeAllowed", 5);
        sAttributeMap.put("editSettingActivity", 6);
        sAttributeMap.put("accountDisplayName", 7);
    }

    protected AuthenticatorDescriptionExtCache(Context context) {
        super(context, "com.motorola.accounts.AccountHelper", "com.motorola.accounts.AccountHelper", "account-authenticator-ext", sSerializer);
    }

    public static AuthenticatorDescriptionExtCache getInstance(Context context) {
        if (instance == null) {
            instance = new AuthenticatorDescriptionExtCache(context);
            Log.v("AuthenticatorDescriptionExtCache", "New instance of AuthenticatorDescriptionExtCache created");
        }
        return instance;
    }

    private static boolean isSystemOrMotoApp(PackageManager packageManager, String str) {
        try {
            if ((packageManager.getApplicationInfo(str, 0).flags & 1) <= 0 && packageManager.checkSignatures("com.motorola.motosignature.app", str) != 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r7v0 */
    /* JADX WARNING: type inference failed for: r7v5, types: [int] */
    /* JADX WARNING: type inference failed for: r7v17 */
    /* JADX WARNING: type inference failed for: r7v19 */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00cc, code lost:
        r7 = r7;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.motorola.settings.accounts.AuthenticatorDescriptionExt parseServiceAttributes(android.content.res.Resources r18, java.lang.String r19, android.util.AttributeSet r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            android.content.Context r4 = r0.mContext
            android.content.pm.PackageManager r4 = r4.getPackageManager()
            boolean r5 = isSystemOrMotoApp(r4, r2)
            r6 = 1
            r7 = 0
            r8 = 0
            r14 = 1
            r15 = 0
            r16 = 0
            if (r5 != 0) goto L_0x0058
            android.content.Context r0 = r0.mContext
            android.os.UserManager r0 = android.os.UserManager.get(r0)
            boolean r0 = r0.isAdminUser()
            if (r0 != 0) goto L_0x0057
            r4.getApplicationInfo(r2, r7)     // Catch:{ NameNotFoundException -> 0x002b }
            return r8
        L_0x002b:
            r10 = r8
        L_0x002c:
            int r0 = r20.getAttributeCount()
            if (r7 >= r0) goto L_0x004b
            java.lang.String r0 = r3.getAttributeName(r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = sAttributeMap
            java.lang.Object r0 = r1.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            if (r0 != r6) goto L_0x0048
            java.lang.String r10 = r3.getAttributeValue(r7)
        L_0x0048:
            int r7 = r7 + 1
            goto L_0x002c
        L_0x004b:
            if (r10 != 0) goto L_0x004e
            goto L_0x0057
        L_0x004e:
            com.motorola.settings.accounts.AuthenticatorDescriptionExt r8 = new com.motorola.settings.accounts.AuthenticatorDescriptionExt
            r11 = 1
            r12 = 1
            r13 = 0
            r9 = r8
            r9.<init>(r10, r11, r12, r13, r14, r15, r16)
        L_0x0057:
            return r8
        L_0x0058:
            r4 = r6
            r0 = r7
            r6 = r0
            r5 = r8
        L_0x005c:
            int r9 = r20.getAttributeCount()
            if (r0 >= r9) goto L_0x00cf
            java.lang.String r9 = r3.getAttributeName(r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = sAttributeMap     // Catch:{ Exception -> 0x00aa }
            java.lang.Object r10 = r10.get(r9)     // Catch:{ Exception -> 0x00aa }
            java.lang.Integer r10 = (java.lang.Integer) r10     // Catch:{ Exception -> 0x00aa }
            int r10 = r10.intValue()     // Catch:{ Exception -> 0x00aa }
            switch(r10) {
                case 1: goto L_0x00a5;
                case 2: goto L_0x00a0;
                case 3: goto L_0x009b;
                case 4: goto L_0x0096;
                case 5: goto L_0x0091;
                case 6: goto L_0x008c;
                case 7: goto L_0x0076;
                default: goto L_0x0075;
            }     // Catch:{ Exception -> 0x00aa }
        L_0x0075:
            goto L_0x00cc
        L_0x0076:
            java.lang.String r15 = r3.getAttributeValue(r0)     // Catch:{ Exception -> 0x00aa }
            boolean r10 = android.text.TextUtils.isEmpty(r15)     // Catch:{ Exception -> 0x00aa }
            if (r10 != 0) goto L_0x00cc
            int r10 = r1.getIdentifier(r15, r8, r2)     // Catch:{ Exception -> 0x00aa }
            if (r10 == 0) goto L_0x00cc
            java.lang.String r9 = r1.getString(r10)     // Catch:{ Exception -> 0x00aa }
            r15 = r9
            goto L_0x00cc
        L_0x008c:
            java.lang.String r16 = r3.getAttributeValue(r0)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x0091:
            boolean r14 = r3.getAttributeBooleanValue(r0, r14)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x0096:
            boolean r4 = r3.getAttributeBooleanValue(r0, r4)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x009b:
            boolean r6 = r3.getAttributeBooleanValue(r0, r6)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x00a0:
            boolean r7 = r3.getAttributeBooleanValue(r0, r7)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x00a5:
            java.lang.String r5 = r3.getAttributeValue(r0)     // Catch:{ Exception -> 0x00aa }
            goto L_0x00cc
        L_0x00aa:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Found an unsupported tag "
            r10.append(r11)
            r10.append(r9)
            java.lang.String r9 = " value = "
            r10.append(r9)
            java.lang.String r9 = r3.getAttributeValue(r0)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            java.lang.String r10 = "AuthenticatorDescriptionExtCache"
            android.util.Log.e(r10, r9)
        L_0x00cc:
            int r0 = r0 + 1
            goto L_0x005c
        L_0x00cf:
            if (r16 == 0) goto L_0x00dd
            java.lang.String r0 = r16.trim()
            int r0 = r0.length()
            if (r0 > 0) goto L_0x00dd
            r16 = r8
        L_0x00dd:
            if (r15 == 0) goto L_0x00ea
            java.lang.String r0 = r15.trim()
            int r0 = r0.length()
            if (r0 > 0) goto L_0x00ea
            r15 = r8
        L_0x00ea:
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            if (r0 == 0) goto L_0x00f1
            return r8
        L_0x00f1:
            com.motorola.settings.accounts.AuthenticatorDescriptionExt r8 = new com.motorola.settings.accounts.AuthenticatorDescriptionExt
            r0 = r8
            r1 = r5
            r2 = r7
            r3 = r6
            r5 = r14
            r6 = r15
            r7 = r16
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.accounts.AuthenticatorDescriptionExtCache.parseServiceAttributes(android.content.res.Resources, java.lang.String, android.util.AttributeSet):com.motorola.settings.accounts.AuthenticatorDescriptionExt");
    }

    private static class MySerializer implements XmlSerializerAndParser<AuthenticatorDescriptionExt> {
        private MySerializer() {
        }

        public void writeAsXml(AuthenticatorDescriptionExt authenticatorDescriptionExt, TypedXmlSerializer typedXmlSerializer) throws IOException {
            typedXmlSerializer.attribute((String) null, "type", authenticatorDescriptionExt.type);
        }

        public AuthenticatorDescriptionExt createFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            return AuthenticatorDescriptionExt.newKey(typedXmlPullParser.getAttributeValue((String) null, "type"));
        }
    }
}
