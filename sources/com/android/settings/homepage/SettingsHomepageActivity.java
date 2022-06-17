package com.android.settings.homepage;

import android.app.ActivityManager;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.accounts.AvatarViewMixin;
import com.android.settings.core.CategoryMixin;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.motorola.settings.display.edge.SettingsHomeEndlessLayoutMixin;
import com.motorola.settings.utils.DisplayUtils;

public class SettingsHomepageActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    private CategoryMixin mCategoryMixin;
    private View mHomepageView;
    private View mSuggestionView;

    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    public void showHomepageWithSuggestion(boolean z) {
        if (this.mHomepageView != null) {
            Log.i("SettingsHomepageActivity", "showHomepageWithSuggestion: " + z);
            this.mSuggestionView.setVisibility(z ? 0 : 8);
            this.mHomepageView.setVisibility(0);
            this.mHomepageView = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.settings_homepage_container);
        findViewById(C1985R$id.app_bar_container).setMinimumHeight(getSearchBoxHeight());
        initHomepageContainer();
        FeatureFactory.getFactory(this).getSearchFeatureProvider().initSearchToolbar(this, (Toolbar) findViewById(C1985R$id.search_action_bar), 1502);
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        getLifecycle().addObserver(new SettingsHomeEndlessLayoutMixin(this));
        this.mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(this.mCategoryMixin);
        if (!((ActivityManager) getSystemService(ActivityManager.class)).isLowRamDevice()) {
            ImageView imageView = (ImageView) findViewById(C1985R$id.account_avatar);
            if (AvatarViewMixin.isAvatarSupported(this)) {
                imageView.setVisibility(0);
                getLifecycle().addObserver(new AvatarViewMixin(this, imageView));
            }
            showSuggestionFragment();
            if (FeatureFlagUtils.isEnabled(this, "settings_contextual_home")) {
                if (DisplayUtils.isDesktopMode(this)) {
                    hideFragment(C1985R$id.contextual_cards_content);
                } else {
                    showFragment(new ContextualCardsFragment(), C1985R$id.contextual_cards_content);
                }
            }
        }
        TopLevelSettings topLevelSettings = new TopLevelSettings();
        int i = C1985R$id.main_content;
        showFragment(topLevelSettings, i);
        ((FrameLayout) findViewById(i)).getLayoutTransition().enableTransitionType(4);
    }

    private void showSuggestionFragment() {
        Class<? extends Fragment> contextualSuggestionFragment = FeatureFactory.getFactory(this).getSuggestionFeatureProvider(this).getContextualSuggestionFragment();
        if (contextualSuggestionFragment != null) {
            int i = C1985R$id.suggestion_content;
            this.mSuggestionView = findViewById(i);
            View findViewById = findViewById(C1985R$id.settings_homepage_container);
            this.mHomepageView = findViewById;
            findViewById.setVisibility(8);
            this.mHomepageView.postDelayed(new SettingsHomepageActivity$$ExternalSyntheticLambda0(this), 300);
            try {
                showFragment((Fragment) contextualSuggestionFragment.getConstructor(new Class[0]).newInstance(new Object[0]), i);
            } catch (Exception e) {
                Log.w("SettingsHomepageActivity", "Cannot show fragment", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestionFragment$0() {
        showHomepageWithSuggestion(false);
    }

    private void showFragment(Fragment fragment, int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        if (findFragmentById == null) {
            beginTransaction.add(i, fragment);
        } else {
            beginTransaction.show(findFragmentById);
        }
        beginTransaction.commit();
    }

    private void hideFragment(int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        if (findFragmentById != null) {
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.hide(findFragmentById);
            beginTransaction.commit();
        }
    }

    private void initHomepageContainer() {
        View findViewById = findViewById(C1985R$id.homepage_container);
        findViewById.setFocusableInTouchMode(true);
        findViewById.requestFocus();
    }

    private int getSearchBoxHeight() {
        return getResources().getDimensionPixelSize(C1982R$dimen.search_bar_height) + (getResources().getDimensionPixelSize(C1982R$dimen.search_bar_margin) * 2);
    }
}
