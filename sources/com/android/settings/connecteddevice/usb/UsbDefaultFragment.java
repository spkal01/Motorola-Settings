package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.TetheringManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class UsbDefaultFragment extends RadioButtonPickerFragment {
    long mCurrentFunctions;
    Handler mHandler;
    private boolean mIsConnected = false;
    boolean mIsStartTethering = false;
    OnStartTetheringCallback mOnStartTetheringCallback = new OnStartTetheringCallback();
    long mPreviousFunctions;
    TetheringManager mTetheringManager;
    UsbBackend mUsbBackend;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbDefaultFragment$$ExternalSyntheticLambda0(this);
    private UsbConnectionBroadcastReceiver mUsbReceiver;

    public int getMetricsCategory() {
        return 1312;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2) {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        Log.d("UsbDefaultFragment", "UsbConnectionListener() connected : " + z + ", functions : " + j + ", defaultFunctions : " + defaultUsbFunctions + ", mIsStartTethering : " + this.mIsStartTethering);
        if (z && !this.mIsConnected && ((defaultUsbFunctions == 32 || defaultUsbFunctions == 1024) && !this.mIsStartTethering)) {
            this.mCurrentFunctions = defaultUsbFunctions;
            startTethering();
        }
        if (this.mIsStartTethering && z) {
            this.mCurrentFunctions = j;
            refresh(j);
            this.mUsbBackend.setDefaultUsbFunctions(j);
            this.mIsStartTethering = false;
        }
        this.mIsConnected = z;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUsbBackend = new UsbBackend(context);
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mHandler = new Handler(context.getMainLooper());
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        this.mCurrentFunctions = this.mUsbBackend.getDefaultUsbFunctions();
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        getPreferenceScreen().addPreference(new FooterPreference.Builder(getActivity()).setTitle(C1992R$string.usb_default_info).build());
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.usb_default_fragment;
    }

    /* access modifiers changed from: protected */
    public List<? extends CandidateInfo> getCandidates() {
        ArrayList newArrayList = Lists.newArrayList();
        for (Long longValue : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue2 = longValue.longValue();
            final String string = getContext().getString(UsbDetailsFunctionsController.FUNCTIONS_MAP.get(Long.valueOf(longValue2)).intValue());
            final String usbFunctionsToString = UsbBackend.usbFunctionsToString(longValue2);
            if (this.mUsbBackend.areFunctionsSupported(longValue2)) {
                newArrayList.add(new CandidateInfo(true) {
                    public Drawable loadIcon() {
                        return null;
                    }

                    public CharSequence loadLabel() {
                        return string;
                    }

                    public String getKey() {
                        return usbFunctionsToString;
                    }
                });
            }
        }
        return newArrayList;
    }

    /* access modifiers changed from: protected */
    public String getDefaultKey() {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        if (defaultUsbFunctions == 1024) {
            defaultUsbFunctions = 32;
        }
        return UsbBackend.usbFunctionsToString(defaultUsbFunctions);
    }

    /* access modifiers changed from: protected */
    public boolean setDefaultKey(String str) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(str);
        this.mPreviousFunctions = this.mUsbBackend.getCurrentFunctions();
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        if (usbFunctionsFromString == 32 || usbFunctionsFromString == 1024) {
            this.mCurrentFunctions = usbFunctionsFromString;
            startTethering();
            return true;
        }
        this.mIsStartTethering = false;
        this.mCurrentFunctions = usbFunctionsFromString;
        this.mUsbBackend.setDefaultUsbFunctions(usbFunctionsFromString);
        return true;
    }

    private void startTethering() {
        Log.d("UsbDefaultFragment", "startTethering()");
        this.mIsStartTethering = true;
        this.mTetheringManager.startTethering(1, new HandlerExecutor(this.mHandler), this.mOnStartTetheringCallback);
    }

    public void onPause() {
        super.onPause();
        this.mUsbBackend.setDefaultUsbFunctions(this.mCurrentFunctions);
    }

    final class OnStartTetheringCallback implements TetheringManager.StartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringStarted() {
            Log.d("UsbDefaultFragment", "onTetheringStarted()");
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mCurrentFunctions);
        }

        public void onTetheringFailed(int i) {
            Log.w("UsbDefaultFragment", "onTetheringFailed() error : " + i);
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mPreviousFunctions);
            UsbDefaultFragment.this.updateCandidates();
        }
    }

    private void refresh(long j) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (Long longValue : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue2 = longValue.longValue();
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(UsbBackend.usbFunctionsToString(longValue2));
            if (radioButtonPreference != null) {
                boolean areFunctionsSupported = this.mUsbBackend.areFunctionsSupported(longValue2);
                radioButtonPreference.setEnabled(areFunctionsSupported);
                if (areFunctionsSupported) {
                    boolean z = true;
                    if (j == 1024) {
                        if (32 != longValue2) {
                            z = false;
                        }
                        radioButtonPreference.setChecked(z);
                    } else {
                        if (j != longValue2) {
                            z = false;
                        }
                        radioButtonPreference.setChecked(z);
                    }
                }
            }
        }
    }
}
