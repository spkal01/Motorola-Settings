package com.android.settings.homepage.contextualcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.FaceSetupSlice;
import com.motorola.settings.biometrics.face.FaceUtils;

public class FaceReEnrollDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private FaceManager mFaceManager;
    private int mReEnrollType;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        FaceReEnrollDialog.super.onCreate(bundle);
        if (getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            i = C1992R$string.f67x9a538631;
        } else {
            i = C1992R$string.security_settings_face_enroll_improve_face_alert_body;
        }
        AlertController.AlertParams alertParams = this.mAlertParams;
        alertParams.mTitle = getText(C1992R$string.security_settings_face_enroll_improve_face_alert_title);
        alertParams.mMessage = getText(i);
        alertParams.mPositiveButtonText = getText(C1992R$string.storage_menu_set_up);
        alertParams.mNegativeButtonText = getText(C1992R$string.cancel);
        alertParams.mPositiveButtonListener = this;
        this.mFaceManager = Utils.getFaceManagerOrNull(getApplicationContext());
        this.mReEnrollType = FaceSetupSlice.getReEnrollSetting(getApplicationContext(), getUserId());
        Log.d("FaceReEnrollDialog", "ReEnroll Type : " + this.mReEnrollType);
        int i2 = this.mReEnrollType;
        if (i2 == 1) {
            setupAlert();
        } else if (i2 == 3) {
            removeFaceAndReEnroll();
        } else {
            Log.d("FaceReEnrollDialog", "Error unsupported flow for : " + this.mReEnrollType);
            dismiss();
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        removeFaceAndReEnroll();
    }

    public void removeFaceAndReEnroll() {
        int userId = getUserId();
        FaceManager faceManager = this.mFaceManager;
        if (faceManager == null || !faceManager.hasEnrolledTemplates(userId)) {
            finish();
        }
        this.mFaceManager.remove(new Face("", 0, 0), userId, new FaceManager.RemovalCallback() {
            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                FaceReEnrollDialog.super.onRemovalError(face, i, charSequence);
                FaceReEnrollDialog.this.finish();
            }

            public void onRemovalSucceeded(Face face, int i) {
                Intent intent;
                FaceReEnrollDialog.super.onRemovalSucceeded(face, i);
                if (i == 0) {
                    if (FaceUtils.isMotoFaceUnlock()) {
                        intent = new Intent("com.motorola.intent.action.FACE_ENROLL");
                    } else {
                        intent = new Intent("android.settings.BIOMETRIC_ENROLL");
                    }
                    FaceReEnrollDialog.this.getApplicationContext();
                    try {
                        FaceReEnrollDialog.this.startActivity(intent);
                    } catch (Exception unused) {
                        Log.e("FaceReEnrollDialog", "Failed to startActivity");
                    }
                    FaceReEnrollDialog.this.finish();
                }
            }
        });
    }
}
