package com.motorola.settings.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

public final /* synthetic */ class PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ImageView f$0;
    public final /* synthetic */ Bitmap f$1;

    public /* synthetic */ PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda3(ImageView imageView, Bitmap bitmap) {
        this.f$0 = imageView;
        this.f$1 = bitmap;
    }

    public final void run() {
        this.f$0.setImageBitmap(this.f$1);
    }
}
