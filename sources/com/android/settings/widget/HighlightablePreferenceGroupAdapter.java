package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C1981R$color;
import com.android.settings.C1985R$id;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.appbar.AppBarLayout;

public class HighlightablePreferenceGroupAdapter extends PreferenceGroupAdapter {
    static final long DELAY_COLLAPSE_DURATION_MILLIS = 300;
    static final long DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    boolean mFadeInAnimated;
    final int mHighlightColor;
    private final String mHighlightKey;
    private int mHighlightPosition = -1;
    private boolean mHighlightRequested;
    /* access modifiers changed from: private */
    public final int mNormalBackgroundRes;

    public static void adjustInitialExpandedChildCount(SettingsPreferenceFragment settingsPreferenceFragment) {
        PreferenceScreen preferenceScreen;
        if (settingsPreferenceFragment != null && (preferenceScreen = settingsPreferenceFragment.getPreferenceScreen()) != null) {
            Bundle arguments = settingsPreferenceFragment.getArguments();
            if (arguments == null || TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
                int initialExpandedChildCount = settingsPreferenceFragment.getInitialExpandedChildCount();
                if (initialExpandedChildCount > 0) {
                    preferenceScreen.setInitialExpandedChildrenCount(initialExpandedChildCount);
                    return;
                }
                return;
            }
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    public HighlightablePreferenceGroupAdapter(PreferenceGroup preferenceGroup, String str, boolean z) {
        super(preferenceGroup);
        this.mHighlightKey = str;
        this.mHighlightRequested = z;
        Context context = preferenceGroup.getContext();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843534, typedValue, true);
        this.mNormalBackgroundRes = typedValue.resourceId;
        this.mHighlightColor = context.getColor(C1981R$color.preference_highligh_color);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        updateBackground(preferenceViewHolder, i);
    }

    /* access modifiers changed from: package-private */
    public void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        String str;
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition && (str = this.mHighlightKey) != null && TextUtils.equals(str, getItem(i).getKey())) {
            addHighlightBackground(preferenceViewHolder, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(C1985R$id.preference_highlighted))) {
            removeHighlightBackground(preferenceViewHolder, false);
        }
    }

    public void requestHighlight(View view, RecyclerView recyclerView, AppBarLayout appBarLayout) {
        int preferenceAdapterPosition;
        if (!this.mHighlightRequested && recyclerView != null && !TextUtils.isEmpty(this.mHighlightKey) && (preferenceAdapterPosition = getPreferenceAdapterPosition(this.mHighlightKey)) >= 0) {
            if (appBarLayout != null) {
                view.postDelayed(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda5(appBarLayout), DELAY_COLLAPSE_DURATION_MILLIS);
            }
            view.postDelayed(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda4(this, recyclerView, preferenceAdapterPosition), DELAY_HIGHLIGHT_DURATION_MILLIS);
            view.postDelayed(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda2(this, preferenceAdapterPosition), 900);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$1(RecyclerView recyclerView, int i) {
        this.mHighlightRequested = true;
        recyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
        recyclerView.smoothScrollToPosition(i);
        this.mHighlightPosition = i;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$2(int i) {
        notifyItemChanged(i);
    }

    public boolean isHighlightRequested() {
        return this.mHighlightRequested;
    }

    /* access modifiers changed from: package-private */
    public void requestRemoveHighlightDelayed(PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.postDelayed(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda3(this, preferenceViewHolder), 15000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$3(PreferenceViewHolder preferenceViewHolder) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(preferenceViewHolder, true);
    }

    private void addHighlightBackground(PreferenceViewHolder preferenceViewHolder, boolean z) {
        View view = preferenceViewHolder.itemView;
        view.setTag(C1985R$id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundColor(this.mHighlightColor);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(preferenceViewHolder);
            return;
        }
        this.mFadeInAnimated = true;
        int i = this.mNormalBackgroundRes;
        int i2 = this.mHighlightColor;
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        ofObject.setDuration(200);
        ofObject.addUpdateListener(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda0(view));
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(4);
        ofObject.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        preferenceViewHolder.setIsRecyclable(false);
        requestRemoveHighlightDelayed(preferenceViewHolder);
    }

    private void removeHighlightBackground(final PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        if (!z) {
            view.setTag(C1985R$id.preference_highlighted, Boolean.FALSE);
            view.setBackgroundResource(this.mNormalBackgroundRes);
            Log.d("HighlightableAdapter", "RemoveHighlight: No animation requested - setting normal background");
            return;
        }
        Boolean bool = Boolean.TRUE;
        int i = C1985R$id.preference_highlighted;
        if (!bool.equals(view.getTag(i))) {
            Log.d("HighlightableAdapter", "RemoveHighlight: Not highlighted - skipping");
            return;
        }
        int i2 = this.mHighlightColor;
        int i3 = this.mNormalBackgroundRes;
        view.setTag(i, Boolean.FALSE);
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
        ofObject.setDuration(500);
        ofObject.addUpdateListener(new HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda1(view));
        ofObject.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                view.setBackgroundResource(HighlightablePreferenceGroupAdapter.this.mNormalBackgroundRes);
                preferenceViewHolder.setIsRecyclable(true);
            }
        });
        ofObject.start();
        Log.d("HighlightableAdapter", "Starting fade out animation");
    }
}
