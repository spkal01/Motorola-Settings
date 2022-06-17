package com.android.settings.network.telephony;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionManager;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import com.android.internal.telephony.OperatorInfo;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CellInfoUtil {
    public static String getNetworkTitle(CellIdentity cellIdentity, String str) {
        if (cellIdentity != null) {
            String objects = Objects.toString(cellIdentity.getOperatorAlphaLong(), "");
            if (TextUtils.isEmpty(objects)) {
                objects = Objects.toString(cellIdentity.getOperatorAlphaShort(), "");
            }
            if (!TextUtils.isEmpty(objects)) {
                return objects;
            }
        }
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return BidiFormatter.getInstance().unicodeWrap(str, TextDirectionHeuristics.LTR);
    }

    public static CellIdentity getCellIdentity(CellInfo cellInfo) {
        if (cellInfo == null) {
            return null;
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellIdentity();
        }
        return null;
    }

    public static CellInfo convertOperatorInfoToCellInfo(OperatorInfo operatorInfo, String str) {
        String str2;
        String str3;
        String operatorNumeric = operatorInfo.getOperatorNumeric();
        if (operatorNumeric == null || !operatorNumeric.matches("^[0-9]{5,6}$")) {
            str3 = null;
            str2 = null;
        } else {
            str3 = operatorNumeric.substring(0, 3);
            str2 = operatorNumeric.substring(3);
        }
        CellIdentityGsm cellIdentityGsm = new CellIdentityGsm(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, str3, str2, operatorInfo.getOperatorAlphaLong(), operatorInfo.getOperatorAlphaShort(), Collections.emptyList());
        CellInfoGsm cellInfoGsm = new CellInfoGsm();
        cellInfoGsm.setRegistered(TextUtils.equals(operatorNumeric, str));
        cellInfoGsm.setCellIdentity(cellIdentityGsm);
        return cellInfoGsm;
    }

    public static String cellInfoListToString(List<CellInfo> list) {
        return (String) list.stream().map(CellInfoUtil$$ExternalSyntheticLambda0.INSTANCE).collect(Collectors.joining(", "));
    }

    public static String cellInfoToString(CellInfo cellInfo) {
        String simpleName = cellInfo.getClass().getSimpleName();
        CellIdentity cellIdentity = getCellIdentity(cellInfo);
        return String.format("{CellType = %s, isRegistered = %b, mcc = %s, mnc = %s, alphaL = %s, alphaS = %s}", new Object[]{simpleName, Boolean.valueOf(cellInfo.isRegistered()), getCellIdentityMcc(cellIdentity), getCellIdentityMnc(cellIdentity), cellIdentity.getOperatorAlphaLong(), cellIdentity.getOperatorAlphaShort()});
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getCellIdentityMcc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L_0x003a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto L_0x000e
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x000e:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L_0x0019
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x0019:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L_0x0024
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x0024:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L_0x002f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x002f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L_0x003a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x003a:
            r2 = r0
        L_0x003b:
            if (r2 != 0) goto L_0x003e
            goto L_0x003f
        L_0x003e:
            r0 = r2
        L_0x003f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMcc(android.telephony.CellIdentity):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getCellIdentityMnc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L_0x003a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto L_0x000e
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x000e:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L_0x0019
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x0019:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L_0x0024
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x0024:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L_0x002f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x002f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L_0x003a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x003a:
            r2 = r0
        L_0x003b:
            if (r2 != 0) goto L_0x003e
            goto L_0x003f
        L_0x003e:
            r0 = r2
        L_0x003f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMnc(android.telephony.CellIdentity):java.lang.String");
    }

    public static String getMotoNetworkTitle(Context context, int i, String str) {
        if (MotoTelephonyFeature.isFtr5303Enabled(context, i)) {
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(context, i);
            String[] stringArray = resourcesForSubId.getStringArray(C1978R$array.config_prefixedNationalRoamingNetworks);
            String string = resourcesForSubId.getString(C1992R$string.config_homeNetwork);
            int i2 = 0;
            str = str.split("\\.")[0].replace("PLAY", string);
            int length = stringArray.length;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                String str2 = stringArray[i2];
                if (str2.equalsIgnoreCase(str)) {
                    str = string + " (" + str2 + ")";
                    MotoMnsLog.logd("NetworkSelectSetting", "formatted network name: " + str);
                    break;
                }
                i2++;
            }
        }
        return MotoTelephonyFeature.localizeChinaOperator(context, i) ? getChinaOperatorTitle(context, str) : str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0038, code lost:
        r2 = (android.telephony.CellIdentityNr) r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getOperatorNumeric(android.telephony.CellInfo r2) {
        /*
            android.telephony.CellIdentity r2 = getCellIdentity(r2)
            r0 = 0
            if (r2 != 0) goto L_0x0008
            return r0
        L_0x0008:
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto L_0x0013
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMobileNetworkOperator()
            return r2
        L_0x0013:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L_0x001e
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMobileNetworkOperator()
            return r2
        L_0x001e:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L_0x0029
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMobileNetworkOperator()
            return r2
        L_0x0029:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L_0x0034
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMobileNetworkOperator()
            return r2
        L_0x0034:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L_0x004a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r1 = r2.getMccString()
            if (r1 != 0) goto L_0x0041
            return r0
        L_0x0041:
            java.lang.String r2 = r2.getMncString()
            java.lang.String r2 = r1.concat(r2)
            return r2
        L_0x004a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getOperatorNumeric(android.telephony.CellInfo):java.lang.String");
    }

    public static String getChinaOperatorTitle(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String chinaOperatorTitle = getChinaOperatorTitle(context, str, C1992R$string.operator_name_china_mobile, C1978R$array.china_operator_cmcc);
        if (chinaOperatorTitle == null) {
            chinaOperatorTitle = getChinaOperatorTitle(context, str, C1992R$string.operator_name_china_unicom, C1978R$array.china_operator_cu);
        }
        if (chinaOperatorTitle == null) {
            chinaOperatorTitle = getChinaOperatorTitle(context, str, C1992R$string.operator_name_china_telcom, C1978R$array.china_operator_ct);
        }
        return chinaOperatorTitle == null ? str : chinaOperatorTitle;
    }

    private static String getChinaOperatorTitle(Context context, String str, int i, int i2) {
        String[] stringArray = context.getResources().getStringArray(i2);
        if (stringArray == null) {
            return null;
        }
        for (String equalsIgnoreCase : stringArray) {
            if (str.equalsIgnoreCase(equalsIgnoreCase)) {
                return context.getResources().getString(i);
            }
        }
        return null;
    }
}
