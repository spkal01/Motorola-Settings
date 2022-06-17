package com.motorola.settings.network.telephony;

import android.content.Context;
import android.net.Uri;
import com.android.settings.slices.CustomSliceRegistry;

public class MobileDataSIM2Slice extends AbstractMobileDataSlice {
    public MobileDataSIM2Slice(Context context) {
        super(context);
    }

    public Uri getUri() {
        return CustomSliceRegistry.MOBILE_DATA_SIM2_SLICE_URI;
    }
}
