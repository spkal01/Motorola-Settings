package com.android.settings.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1981R$color;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settingslib.RestrictedPreference;

public class AddPreference extends RestrictedPreference implements View.OnClickListener {
    private View mAddWidget;
    private Context mContext;
    private OnAddClickListener mListener;
    private View mWidgetFrame;

    public interface OnAddClickListener {
        void onAddClick(AddPreference addPreference);
    }

    public AddPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    /* access modifiers changed from: package-private */
    public int getAddWidgetResId() {
        return C1985R$id.add_preference_widget;
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.mListener = onAddClickListener;
        View view = this.mWidgetFrame;
        if (view != null) {
            view.setVisibility(shouldHideSecondTarget() ? 8 : 0);
        }
    }

    public void setAddWidgetEnabled(boolean z) {
        View view = this.mAddWidget;
        if (view != null) {
            view.setEnabled(z);
            View view2 = this.mAddWidget;
            if (view2 instanceof ImageView) {
                ImageView imageView = (ImageView) view2;
                if (z) {
                    imageView.clearColorFilter();
                } else {
                    imageView.setColorFilter(this.mContext.getColor(C1981R$color.disabled_text_color), PorterDuff.Mode.MULTIPLY);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getSecondTargetResId() {
        return C1987R$layout.preference_widget_add;
    }

    /* access modifiers changed from: protected */
    public boolean shouldHideSecondTarget() {
        return this.mListener == null;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mWidgetFrame = preferenceViewHolder.findViewById(16908312);
        View findViewById = preferenceViewHolder.findViewById(getAddWidgetResId());
        this.mAddWidget = findViewById;
        findViewById.setEnabled(true);
        this.mAddWidget.setOnClickListener(this);
    }

    public void onClick(View view) {
        OnAddClickListener onAddClickListener;
        if (view.getId() == getAddWidgetResId() && (onAddClickListener = this.mListener) != null) {
            onAddClickListener.onAddClick(this);
        }
    }
}
