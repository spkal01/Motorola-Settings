package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupWindow;
import androidx.lifecycle.Lifecycle;
import com.android.settings.C1981R$color;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.C1994R$xml;
import com.android.settings.SubSettings;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class FODAnimationSettings extends SubSettings {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (FingerprintUtils.isMotoFODEnabled(this)) {
            FingerprintUtils.setDarkThemeToolBar(this);
            setTitle(C1992R$string.fingerprint_fod_animation_settings);
            return;
        }
        finish();
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FingerprintAnimationFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return FingerprintAnimationFragment.class.getName().equals(str);
    }

    public void setTheme(int i) {
        super.setTheme(C1993R$style.Theme_SettingsBase_Dark);
    }

    public static class FingerprintAnimationFragment extends DashboardFragment {
        private final String TAG = FingerprintAnimationFragment.class.getSimpleName();
        protected Lifecycle mLifecycle = null;
        private PopupWindow mPopup;

        public int getMetricsCategory() {
            return 3001;
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            super.onCreateOptionsMenu(menu, menuInflater);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).getIcon().setColorFilter(getResources().getColor(C1981R$color.fod_fingerprint_title_color), PorterDuff.Mode.SRC_ATOP);
            }
        }

        /* access modifiers changed from: protected */
        public int getPreferenceScreenResId() {
            return C1994R$xml.fingerprint_fod_animation_settings;
        }

        /* access modifiers changed from: protected */
        public String getLogTag() {
            return this.TAG;
        }

        public int getHelpResource() {
            return C1992R$string.help_url_fingerprint_fod_animation;
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            FODAnimationView fODAnimationView = new FODAnimationView(getContext(), (AttributeSet) null);
            view.post(new C1903x75036597(this, fODAnimationView));
            new FODAnimationViewController(fODAnimationView).init(getLifecycle());
        }

        public void onDestroy() {
            super.onDestroy();
            PopupWindow popupWindow = this.mPopup;
            if (popupWindow != null && popupWindow.isShowing()) {
                this.mPopup.dismiss();
            }
        }

        /* access modifiers changed from: protected */
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            Lifecycle lifecycle = getLifecycle();
            arrayList.add(new FODAnimationRadioPreferenceController(context, lifecycle, FODAnimationRadioPreferenceController.KEY_STYLE_1, 0));
            arrayList.add(new FODAnimationRadioPreferenceController(context, lifecycle, FODAnimationRadioPreferenceController.KEY_STYLE_2, 1));
            arrayList.add(new FODAnimationRadioPreferenceController(context, lifecycle, FODAnimationRadioPreferenceController.KEY_STYLE_3, 2));
            return arrayList;
        }

        /* access modifiers changed from: private */
        /* renamed from: addPopupWindow */
        public void lambda$onViewCreated$0(View view) {
            Context context = getContext();
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(17105610);
            int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(17105609);
            PopupWindow popupWindow = new PopupWindow(view, dimensionPixelSize, dimensionPixelSize2 + (((dimensionPixelSize2 - context.getResources().getDimensionPixelSize(17105617)) - context.getResources().getDimensionPixelSize(17105362)) / 2), false);
            this.mPopup = popupWindow;
            popupWindow.setContentView(view);
            this.mPopup.showAtLocation(view, 81, 0, 0);
        }
    }
}
