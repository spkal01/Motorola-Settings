package com.motorola.settings.network.telephony;

import android.content.Context;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import com.android.settings.C1992R$string;
import com.android.settings.network.ProviderModelSlice$$ExternalSyntheticLambda2;
import com.android.settings.network.telephony.MobileDataSlice;
import com.android.settings.network.telephony.NetworkProviderWorker;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.WirelessUtils;
import java.util.List;
import java.util.Objects;

public class ExtendedMobileDataSlice extends MobileDataSlice {
    private final Context mContext;
    private final ExtendedProviderModelSliceHelper mHelper;

    public ExtendedMobileDataSlice(Context context) {
        super(context);
        this.mContext = context;
        this.mHelper = new ExtendedProviderModelSliceHelper(context, this);
    }

    public Slice getSlice() {
        if (!isMobileDataSliceAvailable()) {
            return super.getSlice();
        }
        NetworkProviderWorker networkProviderWorker = (NetworkProviderWorker) SliceBackgroundWorker.getInstance(getUri());
        if (networkProviderWorker != null) {
            this.mHelper.setDefaultNetworkDescription(networkProviderWorker.getNetworkTypeDescription());
        }
        ListBuilder accentColor = new ListBuilder(this.mContext, getUri(), -1).setHeader(new ListBuilder.HeaderBuilder().setTitle(this.mContext.getString(C1992R$string.mobile_data_settings_title))).setAccentColor(-1);
        this.mHelper.updateTelephony();
        List<ListBuilder.RowBuilder> createMobileDataRows = this.mHelper.createMobileDataRows();
        Objects.requireNonNull(accentColor);
        createMobileDataRows.forEach(new ProviderModelSlice$$ExternalSyntheticLambda2(accentColor));
        return accentColor.build();
    }

    private boolean isMobileDataSliceAvailable() {
        if (!WirelessUtils.isAirplaneModeOn(this.mContext) && this.mHelper.hasCarrier()) {
            return true;
        }
        return false;
    }

    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return NetworkProviderWorker.class;
    }
}
