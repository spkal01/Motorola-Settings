package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import com.android.settings.C1992R$string;
import com.android.settings.widget.PrimarySwitchPreference;
import java.time.LocalTime;

public class DarkModePreference extends PrimarySwitchPreference {
    private Runnable mCallback;
    private DarkModeObserver mDarkModeObserver;
    private TimeFormatter mFormat;
    private PowerManager mPowerManager;
    private UiModeManager mUiModeManager;

    public DarkModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDarkModeObserver = new DarkModeObserver(context);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mFormat = new TimeFormatter(context);
        DarkModePreference$$ExternalSyntheticLambda0 darkModePreference$$ExternalSyntheticLambda0 = new DarkModePreference$$ExternalSyntheticLambda0(this);
        this.mCallback = darkModePreference$$ExternalSyntheticLambda0;
        this.mDarkModeObserver.subscribe(darkModePreference$$ExternalSyntheticLambda0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z = (getContext().getResources().getConfiguration().uiMode & 32) != 0;
        setSwitchEnabled(!isPowerSaveMode);
        updateSummary(isPowerSaveMode, z);
    }

    public void onAttached() {
        super.onAttached();
        this.mDarkModeObserver.subscribe(this.mCallback);
    }

    public void onDetached() {
        super.onDetached();
        this.mDarkModeObserver.unsubscribe();
    }

    private void updateSummary(boolean z, boolean z2) {
        String str;
        int i;
        LocalTime localTime;
        int i2;
        int i3;
        int i4;
        if (z) {
            if (z2) {
                i4 = C1992R$string.dark_ui_mode_disabled_summary_dark_theme_on;
            } else {
                i4 = C1992R$string.dark_ui_mode_disabled_summary_dark_theme_off;
            }
            setSummary((CharSequence) getContext().getString(i4));
            return;
        }
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            Context context = getContext();
            if (z2) {
                i3 = C1992R$string.dark_ui_summary_on_auto_mode_auto;
            } else {
                i3 = C1992R$string.dark_ui_summary_off_auto_mode_auto;
            }
            str = context.getString(i3);
        } else if (nightMode == 3) {
            if (z2) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            String of = this.mFormat.mo12096of(localTime);
            Context context2 = getContext();
            if (z2) {
                i2 = C1992R$string.dark_ui_summary_on_auto_mode_custom;
            } else {
                i2 = C1992R$string.dark_ui_summary_off_auto_mode_custom;
            }
            str = context2.getString(i2, new Object[]{of});
        } else {
            Context context3 = getContext();
            if (z2) {
                i = C1992R$string.dark_ui_summary_on_auto_mode_never;
            } else {
                i = C1992R$string.dark_ui_summary_off_auto_mode_never;
            }
            str = context3.getString(i);
        }
        setSummary((CharSequence) str);
    }
}
