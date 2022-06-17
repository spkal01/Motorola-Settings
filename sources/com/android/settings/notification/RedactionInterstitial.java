package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.RestrictedRadioButton;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import com.motorola.settings.security.screenlock.SettingsSetUpActivity;

public class RedactionInterstitial extends SettingsSetUpActivity {
    /* access modifiers changed from: protected */
    public boolean isToolbarEnabled() {
        return false;
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", RedactionInterstitialFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return RedactionInterstitialFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    public static Intent createStartIntent(Context context, int i) {
        int i2;
        Intent intent = new Intent(context, RedactionInterstitial.class);
        if (UserManager.get(context).isManagedProfile(i)) {
            i2 = C1992R$string.lock_screen_notifications_interstitial_title_profile;
        } else {
            i2 = C1992R$string.lock_screen_notifications_interstitial_title;
        }
        return intent.putExtra(":settings:show_fragment_title_resid", i2).putExtra("android.intent.extra.USER_ID", i);
    }

    public static class RedactionInterstitialFragment extends SettingsPreferenceFragment implements RadioGroup.OnCheckedChangeListener {
        private RadioGroup mRadioGroup;
        private RestrictedRadioButton mRedactSensitiveButton;
        private RestrictedRadioButton mShowAllButton;
        private int mUserId;

        public int getMetricsCategory() {
            return 74;
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C1987R$layout.redaction_interstitial, viewGroup, false);
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mRadioGroup = (RadioGroup) view.findViewById(C1985R$id.radio_group);
            this.mShowAllButton = (RestrictedRadioButton) view.findViewById(C1985R$id.show_all);
            this.mRedactSensitiveButton = (RestrictedRadioButton) view.findViewById(C1985R$id.redact_sensitive);
            this.mRadioGroup.setOnCheckedChangeListener(this);
            this.mUserId = Utils.getUserIdFromBundle(getContext(), getActivity().getIntent().getExtras());
            if (UserManager.get(getContext()).isManagedProfile(this.mUserId)) {
                ((TextView) view.findViewById(C1985R$id.sud_layout_description)).setText(C1992R$string.lock_screen_notifications_interstitial_message_profile);
                this.mShowAllButton.setText(C1992R$string.lock_screen_notifications_summary_show_profile);
                this.mRedactSensitiveButton.setText(C1992R$string.lock_screen_notifications_summary_hide_profile);
                ((RadioButton) view.findViewById(C1985R$id.hide_all)).setVisibility(8);
            }
            ((FooterBarMixin) ((GlifLayout) view.findViewById(C1985R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(getContext()).setText(C1992R$string.app_notifications_dialog_done).setListener(new C1104x56ed0641(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
        }

        /* access modifiers changed from: private */
        public void onDoneButtonClicked(View view) {
            if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
                SetupRedactionInterstitial.setEnabled(getContext(), false);
            }
            RedactionInterstitial redactionInterstitial = (RedactionInterstitial) getActivity();
            if (redactionInterstitial != null) {
                redactionInterstitial.setResult(0, (Intent) null);
                finish();
            }
        }

        public void onResume() {
            super.onResume();
            checkNotificationFeaturesAndSetDisabled(this.mShowAllButton, 12);
            checkNotificationFeaturesAndSetDisabled(this.mRedactSensitiveButton, 4);
            loadFromSettings();
        }

        private void checkNotificationFeaturesAndSetDisabled(RestrictedRadioButton restrictedRadioButton, int i) {
            restrictedRadioButton.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(getActivity(), i, this.mUserId));
        }

        private void loadFromSettings() {
            boolean z = false;
            boolean z2 = UserManager.get(getContext()).isManagedProfile(this.mUserId) || Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, this.mUserId) != 0;
            if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", 1, this.mUserId) != 0) {
                z = true;
            }
            int i = C1985R$id.hide_all;
            if (z2) {
                if (z && !this.mShowAllButton.isDisabledByAdmin()) {
                    i = C1985R$id.show_all;
                } else if (!this.mRedactSensitiveButton.isDisabledByAdmin()) {
                    i = C1985R$id.redact_sensitive;
                }
            }
            this.mRadioGroup.check(i);
        }

        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int i2 = 1;
            int i3 = i == C1985R$id.show_all ? 1 : 0;
            if (i == C1985R$id.hide_all) {
                i2 = 0;
            }
            Settings.Secure.putIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", i3, this.mUserId);
            Settings.Secure.putIntForUser(getContentResolver(), "lock_screen_show_notifications", i2, this.mUserId);
        }
    }
}
