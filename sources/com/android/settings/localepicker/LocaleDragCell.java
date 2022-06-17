package com.android.settings.localepicker;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.internal.app.LocaleStore;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;

class LocaleDragCell extends RelativeLayout {
    private CheckBox mCheckbox;
    private ImageView mDragHandle;
    private TextView mLabel;
    private TextView mLocalized;
    private TextView mMiniLabel;

    public LocaleDragCell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLabel = (TextView) findViewById(C1985R$id.label);
        this.mLocalized = (TextView) findViewById(C1985R$id.l10nWarn);
        this.mMiniLabel = (TextView) findViewById(C1985R$id.miniLabel);
        this.mCheckbox = (CheckBox) findViewById(C1985R$id.checkbox);
        this.mDragHandle = (ImageView) findViewById(C1985R$id.dragHandle);
    }

    public void setShowHandle(boolean z) {
        this.mDragHandle.setVisibility(z ? 0 : 4);
        invalidate();
        requestLayout();
    }

    public void setShowCheckbox(boolean z) {
        if (z) {
            this.mCheckbox.setVisibility(0);
            this.mLabel.setVisibility(4);
        } else {
            this.mCheckbox.setVisibility(4);
            this.mLabel.setVisibility(0);
        }
        invalidate();
        requestLayout();
    }

    public void setShowMiniLabel(boolean z) {
        this.mMiniLabel.setVisibility(z ? 0 : 8);
        invalidate();
        requestLayout();
    }

    public void setMiniLabel(String str) {
        this.mMiniLabel.setText(str);
        invalidate();
    }

    public void setLabelAndDescription(String str, String str2) {
        this.mLabel.setText(str);
        this.mCheckbox.setText(str);
        this.mLabel.setContentDescription(str2);
        this.mCheckbox.setContentDescription(str2);
        invalidate();
    }

    public void setLocalized(LocaleStore.LocaleInfo localeInfo) {
        boolean isLocalized = localeInfo.isLocalized();
        this.mLocalized.setVisibility(isLocalized ? 8 : 0);
        if (!isLocalized) {
            Context context = getContext();
            Intent helpIntent = HelpUtils.getHelpIntent(context, context.getString(C1992R$string.help_url_locale_not_translated), context.getClass().getName());
            if (helpIntent != null) {
                helpIntent.putExtra("l", localeInfo.getLocale().toLanguageTag());
                this.mLocalized.setText(AnnotationSpan.linkify(context.getText(C1992R$string.locale_not_translated_with_learn_more), new AnnotationSpan.LinkInfo(context, "learn_more", helpIntent)));
                this.mLocalized.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        invalidate();
    }

    public ImageView getDragHandle() {
        return this.mDragHandle;
    }

    public CheckBox getCheckbox() {
        return this.mCheckbox;
    }
}
