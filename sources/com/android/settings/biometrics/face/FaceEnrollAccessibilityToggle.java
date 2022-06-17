package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.R$styleable;

public class FaceEnrollAccessibilityToggle extends LinearLayout {
    private Switch mSwitch;

    public FaceEnrollAccessibilityToggle(Context context) {
        this(context, (AttributeSet) null);
    }

    public FaceEnrollAccessibilityToggle(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: finally extract failed */
    public FaceEnrollAccessibilityToggle(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(C1987R$layout.face_enroll_accessibility_toggle, this, true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.FaceEnrollAccessibilityToggle);
        try {
            ((TextView) findViewById(C1985R$id.title)).setText(obtainStyledAttributes.getText(R$styleable.FaceEnrollAccessibilityToggle_messageText));
            obtainStyledAttributes.recycle();
            Switch switchR = (Switch) findViewById(C1985R$id.toggle);
            this.mSwitch = switchR;
            switchR.setChecked(false);
            this.mSwitch.setClickable(false);
            this.mSwitch.setFocusable(false);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public boolean isChecked() {
        return this.mSwitch.isChecked();
    }

    public void setChecked(boolean z) {
        this.mSwitch.setChecked(z);
    }

    public void setListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public Switch getSwitch() {
        return this.mSwitch;
    }
}
