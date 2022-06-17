package com.motorola.settings.network;

import android.content.Context;
import android.telephony.SubscriptionManager;
import com.android.settings.C1994R$xml;
import com.android.settings.network.telephony.MobileNetworkSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import com.motorola.settings.network.att.AttMobileNetworkSettings;
import com.motorola.settings.network.emara.EmaraMobileNetworkSettings;
import com.motorola.settings.network.sprint.SprintMobileNetworkSettings;
import com.motorola.settings.network.tmo.TmoMobileNetworkSettings;
import com.motorola.settings.network.visible.VisibleMobileNetworkSettings;
import com.motorola.settings.network.vzw.VzwMobileNetworkSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MotoTelephonyFactory {
    public static MobileNetworkSettings createMobileNetworkSettings(Context context, int i) {
        if (MotoTelephonyFeature.supportAttFeatures(context, i)) {
            return new AttMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportEmaraFeatures(context, i)) {
            return new EmaraMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportSprintFeatures(context, i)) {
            return new SprintMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportTmoFeatures(context, i)) {
            return new TmoMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportVisibleFeatures(context, i)) {
            return new VisibleMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportVzwFeatures(context, i)) {
            return new VzwMobileNetworkSettings();
        }
        if (MotoTelephonyFeature.supportCommonFeatures(context, i)) {
            return new ExtendedMobileNetworkSettings();
        }
        return new MobileNetworkSettings();
    }

    public static String getMnsClassName(Context context, int i) {
        if (MotoTelephonyFeature.supportAttFeatures(context, i)) {
            return "com.motorola.settings.network.att.AttMobileNetworkSettings";
        }
        if (MotoTelephonyFeature.supportEmaraFeatures(context, i)) {
            return "com.motorola.settings.network.emara.EmaraMobileNetworkSettings";
        }
        if (MotoTelephonyFeature.supportSprintFeatures(context, i)) {
            return "com.motorola.settings.network.sprint.SprintMobileNetworkSettings";
        }
        if (MotoTelephonyFeature.supportTmoFeatures(context, i)) {
            return "com.motorola.settings.network.tmo.TmoMobileNetworkSettings";
        }
        if (MotoTelephonyFeature.supportVisibleFeatures(context, i)) {
            return "com.motorola.settings.network.visible.VisibleMobileNetworkSettings";
        }
        if (MotoTelephonyFeature.supportVzwFeatures(context, i)) {
            return "com.motorola.settings.network.vzw.VzwMobileNetworkSettings";
        }
        return MotoTelephonyFeature.supportCommonFeatures(context, i) ? "com.motorola.settings.network.ExtendedMobileNetworkSettings" : "com.android.settings.network.telephony.MobileNetworkSettings";
    }

    public static Set<Integer> getAllSimFragmentXml(Context context) {
        int[] activeSubscriptionIdList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionIdList();
        TreeSet treeSet = new TreeSet();
        treeSet.add(Integer.valueOf(C1994R$xml.mobile_network_settings));
        for (int mnsClassName : activeSubscriptionIdList) {
            String mnsClassName2 = getMnsClassName(context, mnsClassName);
            mnsClassName2.hashCode();
            char c = 65535;
            switch (mnsClassName2.hashCode()) {
                case -1506260195:
                    if (mnsClassName2.equals("com.motorola.settings.network.ExtendedMobileNetworkSettings")) {
                        c = 0;
                        break;
                    }
                    break;
                case 772774324:
                    if (mnsClassName2.equals("com.motorola.settings.network.att.AttMobileNetworkSettings")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1139948634:
                    if (mnsClassName2.equals("com.motorola.settings.network.sprint.SprintMobileNetworkSettings")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1233326608:
                    if (mnsClassName2.equals("com.motorola.settings.network.vzw.VzwMobileNetworkSettings")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1947029478:
                    if (mnsClassName2.equals("com.motorola.settings.network.emara.EmaraMobileNetworkSettings")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1975029970:
                    if (mnsClassName2.equals("com.motorola.settings.network.visible.VisibleMobileNetworkSettings")) {
                        c = 5;
                        break;
                    }
                    break;
                case 2135086474:
                    if (mnsClassName2.equals("com.motorola.settings.network.tmo.TmoMobileNetworkSettings")) {
                        c = 6;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    break;
                case 1:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.att_mobile_network_settings));
                    break;
                case 2:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.sprint_mobile_network_settings));
                    break;
                case 3:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.vzw_mobile_network_settings));
                    break;
                case 4:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.emara_mobile_network_settings));
                    break;
                case 5:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.visible_mobile_network_settings));
                    break;
                case 6:
                    treeSet.add(Integer.valueOf(C1994R$xml.extended_mobile_network_settings));
                    treeSet.add(Integer.valueOf(C1994R$xml.tmo_mobile_network_settings));
                    break;
            }
        }
        return treeSet;
    }

    public static List<AbstractPreferenceController> getControllersForSubId(Context context, int i) {
        List<AbstractPreferenceController> controllersFromXmlAndCode = MotoPreferenceControllerListHelper.getControllersFromXmlAndCode(context, C1994R$xml.mobile_network_settings, (List<AbstractPreferenceController>) null);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String mnsClassName = getMnsClassName(context, i);
        mnsClassName.hashCode();
        char c = 65535;
        switch (mnsClassName.hashCode()) {
            case -1506260195:
                if (mnsClassName.equals("com.motorola.settings.network.ExtendedMobileNetworkSettings")) {
                    c = 0;
                    break;
                }
                break;
            case 772774324:
                if (mnsClassName.equals("com.motorola.settings.network.att.AttMobileNetworkSettings")) {
                    c = 1;
                    break;
                }
                break;
            case 1139948634:
                if (mnsClassName.equals("com.motorola.settings.network.sprint.SprintMobileNetworkSettings")) {
                    c = 2;
                    break;
                }
                break;
            case 1233326608:
                if (mnsClassName.equals("com.motorola.settings.network.vzw.VzwMobileNetworkSettings")) {
                    c = 3;
                    break;
                }
                break;
            case 1947029478:
                if (mnsClassName.equals("com.motorola.settings.network.emara.EmaraMobileNetworkSettings")) {
                    c = 4;
                    break;
                }
                break;
            case 1975029970:
                if (mnsClassName.equals("com.motorola.settings.network.visible.VisibleMobileNetworkSettings")) {
                    c = 5;
                    break;
                }
                break;
            case 2135086474:
                if (mnsClassName.equals("com.motorola.settings.network.tmo.TmoMobileNetworkSettings")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 1:
                arrayList2.addAll(AttMobileNetworkSettings.createAttControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 2:
                arrayList2.addAll(SprintMobileNetworkSettings.createSprintControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 3:
                arrayList2.addAll(VzwMobileNetworkSettings.createVzwControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 4:
                arrayList2.addAll(EmaraMobileNetworkSettings.createEmaraControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 5:
                arrayList2.addAll(VisibleMobileNetworkSettings.createVisibleControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
            case 6:
                arrayList2.addAll(TmoMobileNetworkSettings.createTmoControllers(context, i));
                arrayList.addAll(ExtendedMobileNetworkSettings.createExtenededControllers(context, i));
                break;
        }
        MotoPreferenceControllerListHelper.updateControllers(arrayList, arrayList2, true);
        MotoPreferenceControllerListHelper.updateControllers(controllersFromXmlAndCode, arrayList, true);
        return controllersFromXmlAndCode;
    }
}
