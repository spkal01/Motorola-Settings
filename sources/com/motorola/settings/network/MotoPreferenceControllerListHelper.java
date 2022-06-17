package com.motorola.settings.network;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MotoPreferenceControllerListHelper {
    protected static final String[] REASSIGN_CATEGORY_KEYS = {"enabled_state_container", "calling_category", "network_operators_category_key"};

    public static List<AbstractPreferenceController> getControllersFromXmlAndCode(Context context, int i, List<AbstractPreferenceController> list) {
        ArrayList arrayList = new ArrayList();
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, i), list);
        if (list != null) {
            arrayList.addAll(list);
        }
        if (filterControllers != null) {
            arrayList.addAll(filterControllers);
        }
        return arrayList;
    }

    public static void updateControllers(List<AbstractPreferenceController> list, List<AbstractPreferenceController> list2, boolean z) {
        if (list != null && list2 != null) {
            List<AbstractPreferenceController> filterControllers = filterControllers(list, list2);
            list.clear();
            list.addAll(filterControllers);
            if (z) {
                list.addAll(list2);
            }
        }
    }

    public static List<AbstractPreferenceController> filterControllers(List<AbstractPreferenceController> list, List<AbstractPreferenceController> list2) {
        if (list == null || list2 == null) {
            return list;
        }
        TreeSet treeSet = new TreeSet();
        ArrayList arrayList = new ArrayList();
        for (AbstractPreferenceController preferenceKey : list2) {
            String preferenceKey2 = preferenceKey.getPreferenceKey();
            if (preferenceKey2 != null) {
                treeSet.add(preferenceKey2);
            }
        }
        for (AbstractPreferenceController next : list) {
            if (treeSet.contains(next.getPreferenceKey())) {
                MotoMnsLog.logw("MotoPreferenceControllerListHelper", next.getPreferenceKey() + " already has a controller");
            } else {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static void addMotoXmlRes(Context context, PreferenceManager preferenceManager, PreferenceScreen preferenceScreen, int i) {
        PreferenceScreen inflateFromResource = preferenceManager.inflateFromResource(context, i, (PreferenceScreen) null);
        if (inflateFromResource != null) {
            for (String str : REASSIGN_CATEGORY_KEYS) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) inflateFromResource.findPreference(str);
                reAssignParent((PreferenceGroup) preferenceManager.findPreference(str), preferenceGroup);
                if (preferenceGroup != null) {
                    inflateFromResource.removePreference(preferenceGroup);
                }
            }
            reAssignParent(preferenceScreen, inflateFromResource);
        }
    }

    private static void reAssignParent(PreferenceGroup preferenceGroup, PreferenceGroup preferenceGroup2) {
        if (preferenceGroup != null && preferenceGroup2 != null) {
            MotoMnsLog.logd("MotoPreferenceControllerListHelper", "newParent:" + preferenceGroup.getKey());
            int preferenceCount = preferenceGroup2.getPreferenceCount();
            Preference[] preferenceArr = new Preference[preferenceCount];
            for (int i = 0; i < preferenceCount; i++) {
                preferenceArr[i] = preferenceGroup2.getPreference(i);
            }
            for (int i2 = 0; i2 < preferenceCount; i2++) {
                Preference preference = preferenceArr[i2];
                preferenceGroup2.removePreference(preference);
                preferenceGroup.addPreference(preference);
                MotoMnsLog.logd("MotoPreferenceControllerListHelper", "index:" + i2 + ", key:" + preference.getKey());
            }
        }
    }

    public static void initControllers(Context context, int i, List<AbstractPreferenceController> list, Lifecycle lifecycle, FragmentManager fragmentManager) {
        list.forEach(new MotoPreferenceControllerListHelper$$ExternalSyntheticLambda0(i, fragmentManager, lifecycle));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$initControllers$0(int i, FragmentManager fragmentManager, Lifecycle lifecycle, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof MotoTelephonyInit) {
            ((MotoTelephonyInit) abstractPreferenceController).init(i, fragmentManager);
        }
        if (abstractPreferenceController instanceof LifecycleObserver) {
            lifecycle.addObserver((LifecycleObserver) abstractPreferenceController);
        }
    }
}
