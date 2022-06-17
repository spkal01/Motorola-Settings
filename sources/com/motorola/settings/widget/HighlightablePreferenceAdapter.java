package com.motorola.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.android.settings.C1981R$color;
import com.android.settings.C1985R$id;
import com.google.android.material.appbar.AppBarLayout;

public abstract class HighlightablePreferenceAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    /* access modifiers changed from: private */
    public boolean mFadeInAnimated;
    private ValueAnimator mFadeInAnimator;
    private ValueAnimator mFadeOutAnimator;
    private final int mHighlightColor;
    private int mHighlightPosition = -1;
    private boolean mHighlightRequested;
    /* access modifiers changed from: private */
    public final int mNormalBackgroundRes;

    public HighlightablePreferenceAdapter(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843534, typedValue, true);
        this.mNormalBackgroundRes = typedValue.resourceId;
        this.mHighlightColor = context.getColor(C1981R$color.preference_highligh_color);
    }

    public void requestHighlight(int i, RecyclerView recyclerView, AppBarLayout appBarLayout) {
        if (!this.mHighlightRequested && recyclerView != null && i >= 0) {
            if (appBarLayout != null) {
                recyclerView.postDelayed(new HighlightablePreferenceAdapter$$ExternalSyntheticLambda2(appBarLayout), 600);
            }
            recyclerView.postDelayed(new HighlightablePreferenceAdapter$$ExternalSyntheticLambda4(this, recyclerView, i), 600);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$1(RecyclerView recyclerView, int i) {
        this.mHighlightRequested = true;
        recyclerView.smoothScrollToPosition(i);
        this.mHighlightPosition = i;
        notifyItemChanged(i);
    }

    public void onBindViewHolder(T t, int i) {
        View view = t.itemView;
        if (i == this.mHighlightPosition) {
            t.setIsRecyclable(false);
            addHighlightBackground(view, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(C1985R$id.preference_highlighted))) {
            removeHighlightBackground(view, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void requestRemoveHighlightDelayed(View view) {
        view.postDelayed(new HighlightablePreferenceAdapter$$ExternalSyntheticLambda3(this, view), 15000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$2(View view) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(view, true);
    }

    private void addHighlightBackground(View view, boolean z) {
        if (this.mFadeInAnimator == null) {
            ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(this.mNormalBackgroundRes), Integer.valueOf(this.mHighlightColor)});
            this.mFadeInAnimator = ofObject;
            ofObject.setDuration(200);
            this.mFadeInAnimator.setRepeatMode(2);
            this.mFadeInAnimator.setRepeatCount(4);
        }
        this.mFadeInAnimator.removeAllUpdateListeners();
        this.mFadeInAnimator.removeAllListeners();
        view.setTag(C1985R$id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundColor(this.mHighlightColor);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(view);
            return;
        }
        this.mFadeInAnimator.addUpdateListener(new HighlightablePreferenceAdapter$$ExternalSyntheticLambda1(view));
        this.mFadeInAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                boolean unused = HighlightablePreferenceAdapter.this.mFadeInAnimated = true;
            }
        });
        this.mFadeInAnimator.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        requestRemoveHighlightDelayed(view);
    }

    private void removeHighlightBackground(final View view, boolean z) {
        if (this.mFadeOutAnimator == null) {
            ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(this.mHighlightColor), Integer.valueOf(this.mNormalBackgroundRes)});
            this.mFadeOutAnimator = ofObject;
            ofObject.setDuration(500);
        }
        this.mFadeOutAnimator.removeAllUpdateListeners();
        this.mFadeOutAnimator.removeAllListeners();
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
        view.setTag(i, Boolean.FALSE);
        this.mFadeOutAnimator.addUpdateListener(new HighlightablePreferenceAdapter$$ExternalSyntheticLambda0(view));
        this.mFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                view.setBackgroundResource(HighlightablePreferenceAdapter.this.mNormalBackgroundRes);
            }
        });
        this.mFadeOutAnimator.start();
        Log.d("HighlightableAdapter", "Starting fade out animation");
    }

    public static boolean isArgKeySystemPackage(Context context, Bundle bundle) {
        ComponentName unflattenFromString;
        if (!(context == null || bundle == null || !bundle.containsKey(":settings:fragment_args_key") || (unflattenFromString = ComponentName.unflattenFromString(bundle.getString(":settings:fragment_args_key"))) == null)) {
            try {
                if ((context.getPackageManager().getApplicationInfo(unflattenFromString.getPackageName(), 0).flags & 1) != 0) {
                    return true;
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
