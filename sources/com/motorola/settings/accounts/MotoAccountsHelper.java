package com.motorola.settings.accounts;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.RegisteredServicesCache;
import android.os.UserHandle;
import android.util.Log;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MotoAccountsHelper {
    private static ArrayList<String> sHiddenAccountTypes;

    private static final AuthenticatorDescriptionExt getAuthenticatorDescriptionExt(Context context, String str) {
        RegisteredServicesCache.ServiceInfo serviceInfo = AuthenticatorDescriptionExtCache.getInstance(context.getApplicationContext()).getServiceInfo(AuthenticatorDescriptionExt.newKey(str), UserHandle.getUserId(-2));
        if (serviceInfo != null) {
            return (AuthenticatorDescriptionExt) serviceInfo.type;
        }
        return new AuthenticatorDescriptionExt(str);
    }

    public static final boolean isHidden(Context context, String str) {
        if (sHiddenAccountTypes == null) {
            ArrayList<String> arrayList = new ArrayList<>();
            sHiddenAccountTypes = arrayList;
            getHiddenAccountTypeFromFile(arrayList, "/system/etc/hideaccount/");
            getHiddenAccountTypeFromFile(sHiddenAccountTypes, "/product/etc/hideaccount/");
        }
        if (sHiddenAccountTypes.contains(str)) {
            return true;
        }
        return getAuthenticatorDescriptionExt(context, str).isAccountHidden;
    }

    public static boolean syncAvailable(Context context, String str) {
        return getAuthenticatorDescriptionExt(context, str).showSyncOption;
    }

    public static boolean removeAllowed(Context context, String str) {
        if (str != null) {
            return getAuthenticatorDescriptionExt(context, str).isRemoveAllowed;
        }
        return true;
    }

    public static AuthenticatorDescription[] removeUnneededAuthDescs(Context context, AuthenticatorDescription[] authenticatorDescriptionArr) {
        ArrayList arrayList = new ArrayList(authenticatorDescriptionArr.length);
        for (int i = 0; i < authenticatorDescriptionArr.length; i++) {
            String str = authenticatorDescriptionArr[i].type;
            if (!isHidden(context, str) && !getAuthenticatorDescriptionExt(context, str).isAuthenticatorHidden) {
                arrayList.add(authenticatorDescriptionArr[i]);
            }
        }
        return (AuthenticatorDescription[]) arrayList.toArray(new AuthenticatorDescription[0]);
    }

    public static String getAccountDisplayName(Context context, Account account) {
        String str = getAuthenticatorDescriptionExt(context, account.type).accountDisplayName;
        return str == null ? account.name : str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0052 A[SYNTHETIC, Splitter:B:28:0x0052] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x005d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void getHiddenAccountTypeFromFile(java.util.ArrayList<java.lang.String> r7, java.lang.String r8) {
        /*
            java.io.File r0 = new java.io.File
            r0.<init>(r8)
            boolean r8 = r0.exists()
            if (r8 == 0) goto L_0x0060
            boolean r8 = r0.isDirectory()
            if (r8 == 0) goto L_0x0060
            java.io.File[] r8 = r0.listFiles()
            if (r8 == 0) goto L_0x0060
            int r0 = r8.length
            if (r0 <= 0) goto L_0x0060
            int r0 = r8.length
            r1 = 0
        L_0x001c:
            if (r1 >= r0) goto L_0x0060
            r2 = r8[r1]
            boolean r3 = r2.isDirectory()
            if (r3 != 0) goto L_0x005d
            r3 = 0
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x003a }
            r4.<init>(r2)     // Catch:{ all -> 0x003a }
            java.util.ArrayList r3 = parse(r4)     // Catch:{ all -> 0x0039 }
            if (r3 == 0) goto L_0x0035
            r7.addAll(r3)     // Catch:{ all -> 0x0039 }
        L_0x0035:
            r4.close()     // Catch:{ Exception -> 0x005d }
            goto L_0x005d
        L_0x0039:
            r3 = r4
        L_0x003a:
            java.lang.String r4 = "MotoAccountsHelper"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
            r5.<init>()     // Catch:{ all -> 0x0056 }
            java.lang.String r6 = "Error occurred while loading app list file "
            r5.append(r6)     // Catch:{ all -> 0x0056 }
            r5.append(r2)     // Catch:{ all -> 0x0056 }
            java.lang.String r2 = r5.toString()     // Catch:{ all -> 0x0056 }
            android.util.Log.e(r4, r2)     // Catch:{ all -> 0x0056 }
            if (r3 == 0) goto L_0x005d
            r3.close()     // Catch:{ Exception -> 0x005d }
            goto L_0x005d
        L_0x0056:
            r7 = move-exception
            if (r3 == 0) goto L_0x005c
            r3.close()     // Catch:{ Exception -> 0x005c }
        L_0x005c:
            throw r7
        L_0x005d:
            int r1 = r1 + 1
            goto L_0x001c
        L_0x0060:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.accounts.MotoAccountsHelper.getHiddenAccountTypeFromFile(java.util.ArrayList, java.lang.String):void");
    }

    private static ArrayList<String> parse(InputStream inputStream) {
        String textContent;
        if (inputStream != null) {
            ArrayList<String> arrayList = null;
            try {
                Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getDocumentElement();
                if (documentElement == null || !documentElement.hasChildNodes()) {
                    return null;
                }
                ArrayList<String> arrayList2 = new ArrayList<>();
                try {
                    NodeList childNodes = documentElement.getChildNodes();
                    int i = 0;
                    while (childNodes != null && i < childNodes.getLength()) {
                        Node item = childNodes.item(i);
                        if (item != null) {
                            if (item.getNodeType() == 1) {
                                if (item.getNodeName().equalsIgnoreCase("accounttype") && (textContent = item.getTextContent()) != null && !textContent.trim().isEmpty()) {
                                    arrayList2.add(textContent);
                                }
                            }
                        }
                        i++;
                    }
                    return arrayList2;
                } catch (Exception e) {
                    e = e;
                    arrayList = arrayList2;
                    Log.e("MotoAccountsHelper", "Error occured while parsing inputstream", e);
                    return arrayList;
                }
            } catch (Exception e2) {
                e = e2;
                Log.e("MotoAccountsHelper", "Error occured while parsing inputstream", e);
                return arrayList;
            }
        } else {
            throw new IllegalArgumentException("Invalid inputstream");
        }
    }
}
