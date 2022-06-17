package com.motorola.settings.biometrics.face;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class FaceSettingsRedoPreferenceController extends BasePreferenceController implements Preference.OnPreferenceClickListener {
    static final String KEY = "security_settings_face_redo_face_scan";
    private static final String TAG = "FaceSettings/Redo";
    private SettingsActivity mActivity;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final FaceManager mFaceManager;
    private final FaceManager.RemovalCallback mRemovalCallback;
    /* access modifiers changed from: private */
    public byte[] mToken;
    private int mUserId;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY;
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsRedoPreferenceController(Context context, String str) {
        super(context, str);
        this.mRemovalCallback = new FaceManager.RemovalCallback() {
            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                Log.e(FaceSettingsRedoPreferenceController.TAG, "Unable to remove face: " + face.getBiometricId() + " error: " + i + " " + charSequence);
                Toast.makeText(FaceSettingsRedoPreferenceController.this.mContext, charSequence, 0).show();
            }

            public void onRemovalSucceeded(Face face, int i) {
                if (i == 0) {
                    Log.v(FaceSettingsRedoPreferenceController.TAG, "onRemovalSucceeded ");
                    Intent intent = new Intent("com.motorola.intent.action.FACE_ENROLL");
                    intent.putExtra("for_face", true);
                    intent.putExtra("for_redo", true);
                    intent.putExtra("hw_auth_token", FaceSettingsRedoPreferenceController.this.mToken);
                    intent.addFlags(268435456);
                    FaceSettingsRedoPreferenceController.this.mContext.startActivity(intent);
                    return;
                }
                Log.v(FaceSettingsRedoPreferenceController.TAG, "Remaining: " + i);
            }
        };
        this.mContext = context;
        this.mFaceManager = (FaceManager) context.getSystemService(FaceManager.class);
    }

    public FaceSettingsRedoPreferenceController(Context context) {
        this(context, KEY);
    }

    public void setUserId(int i) {
        this.mUserId = i;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setOnPreferenceClickListener(this);
    }

    public int getAvailabilityStatus() {
        return FaceUtils.isMotoFaceUnlock() ? 0 : 2;
    }

    public boolean onPreferenceClick(Preference preference) {
        showFaceRedoWarningDialog();
        return true;
    }

    public void setActivity(SettingsActivity settingsActivity) {
        this.mActivity = settingsActivity;
    }

    /* access modifiers changed from: private */
    public void deleteFace() {
        List enrolledFaces = this.mFaceManager.getEnrolledFaces(this.mUserId);
        if (enrolledFaces.isEmpty()) {
            Log.e(TAG, "No faces");
            return;
        }
        if (enrolledFaces.size() > 1) {
            Log.e(TAG, "Multiple enrollments: " + enrolledFaces.size());
        }
        this.mFaceManager.remove((Face) enrolledFaces.get(0), this.mUserId, this.mRemovalCallback);
    }

    /* access modifiers changed from: package-private */
    public void showFaceRedoWarningDialog() {
        new AlertDialog.Builder(this.mActivity).setTitle(C1992R$string.security_settings_face_unlock_redo_face_scan_title).setMessage(C1992R$string.face_redo_warning_msg).setPositiveButton(C1992R$string.face_redo_confirm_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FaceSettingsRedoPreferenceController.this.deleteFace();
            }
        }).setNegativeButton(C1992R$string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
            }
        }).show();
    }

    public void setToken(byte[] bArr) {
        this.mToken = bArr;
    }
}
