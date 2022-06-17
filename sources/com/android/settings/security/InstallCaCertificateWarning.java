package com.android.settings.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;

public class InstallCaCertificateWarning extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        setContentView(C1987R$layout.ca_certificate_warning_dialog);
        getWindow().addSystemFlags(524288);
        GlifLayout glifLayout = (GlifLayout) findViewById(C1985R$id.setup_wizard_layout);
        glifLayout.setHeaderText(C1992R$string.ca_certificate_warning_title);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(C1992R$string.certificate_warning_install_anyway).setListener(installCaCertificate()).setButtonType(0).setTheme(C1993R$style.SudGlifButton_Secondary).build());
        footerBarMixin.getSecondaryButtonView().setFilterTouchesWhenObscured(true);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.certificate_warning_dont_install).setListener(returnToInstallCertificateFromStorage()).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
        footerBarMixin.getPrimaryButtonView().setFilterTouchesWhenObscured(true);
    }

    private View.OnClickListener installCaCertificate() {
        return new InstallCaCertificateWarning$$ExternalSyntheticLambda1(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$installCaCertificate$0(View view) {
        Intent intent = new Intent();
        intent.setAction("android.credentials.INSTALL");
        intent.putExtra("certificate_install_usage", "ca");
        startActivity(intent);
        finish();
    }

    private View.OnClickListener returnToInstallCertificateFromStorage() {
        return new InstallCaCertificateWarning$$ExternalSyntheticLambda0(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$returnToInstallCertificateFromStorage$1(View view) {
        Toast.makeText(this, C1992R$string.cert_not_installed, 0).show();
        finish();
    }
}
