package com.motorola.settings.network.telephony;

import android.content.Context;
import android.net.Uri;
import com.android.settings.slices.CustomSliceRegistry;

public class MobileDataSIM1Slice extends AbstractMobileDataSlice {
    public MobileDataSIM1Slice(Context context) {
        super(context);
    }

    public Uri getUri() {
        return CustomSliceRegistry.MOBILE_DATA_SIM1_SLICE_URI;
    }
}
