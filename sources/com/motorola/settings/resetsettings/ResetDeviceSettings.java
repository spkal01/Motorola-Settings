package com.motorola.settings.resetsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockSettingsHelper;

public class ResetDeviceSettings extends InstrumentedFragment {
    private View mContentView;
    private Button mInitiateButton;
    private final View.OnClickListener mInitiateListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (!ResetDeviceSettings.this.runKeyguardConfirmation(55)) {
                ResetDeviceSettings.this.showFinalConfirmation();
            }
        }
    };

    public int getMetricsCategory() {
        return 83;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C1992R$string.reset_device_settings_title);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55) {
            if (i2 == -1) {
                showFinalConfirmation();
            } else {
                establishInitialState();
            }
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mContentView = layoutInflater.inflate(C1987R$layout.reset_device_settings, (ViewGroup) null);
        establishInitialState();
        return this.mContentView;
    }

    /* access modifiers changed from: package-private */
    public void showFinalConfirmation() {
        new SubSettingLauncher(getContext()).setDestination(ResetDeviceSettingsConfirm.class.getName()).setTitleRes(C1992R$string.reset_device_settings_title).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    /* access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(C1992R$string.reset_device_settings_title)).show();
    }

    private void establishInitialState() {
        Button button = (Button) this.mContentView.findViewById(C1985R$id.initiate_reset_device_settings);
        this.mInitiateButton = button;
        button.setOnClickListener(this.mInitiateListener);
    }
}
