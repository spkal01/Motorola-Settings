package com.motorola.settings.security.screenlock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;

public class PrivacySpaceRedactionInterstitial extends SettingsSetUpActivity {
    /* access modifiers changed from: protected */
    public boolean isToolbarEnabled() {
        return false;
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", PrivacySpaceRedactionInterstitialFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return PrivacySpaceRedactionInterstitialFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    public static Intent createStartIntent(Context context) {
        return new Intent(context, PrivacySpaceRedactionInterstitial.class);
    }

    public static class PrivacySpaceRedactionInterstitialFragment extends SettingsPreferenceFragment implements RadioGroup.OnCheckedChangeListener {
        boolean enabled;
        private RadioGroup mRadioGroup;
        private final Intent resultData = new Intent();
        boolean show;

        public int getMetricsCategory() {
            return 74;
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C1987R$layout.redaction_interstitial, viewGroup, false);
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(C1985R$id.radio_group);
            this.mRadioGroup = radioGroup;
            radioGroup.setOnCheckedChangeListener(this);
            GlifLayout glifLayout = (GlifLayout) view.findViewById(C1985R$id.setup_wizard_layout);
            glifLayout.setHeaderText(C1992R$string.lock_settings_privacy_space_notification_title);
            ((FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(getContext()).setText(C1992R$string.next_label).setListener(new C1964x31933ec5(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
        }

        /* access modifiers changed from: private */
        public void onDoneButtonClicked(View view) {
            Log.d("PrivacySpaceRedactionInterstitial", "RadioGroup checked,show:" + this.show + ",enabled:" + this.enabled);
            this.resultData.putExtra("lock_screen_allow_private_notifications", this.show ? 1 : 0);
            this.resultData.putExtra("lock_screen_show_notifications", this.enabled ? 1 : 0);
            getActivity().setResult(1, this.resultData);
            getActivity().finish();
        }

        public void onResume() {
            super.onResume();
            this.mRadioGroup.check(C1985R$id.hide_all);
        }

        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            boolean z = true;
            this.show = i == C1985R$id.show_all;
            if (i == C1985R$id.hide_all) {
                z = false;
            }
            this.enabled = z;
        }
    }
}
