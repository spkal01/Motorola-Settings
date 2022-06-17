package com.motorola.settings.biometrics.face;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.android.settings.biometrics.face.FaceEnrollIntroduction;

public class MotoFaceEnrollActivity extends Activity {
    private byte[] mToken;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PackageManager packageManager = getApplicationContext().getPackageManager();
        boolean booleanExtra = getIntent().getBooleanExtra("for_redo", false);
        if (this.mToken == null) {
            this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        }
        if (packageManager.hasSystemFeature("android.hardware.biometrics.face") && FaceUtils.isMotoFaceUnlock()) {
            Intent faceIntroIntent = getFaceIntroIntent();
            faceIntroIntent.putExtra("for_redo", booleanExtra);
            faceIntroIntent.putExtra("hw_auth_token", this.mToken);
            if (getCallingActivity() != null) {
                faceIntroIntent.setFlags(33554432);
            }
            startActivity(faceIntroIntent);
        }
        finish();
    }

    private Intent getFaceIntroIntent() {
        Intent intent = new Intent(this, FaceEnrollIntroduction.class);
        intent.addFlags(268468224);
        return intent;
    }
}
