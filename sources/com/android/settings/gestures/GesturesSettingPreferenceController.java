package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GesturesSettingPreferenceController extends BasePreferenceController {
    private static final String FAKE_PREF_KEY = "fake_key_only_for_get_available";
    private static final String KEY_GESTURES_SETTINGS = "gesture_settings";
    private final AwareFeatureProvider mAwareFeatureProvider;
    private final AssistGestureFeatureProvider mFeatureProvider;
    private List<AbstractPreferenceController> mGestureControllers;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GesturesSettingPreferenceController(Context context) {
        super(context, KEY_GESTURES_SETTINGS);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
        this.mAwareFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
    }

    public int getAvailabilityStatus() {
        boolean z;
        if (this.mGestureControllers == null) {
            this.mGestureControllers = buildAllPreferenceControllers(this.mContext);
        }
        Iterator<AbstractPreferenceController> it = this.mGestureControllers.iterator();
        loop0:
        while (true) {
            z = false;
            while (true) {
                if (!it.hasNext()) {
                    break loop0;
                }
                AbstractPreferenceController next = it.next();
                if (z || next.isAvailable()) {
                    z = true;
                }
            }
        }
        if (z) {
            return 0;
        }
        return 3;
    }

    private static List<AbstractPreferenceController> buildAllPreferenceControllers(Context context) {
        AmbientDisplayConfiguration ambientDisplayConfiguration = new AmbientDisplayConfiguration(context);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AssistGestureSettingsPreferenceController(context, FAKE_PREF_KEY).setAssistOnly(false));
        arrayList.add(new SwipeToNotificationPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new DoubleTwistPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new DoubleTapPowerPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new PickupGesturePreferenceController(context, FAKE_PREF_KEY).setConfig(ambientDisplayConfiguration));
        arrayList.add(new DoubleTapScreenPreferenceController(context, FAKE_PREF_KEY).setConfig(ambientDisplayConfiguration));
        arrayList.add(new PreventRingingParentPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new SystemNavigationPreferenceController(context, FAKE_PREF_KEY));
        return arrayList;
    }
}
