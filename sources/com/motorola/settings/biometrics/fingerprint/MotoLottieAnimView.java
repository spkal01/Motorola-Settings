package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Pair;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.android.settings.R$styleable;
import java.util.HashMap;

public class MotoLottieAnimView extends LottieAnimationView {
    private static HashMap<String, Pair<Integer, Integer>> sFixedColors;
    private int[] mAnimJsonIds;
    private int mCurrentAnimation = -1;
    private boolean mNightMode;

    static {
        HashMap<String, Pair<Integer, Integer>> hashMap = new HashMap<>();
        sFixedColors = hashMap;
        hashMap.put("background", new Pair(17170477, 17170470));
        sFixedColors.put("phone", new Pair(17170471, 17170464));
        sFixedColors.put("screen", new Pair(17170509, 17170510));
        sFixedColors.put("backsensor", new Pair(17170469, 17170466));
        sFixedColors.put("card1", new Pair(17170494, 17170492));
        sFixedColors.put("card2", new Pair(17170520, 17170518));
        sFixedColors.put("icon", new Pair(17170492, 17170495));
        sFixedColors.put("box1", new Pair(17170508, 17170509));
        sFixedColors.put("box2", new Pair(17170504, 17170505));
        sFixedColors.put("glass", new Pair(17170491, 17170491));
        sFixedColors.put("fingerprint", new Pair(17170491, 17170491));
        sFixedColors.put("sensor", new Pair(17170465, 17170465));
    }

    public MotoLottieAnimView(Context context) {
        super(context);
    }

    public MotoLottieAnimView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public MotoLottieAnimView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mNightMode = FingerprintUtils.isDarkTheme(context) || FingerprintUtils.isMotoFODEnabled(context);
        setFailureListener(MotoLottieAnimView$$ExternalSyntheticLambda0.INSTANCE);
        int resourceId = context.obtainStyledAttributes(attributeSet, R$styleable.MotoLottieAnimView).getResourceId(R$styleable.MotoLottieAnimView_animationJsonArray, 0);
        if (resourceId != 0) {
            TypedArray obtainTypedArray = getResources().obtainTypedArray(resourceId);
            this.mAnimJsonIds = new int[obtainTypedArray.length()];
            for (int i = 0; i < obtainTypedArray.length(); i++) {
                this.mAnimJsonIds[i] = obtainTypedArray.getResourceId(i, -1);
            }
            obtainTypedArray.recycle();
            setNextAnimation();
        }
    }

    private void setNextAnimation() {
        int i = this.mCurrentAnimation;
        int i2 = i + 1;
        int[] iArr = this.mAnimJsonIds;
        if (i2 < iArr.length) {
            int i3 = i + 1;
            this.mCurrentAnimation = i3;
            setAnimation(iArr[i3]);
            playAnimation();
        }
    }

    public void updateProgress(int i, int i2) {
        int i3;
        if (i % 2 == 0) {
            i3 = i / 2;
        } else {
            i3 = (i / 2) + 1;
        }
        if (i2 <= i3 && this.mCurrentAnimation == 0) {
            setNextAnimation();
        }
    }

    public void setAnimation(int i) {
        super.setAnimation(i);
        applyDynamicColors();
    }

    private void applyDynamicColors() {
        for (String next : sFixedColors.keySet()) {
            Pair pair = sFixedColors.get(next);
            final int color = getContext().getResources().getColor(((Integer) (this.mNightMode ? pair.second : pair.first)).intValue());
            addValueCallback(new KeyPath("**", next, "**"), LottieProperty.COLOR_FILTER, new SimpleLottieValueCallback<ColorFilter>() {
                public ColorFilter getValue(LottieFrameInfo<ColorFilter> lottieFrameInfo) {
                    return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            });
        }
    }
}
