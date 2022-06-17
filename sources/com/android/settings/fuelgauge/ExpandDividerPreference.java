package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class ExpandDividerPreference extends Preference {
    static final String PREFERENCE_KEY = "expandable_divider";
    ImageView mImageView;
    private boolean mIsExpanded;
    private OnExpandListener mOnExpandListener;
    TextView mTextView;
    private String mTitleContent;

    public interface OnExpandListener {
        void onExpand(boolean z);
    }

    public ExpandDividerPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public ExpandDividerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsExpanded = false;
        this.mTitleContent = null;
        setLayoutResource(C1987R$layout.preference_expand_divider);
        setKey(PREFERENCE_KEY);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mTextView = (TextView) preferenceViewHolder.findViewById(C1985R$id.expand_title);
        this.mImageView = (ImageView) preferenceViewHolder.findViewById(C1985R$id.expand_icon);
        refreshState();
    }

    public void onClick() {
        this.mIsExpanded = !this.mIsExpanded;
        refreshState();
        OnExpandListener onExpandListener = this.mOnExpandListener;
        if (onExpandListener != null) {
            onExpandListener.onExpand(this.mIsExpanded);
        }
    }

    /* access modifiers changed from: package-private */
    public void setTitle(String str) {
        this.mTitleContent = str;
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.postDelayed(new ExpandDividerPreference$$ExternalSyntheticLambda0(this, str), 50);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setTitle$0(String str) {
        this.mTextView.setText(str);
    }

    /* access modifiers changed from: package-private */
    public void setIsExpanded(boolean z) {
        this.mIsExpanded = z;
        refreshState();
    }

    /* access modifiers changed from: package-private */
    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.mOnExpandListener = onExpandListener;
    }

    private void refreshState() {
        int i;
        if (this.mIsExpanded) {
            i = C1983R$drawable.ic_settings_expand_less;
        } else {
            i = C1983R$drawable.ic_settings_expand_more;
        }
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageResource(i);
        }
        setTitle(this.mTitleContent);
    }
}
