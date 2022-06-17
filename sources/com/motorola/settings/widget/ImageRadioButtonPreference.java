package com.motorola.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.Utils;
import com.android.settingslib.widget.RadioButtonPreference;

public class ImageRadioButtonPreference extends RadioButtonPreference {
    private Drawable mDrawable;

    public ImageRadioButtonPreference(Context context) {
        super(context, (AttributeSet) null);
        setLayoutResource(C1987R$layout.image_radio_button_preference);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        setSafeImage((ImageView) preferenceViewHolder.findViewById(C1985R$id.radio_button_image));
    }

    public void setImage(Drawable drawable) {
        this.mDrawable = drawable;
    }

    public void setSafeImage(ImageView imageView) {
        Drawable drawable = this.mDrawable;
        if (drawable != null && !(drawable instanceof VectorDrawable)) {
            drawable = Utils.getSafeDrawable(drawable, 500, 500);
        }
        imageView.setImageDrawable(drawable);
    }
}
