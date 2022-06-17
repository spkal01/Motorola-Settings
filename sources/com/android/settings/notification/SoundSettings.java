package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.preference.SeekBarVolumizer;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.VolumeSeekBarPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.sound.HandsFreeProfileOutputPreferenceController;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.UpdatableListPreferenceDialogFragment;
import com.motorola.settings.notification.DialPadToneLengthPreferenceController;
import com.motorola.settings.notification.ExtNotificationVolumePreferenceController;
import com.motorola.settings.notification.PhoneRingtone2PreferenceController;
import com.motorola.settings.relativevolume.ExtMediaVolumePreferenceController;
import com.motorola.settings.relativevolume.RelativeVolumePreference;
import com.motorola.settings.relativevolume.RelativeVolumePreferenceController;
import com.motorola.settings.relativevolume.RelativeVolumeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SoundSettings extends DashboardFragment implements OnActivityResultListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.sound_settings) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SoundSettings.buildPreferenceControllers(context, (SoundSettings) null, (Lifecycle) null);
        }
    };
    static final int STOP_SAMPLE = 1;
    private UpdatableListPreferenceDialogFragment mDialogFragment;
    final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                SoundSettings.this.mVolumeCallback.stopSample();
            }
        }
    };
    private String mHfpOutputControllerKey;
    /* access modifiers changed from: private */
    public RelativeVolumePreference mRelativeVolumePreference;
    private RingtonePreference mRequestPreference;
    private String mVibrationPreferencesKey = "vibration_preference_screen";
    final VolumePreferenceCallback mVolumeCallback = new VolumePreferenceCallback();

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "SoundSettings";
    }

    public int getMetricsCategory() {
        return 336;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            String string = bundle.getString("selected_preference", (String) null);
            if (!TextUtils.isEmpty(string)) {
                this.mRequestPreference = (RingtonePreference) findPreference(string);
            }
            this.mDialogFragment = (UpdatableListPreferenceDialogFragment) getFragmentManager().findFragmentByTag("SoundSettings");
        }
        if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(getContext())) {
            RelativeVolumePreference relativeVolumePreference = (RelativeVolumePreference) findPreference(RelativeVolumePreferenceController.KEY_RELATIVE_VOLUME);
            this.mRelativeVolumePreference = relativeVolumePreference;
            if (relativeVolumePreference == null) {
                Log.d("SoundSettings", "cannot find RelativeVolumePreference");
            } else {
                Log.d("SoundSettings", " find RelativeVolumePreference");
            }
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_sound;
    }

    public void onPause() {
        super.onPause();
        this.mVolumeCallback.stopSample();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof RingtonePreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        writePreferenceClickMetric(preference);
        RingtonePreference ringtonePreference = (RingtonePreference) preference;
        this.mRequestPreference = ringtonePreference;
        ringtonePreference.onPrepareRingtonePickerIntent(ringtonePreference.getIntent());
        getActivity().startActivityForResultAsUser(this.mRequestPreference.getIntent(), 200, (Bundle) null, UserHandle.of(this.mRequestPreference.getUserId()));
        return true;
    }

    public void onDisplayPreferenceDialog(Preference preference) {
        if (TextUtils.equals(this.mVibrationPreferencesKey, preference.getKey())) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        UpdatableListPreferenceDialogFragment newInstance = UpdatableListPreferenceDialogFragment.newInstance(preference.getKey(), this.mHfpOutputControllerKey.equals(preference.getKey()) ? 1416 : 0);
        this.mDialogFragment = newInstance;
        newInstance.setTargetFragment(this, 0);
        this.mDialogFragment.show(getFragmentManager(), "SoundSettings");
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.sound_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }

    public void onAttach(Context context) {
        Class cls = HandsFreeProfileOutputPreferenceController.class;
        super.onAttach(context);
        ArrayList arrayList = new ArrayList();
        arrayList.add((VolumeSeekBarPreferenceController) use(AlarmVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(ExtMediaVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(RingVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(ExtNotificationVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(CallVolumePreferenceController.class));
        ((HandsFreeProfileOutputPreferenceController) use(cls)).setCallback(new SoundSettings$$ExternalSyntheticLambda0(this));
        this.mHfpOutputControllerKey = ((HandsFreeProfileOutputPreferenceController) use(cls)).getPreferenceKey();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            VolumeSeekBarPreferenceController volumeSeekBarPreferenceController = (VolumeSeekBarPreferenceController) it.next();
            volumeSeekBarPreferenceController.setCallback(this.mVolumeCallback);
            getSettingsLifecycle().addObserver(volumeSeekBarPreferenceController);
        }
        if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(context)) {
            getSettingsLifecycle().addObserver((LifecycleObserver) use(RelativeVolumePreferenceController.class));
        }
    }

    final class VolumePreferenceCallback implements VolumeSeekBarPreference.Callback {
        private SeekBarVolumizer mCurrent;

        VolumePreferenceCallback() {
        }

        public void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
            if (this.mCurrent != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }

        public void onStreamValueChanged(int i, int i2, boolean z) {
            if (this.mCurrent != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000);
            }
            if (i == 3) {
                if (RelativeVolumeUtils.DEBUG) {
                    Log.d("SoundSettings", "onStreamValueChanged master volume change to : " + i2 + " fromTouch: " + z);
                }
                Context access$000 = SoundSettings.this.getPrefContext();
                if (access$000 != null && RelativeVolumeUtils.isRelativeVolumeFeatureOn(access$000)) {
                    Log.d("SoundSettings", "updateWhenMediaVolumeChange master volume change to : " + i2);
                    if (SoundSettings.this.mRelativeVolumePreference != null && z) {
                        SoundSettings.this.mRelativeVolumePreference.updateWhenMediaVolumeChange(i2);
                    }
                }
            }
        }

        public void onTouchStateChanged(boolean z) {
            if (RelativeVolumeUtils.DEBUG) {
                Log.d("SoundSettings", "onTouchStateChanged: " + z);
            }
            Context access$200 = SoundSettings.this.getPrefContext();
            if (access$200 != null && RelativeVolumeUtils.isRelativeVolumeFeatureOn(access$200) && SoundSettings.this.mRelativeVolumePreference != null) {
                SoundSettings.this.mRelativeVolumePreference.updateMasterSeekBarState(z);
            }
        }

        public void onStartTrackingTouch(SeekBarVolumizer seekBarVolumizer) {
            SeekBarVolumizer seekBarVolumizer2 = this.mCurrent;
            if (!(seekBarVolumizer2 == null || seekBarVolumizer2 == seekBarVolumizer)) {
                seekBarVolumizer2.stopSample();
            }
            this.mCurrent = seekBarVolumizer;
        }

        public void stopSample() {
            SeekBarVolumizer seekBarVolumizer = this.mCurrent;
            if (seekBarVolumizer != null) {
                seekBarVolumizer.stopSample();
            }
        }
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SoundSettings soundSettings, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PhoneRingtonePreferenceController(context));
        arrayList.add(new PhoneRingtone2PreferenceController(context));
        arrayList.add(new AlarmRingtonePreferenceController(context));
        arrayList.add(new NotificationRingtonePreferenceController(context));
        DialPadTonePreferenceController dialPadTonePreferenceController = new DialPadTonePreferenceController(context, soundSettings, lifecycle);
        ScreenLockSoundPreferenceController screenLockSoundPreferenceController = new ScreenLockSoundPreferenceController(context, soundSettings, lifecycle);
        ChargingSoundPreferenceController chargingSoundPreferenceController = new ChargingSoundPreferenceController(context, soundSettings, lifecycle);
        DockingSoundPreferenceController dockingSoundPreferenceController = new DockingSoundPreferenceController(context, soundSettings, lifecycle);
        TouchSoundPreferenceController touchSoundPreferenceController = new TouchSoundPreferenceController(context, soundSettings, lifecycle);
        VibrateOnTouchPreferenceController vibrateOnTouchPreferenceController = new VibrateOnTouchPreferenceController(context, soundSettings, lifecycle);
        DockAudioMediaPreferenceController dockAudioMediaPreferenceController = new DockAudioMediaPreferenceController(context, soundSettings, lifecycle);
        BootSoundPreferenceController bootSoundPreferenceController = new BootSoundPreferenceController(context);
        EmergencyTonePreferenceController emergencyTonePreferenceController = new EmergencyTonePreferenceController(context, soundSettings, lifecycle);
        arrayList.add(new DialPadToneLengthPreferenceController(context, lifecycle));
        arrayList.add(dialPadTonePreferenceController);
        arrayList.add(screenLockSoundPreferenceController);
        arrayList.add(chargingSoundPreferenceController);
        arrayList.add(dockingSoundPreferenceController);
        arrayList.add(touchSoundPreferenceController);
        arrayList.add(vibrateOnTouchPreferenceController);
        arrayList.add(dockAudioMediaPreferenceController);
        arrayList.add(bootSoundPreferenceController);
        arrayList.add(emergencyTonePreferenceController);
        arrayList.add(new PreferenceCategoryController(context, "other_sounds_and_vibrations_category").setChildren(Arrays.asList(new AbstractPreferenceController[]{dialPadTonePreferenceController, screenLockSoundPreferenceController, chargingSoundPreferenceController, dockingSoundPreferenceController, touchSoundPreferenceController, vibrateOnTouchPreferenceController, dockAudioMediaPreferenceController, bootSoundPreferenceController, emergencyTonePreferenceController})));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: onPreferenceDataChanged */
    public void lambda$onAttach$0(ListPreference listPreference) {
        UpdatableListPreferenceDialogFragment updatableListPreferenceDialogFragment = this.mDialogFragment;
        if (updatableListPreferenceDialogFragment != null) {
            updatableListPreferenceDialogFragment.onListPreferenceUpdated(listPreference);
        }
    }
}
