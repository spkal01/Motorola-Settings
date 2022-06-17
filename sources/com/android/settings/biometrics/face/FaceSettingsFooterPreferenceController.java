package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
import com.motorola.settings.biometrics.face.FaceUtils;

public class FaceSettingsFooterPreferenceController extends BasePreferenceController {
    private static final String ANNOTATION_URL = "url";
    private FaceFeatureProvider mProvider;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 1;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    public FaceSettingsFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mProvider = FeatureFactory.getFactory(context).getFaceFeatureProvider();
    }

    public void updateState(Preference preference) {
        int i;
        super.updateState(preference);
        Context context = this.mContext;
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo(this.mContext, ANNOTATION_URL, HelpUtils.getHelpIntent(context, context.getString(C1992R$string.help_url_face), getClass().getName()));
        if (FaceUtils.isMotoFaceUnlock()) {
            i = C1992R$string.security_settings_face_settings_footer_moto;
        } else if (this.mProvider.isAttentionSupported(this.mContext)) {
            i = C1992R$string.security_settings_face_settings_footer;
        } else {
            i = C1992R$string.security_settings_face_settings_footer_attention_not_supported;
        }
        preference.setTitle(AnnotationSpan.linkify(this.mContext.getText(i), linkInfo));
    }
}
