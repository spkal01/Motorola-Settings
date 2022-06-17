package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.internal.util.Preconditions;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.password.ChooseLockSettingsHelper;

public class StorageWizardMoveConfirm extends StorageWizardBase {
    private ApplicationInfo mApp;
    private String mPackageName;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mVolume == null) {
            finish();
            return;
        }
        setContentView(C1987R$layout.storage_wizard_generic);
        try {
            this.mPackageName = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
            this.mApp = getPackageManager().getApplicationInfo(this.mPackageName, 0);
            Preconditions.checkState(getPackageManager().getPackageCandidateVolumes(this.mApp).contains(this.mVolume));
            String charSequence = getPackageManager().getApplicationLabel(this.mApp).toString();
            String bestVolumeDescription = this.mStorage.getBestVolumeDescription(this.mVolume);
            setIcon(C1983R$drawable.ic_swap_horiz);
            setHeaderText(C1992R$string.storage_wizard_move_confirm_title, charSequence);
            setBodyText(C1992R$string.storage_wizard_move_confirm_body, charSequence, bestVolumeDescription);
            setNextButtonText(C1992R$string.move_app, new CharSequence[0]);
            setBackButtonVisibility(4);
        } catch (PackageManager.NameNotFoundException unused) {
            finish();
        }
    }

    public void onNavigateNext(View view) {
        if (StorageManager.isFileEncryptedNativeOrEmulated()) {
            for (UserInfo userInfo : ((UserManager) getSystemService(UserManager.class)).getUsers()) {
                if (!StorageManager.isUserKeyUnlocked(userInfo.id)) {
                    Log.d("StorageWizardMoveConfirm", "User " + userInfo.id + " is currently locked; requesting unlock");
                    new ChooseLockSettingsHelper.Builder(this).setRequestCode(100).setDescription(TextUtils.expandTemplate(getText(C1992R$string.storage_wizard_move_unlock), new CharSequence[]{userInfo.name})).setUserId(userInfo.id).setForceVerifyPath(true).setAllowAnyUserId(true).show();
                    return;
                }
            }
        }
        String charSequence = getPackageManager().getApplicationLabel(this.mApp).toString();
        int movePackage = getPackageManager().movePackage(this.mPackageName, this.mVolume);
        Intent intent = new Intent(this, StorageWizardMoveProgress.class);
        intent.putExtra("android.content.pm.extra.MOVE_ID", movePackage);
        intent.putExtra("android.intent.extra.TITLE", charSequence);
        intent.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
        startActivity(intent);
        finishAffinity();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 100) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == -1) {
            onNavigateNext((View) null);
        } else {
            Log.w("StorageWizardMoveConfirm", "Failed to confirm credentials");
        }
    }
}
