package com.motorola.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;

public class DoublePressPowerToLaunchAppPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    private static final String APP_NAME_ALEXA = "ALEXA";
    private static final String ASSIST_APP_INTENT = "android.intent.action.ASSIST";
    private static final String CAMERA_APP_INTENT = "ACTION_CAMERA";
    private static final boolean DBG = Build.IS_DEBUGGABLE;
    private static final String KEY_DOUBLE_PRESS_POWER_TO_LAUNCH_APP = "gesture_double_press_power_to_launch_app";
    public static final int MULTI_PRESS_POWER_LAUNCH_APP = 3;
    private static final String TAG = "DblPressPwrLaunchAppCtrllr";
    final boolean mCameraGestureEnabled;
    final int mDoublePressOnPowerBehavior;
    private final String[] mLaunchableAppsList;
    private TriplePowerKeySettingObserver mSettingObserver = null;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY_DOUBLE_PRESS_POWER_TO_LAUNCH_APP;
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

    public DoublePressPowerToLaunchAppPreferenceController(Context context, String str) {
        super(context, str);
        Resources resources = context.getResources();
        this.mDoublePressOnPowerBehavior = resources.getInteger(17694808);
        this.mCameraGestureEnabled = resources.getBoolean(17891408);
        this.mLaunchableAppsList = resources.getStringArray(C1978R$array.config_double_press_power_launchable_apps_list);
    }

    public int getAvailabilityStatus() {
        return (this.mDoublePressOnPowerBehavior != 3 || !hasLaunchableApps()) ? 3 : 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
            if (findPreference instanceof ListPreference) {
                initListPreference((ListPreference) findPreference);
            }
            if (TripleTapPowerEmergencyCallPrefController.isGestureAvailable(this.mContext)) {
                this.mSettingObserver = new TriplePowerKeySettingObserver(findPreference, this);
            }
        }
    }

    public void updateState(Preference preference) {
        if (preference != null && (preference instanceof ListPreference)) {
            if (TripleTapPowerEmergencyCallPrefController.isTripleTapEmergencyCallEnabled(this.mContext)) {
                preference.setEnabled(false);
                preference.setSummary((CharSequence) this.mContext.getResources().getString(C1992R$string.double_press_power_setting_disabled));
                return;
            }
            preference.setEnabled(true);
            setSummary((ListPreference) preference, getSelection());
        }
    }

    public void onResume() {
        TriplePowerKeySettingObserver triplePowerKeySettingObserver = this.mSettingObserver;
        if (triplePowerKeySettingObserver != null) {
            triplePowerKeySettingObserver.register(this.mContext.getContentResolver());
        }
    }

    public void onPause() {
        TriplePowerKeySettingObserver triplePowerKeySettingObserver = this.mSettingObserver;
        if (triplePowerKeySettingObserver != null) {
            triplePowerKeySettingObserver.unregister(this.mContext.getContentResolver());
        }
    }

    private void initListPreference(ListPreference listPreference) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Resources resources = this.mContext.getResources();
        arrayList.add(resources.getString(C1992R$string.double_press_power_launch_none));
        arrayList2.add("");
        for (String str : this.mLaunchableAppsList) {
            String launchableAppLabelOrNull = getLaunchableAppLabelOrNull(str);
            if (launchableAppLabelOrNull != null) {
                if (launchableAppLabelOrNull.equalsIgnoreCase(APP_NAME_ALEXA)) {
                    arrayList.add(resources.getString(C1992R$string.double_press_power_launch_alexa));
                } else {
                    arrayList.add(resources.getString(C1992R$string.double_press_power_launch_app, new Object[]{launchableAppLabelOrNull}));
                }
                arrayList2.add(str);
            }
        }
        if (this.mCameraGestureEnabled) {
            arrayList.add(resources.getString(C1992R$string.double_press_power_launch_app, new Object[]{resources.getString(C1992R$string.double_press_power_launch_appname_camera)}));
            arrayList2.add(CAMERA_APP_INTENT);
        }
        listPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
        listPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
        String selection = getSelection();
        listPreference.setValue(selection);
        setSummary(listPreference, selection);
    }

    private boolean hasLaunchableApps() {
        if (this.mCameraGestureEnabled) {
            return true;
        }
        for (String launchableAppLabelOrNull : this.mLaunchableAppsList) {
            if (getLaunchableAppLabelOrNull(launchableAppLabelOrNull) != null) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:32|33|(2:35|48)(1:45)) */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        if (DBG != false) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009e, code lost:
        android.util.Log.e(TAG, "Package not found. does not have label to be shown in Settings UI. Ignoring for intent : " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        return null;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:32:0x009a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getLaunchableAppLabelOrNull(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.String r0 = "DblPressPwrLaunchAppCtrllr"
            r1 = 0
            android.content.Intent r2 = android.content.Intent.parseUri(r7, r1)     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r3 = "android.intent.action.ASSIST"
            java.lang.String r4 = r2.getAction()     // Catch:{ URISyntaxException -> 0x00b3 }
            boolean r3 = r3.equals(r4)     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r3 == 0) goto L_0x0020
            android.content.Context r6 = r6.mContext     // Catch:{ URISyntaxException -> 0x00b3 }
            android.content.res.Resources r6 = r6.getResources()     // Catch:{ URISyntaxException -> 0x00b3 }
            int r1 = com.android.settings.C1992R$string.double_press_power_launch_appname_assist     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r6 = r6.getString(r1)     // Catch:{ URISyntaxException -> 0x00b3 }
            return r6
        L_0x0020:
            android.content.Context r6 = r6.mContext     // Catch:{ URISyntaxException -> 0x00b3 }
            android.content.pm.PackageManager r6 = r6.getPackageManager()     // Catch:{ URISyntaxException -> 0x00b3 }
            android.content.pm.ActivityInfo r3 = r2.resolveActivityInfo(r6, r1)     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r3 == 0) goto L_0x003b
            java.lang.CharSequence r3 = r3.loadLabel(r6)     // Catch:{ URISyntaxException -> 0x00b3 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r4 != 0) goto L_0x003b
            java.lang.String r6 = r3.toString()     // Catch:{ URISyntaxException -> 0x00b3 }
            return r6
        L_0x003b:
            boolean r3 = DBG     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r3 == 0) goto L_0x0058
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x00b3 }
            r4.<init>()     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r5 = "Failed to resolve the intent for \""
            r4.append(r5)     // Catch:{ URISyntaxException -> 0x00b3 }
            r4.append(r7)     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r5 = "\" or invalid label. Try using package from intent to get the app label."
            r4.append(r5)     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r4 = r4.toString()     // Catch:{ URISyntaxException -> 0x00b3 }
            android.util.Log.i(r0, r4)     // Catch:{ URISyntaxException -> 0x00b3 }
        L_0x0058:
            java.lang.String r4 = r2.getPackage()     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r4 != 0) goto L_0x0068
            android.content.ComponentName r2 = r2.getComponent()     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r2 == 0) goto L_0x0068
            java.lang.String r4 = r2.getPackageName()     // Catch:{ URISyntaxException -> 0x00b3 }
        L_0x0068:
            if (r4 == 0) goto L_0x00cb
            android.content.pm.ApplicationInfo r1 = r6.getApplicationInfo(r4, r1)     // Catch:{ NameNotFoundException -> 0x009a }
            if (r1 == 0) goto L_0x00cb
            boolean r2 = r1.enabled     // Catch:{ NameNotFoundException -> 0x009a }
            if (r2 == 0) goto L_0x00cb
            java.lang.CharSequence r6 = r6.getApplicationLabel(r1)     // Catch:{ NameNotFoundException -> 0x009a }
            boolean r1 = android.text.TextUtils.isEmpty(r6)     // Catch:{ NameNotFoundException -> 0x009a }
            if (r1 != 0) goto L_0x0083
            java.lang.String r6 = r6.toString()     // Catch:{ NameNotFoundException -> 0x009a }
            return r6
        L_0x0083:
            if (r3 == 0) goto L_0x00cb
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x009a }
            r6.<init>()     // Catch:{ NameNotFoundException -> 0x009a }
            java.lang.String r1 = "App is disabled OR does not have label to be shown in Settings UI. Ignoring for intent : "
            r6.append(r1)     // Catch:{ NameNotFoundException -> 0x009a }
            r6.append(r7)     // Catch:{ NameNotFoundException -> 0x009a }
            java.lang.String r6 = r6.toString()     // Catch:{ NameNotFoundException -> 0x009a }
            android.util.Log.e(r0, r6)     // Catch:{ NameNotFoundException -> 0x009a }
            goto L_0x00cb
        L_0x009a:
            boolean r6 = DBG     // Catch:{ URISyntaxException -> 0x00b3 }
            if (r6 == 0) goto L_0x00cb
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x00b3 }
            r6.<init>()     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r1 = "Package not found. does not have label to be shown in Settings UI. Ignoring for intent : "
            r6.append(r1)     // Catch:{ URISyntaxException -> 0x00b3 }
            r6.append(r7)     // Catch:{ URISyntaxException -> 0x00b3 }
            java.lang.String r6 = r6.toString()     // Catch:{ URISyntaxException -> 0x00b3 }
            android.util.Log.e(r0, r6)     // Catch:{ URISyntaxException -> 0x00b3 }
            goto L_0x00cb
        L_0x00b3:
            boolean r6 = DBG
            if (r6 == 0) goto L_0x00cb
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r1 = "Invalid URI syntax for intent : "
            r6.append(r1)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Log.e(r0, r6)
        L_0x00cb:
            r6 = 0
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.gestures.DoublePressPowerToLaunchAppPreferenceController.getLaunchableAppLabelOrNull(java.lang.String):java.lang.String");
    }

    private void setAppIntent(String str) {
        MotorolaSettings.Secure.putString(this.mContext.getContentResolver(), "pwr_double_tap_app", str);
    }

    private String getAppIntent() {
        return MotorolaSettings.Secure.getString(this.mContext.getContentResolver(), "pwr_double_tap_app");
    }

    private void disableCameraGesture(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", z ? 1 : 0);
    }

    private boolean isCameraGestureDisabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 1;
    }

    private void setSelection(String str) {
        if (CAMERA_APP_INTENT.equals(str)) {
            disableCameraGesture(false);
            setAppIntent("");
            return;
        }
        disableCameraGesture(true);
        setAppIntent(str);
    }

    private String getSelection() {
        String appIntent = getAppIntent();
        if (TextUtils.isEmpty(appIntent)) {
            return isCameraGestureDisabled() ? "" : CAMERA_APP_INTENT;
        }
        return appIntent;
    }

    private void setSummary(ListPreference listPreference, String str) {
        int findIndexOfValue = listPreference.findIndexOfValue(str);
        listPreference.setSummary(findIndexOfValue >= 0 ? listPreference.getEntries()[findIndexOfValue].toString() : "");
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        setSelection(str);
        setSummary((ListPreference) preference, str);
        return true;
    }
}
