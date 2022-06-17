package com.android.settings.core;

import android.R;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C1977R$anim;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1993R$style;
import com.android.settings.SubSettings;
import com.android.settings.core.CategoryMixin;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.resources.TextAppearanceConfig;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import com.motorola.settings.display.edge.SettingsBaseEndlessLayoutMixin;

public class SettingsBaseActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    protected AppBarLayout mAppBarLayout;
    protected CategoryMixin mCategoryMixin;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    /* access modifiers changed from: protected */
    public boolean isToolbarEnabled() {
        return true;
    }

    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        if (isLockTaskModePinned() && !isSettingsRunOnTop()) {
            Log.w("SettingsBaseActivity", "Devices lock task mode pinned.");
            finish();
        }
        System.currentTimeMillis();
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        getLifecycle().addObserver(new SettingsBaseEndlessLayoutMixin(this));
        TextAppearanceConfig.setShouldLoadFontSynchronously(true);
        this.mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(this.mCategoryMixin);
        if (!getTheme().obtainStyledAttributes(R.styleable.Theme).getBoolean(38, false)) {
            requestWindowFeature(1);
        }
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        if (!isAnySetupWizard || !(this instanceof SubSettings)) {
            SettingsBaseEndlessLayoutMixin.applyTheme(this);
        } else {
            if (ThemeHelper.trySetDynamicColor(this)) {
                if (ThemeHelper.isSetupWizardDayNightEnabled(this)) {
                    i = C1993R$style.SudDynamicColorThemeSettings_SetupWizard_DayNight;
                } else {
                    i = C1993R$style.SudDynamicColorThemeSettings_SetupWizard;
                }
            } else if (ThemeHelper.isSetupWizardDayNightEnabled(this)) {
                i = C1993R$style.SubSettings_SetupWizard;
            } else {
                i = C1993R$style.SudThemeGlifV3_Light;
            }
            setTheme(i);
        }
        if (!isToolbarEnabled() || isAnySetupWizard) {
            super.setContentView(C1987R$layout.settings_base_layout);
        } else {
            super.setContentView(C1987R$layout.collapsing_toolbar_base_layout);
            this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(C1985R$id.collapsing_toolbar);
            this.mAppBarLayout = (AppBarLayout) findViewById(C1985R$id.app_bar);
            CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
            }
            disableCollapsingToolbarLayoutScrollingBehavior();
        }
        Toolbar toolbar = (Toolbar) findViewById(C1985R$id.action_bar);
        if (!isToolbarEnabled() || isAnySetupWizard) {
            toolbar.setVisibility(8);
        } else {
            setActionBar(toolbar);
        }
    }

    public void setActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
        this.mToolbar = toolbar;
    }

    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finishAfterTransition();
        return true;
    }

    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        int transitionType = getTransitionType(intent);
        super.startActivityForResult(intent, i, bundle);
        if (transitionType == 1) {
            overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
        } else if (transitionType == 2) {
            overridePendingTransition(17432576, C1977R$anim.sud_stay);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (getTransitionType(getIntent()) == 2) {
            overridePendingTransition(C1977R$anim.sud_stay, 17432577);
        }
        super.onPause();
    }

    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) findViewById(C1985R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, viewGroup);
    }

    public void setContentView(View view) {
        ((ViewGroup) findViewById(C1985R$id.content_frame)).addView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ((ViewGroup) findViewById(C1985R$id.content_frame)).addView(view, layoutParams);
    }

    public void setTitle(CharSequence charSequence) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(charSequence);
        } else {
            super.setTitle(charSequence);
        }
    }

    public void setTitle(int i) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(getText(i));
        } else {
            super.setTitle(i);
        }
    }

    private boolean isLockTaskModePinned() {
        return ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getLockTaskModeState() == 2;
    }

    private boolean isSettingsRunOnTop() {
        return TextUtils.equals(getPackageName(), ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getRunningTasks(1).get(0).baseActivity.getPackageName());
    }

    public boolean setTileEnabled(ComponentName componentName, boolean z) {
        PackageManager packageManager = getPackageManager();
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);
        if ((componentEnabledSetting == 1) == z && componentEnabledSetting != 0) {
            return false;
        }
        if (z) {
            this.mCategoryMixin.removeFromDenylist(componentName);
        } else {
            this.mCategoryMixin.addToDenylist(componentName);
        }
        packageManager.setComponentEnabledSetting(componentName, z ? 1 : 2, 1);
        return true;
    }

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout != null) {
            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                public boolean canDrag(AppBarLayout appBarLayout) {
                    return false;
                }
            });
            ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(behavior);
        }
    }

    private int getTransitionType(Intent intent) {
        return intent.getIntExtra("page_transition_type", -1);
    }
}
