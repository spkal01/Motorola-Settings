package com.android.settings.applications;

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

public class ConvertToFbe extends InstrumentedFragment {
    public int getMetricsCategory() {
        return 402;
    }

    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(C1992R$string.convert_to_file_encryption)).show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C1992R$string.convert_to_file_encryption);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C1987R$layout.convert_fbe, (ViewGroup) null);
        ((Button) inflate.findViewById(C1985R$id.button_convert_fbe)).setOnClickListener(new ConvertToFbe$$ExternalSyntheticLambda0(this));
        return inflate;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        if (!runKeyguardConfirmation(55)) {
            convert();
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55 && i2 == -1) {
            convert();
        }
    }

    private void convert() {
        new SubSettingLauncher(getContext()).setDestination(ConfirmConvertToFbe.class.getName()).setTitleRes(C1992R$string.convert_to_file_encryption).setSourceMetricsCategory(getMetricsCategory()).launch();
    }
}
