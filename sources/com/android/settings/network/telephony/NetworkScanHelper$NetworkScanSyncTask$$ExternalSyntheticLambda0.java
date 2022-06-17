package com.android.settings.network.telephony;

import com.android.internal.telephony.OperatorInfo;
import java.util.function.Function;

public final /* synthetic */ class NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ String f$0;

    public /* synthetic */ NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0(String str) {
        this.f$0 = str;
    }

    public final Object apply(Object obj) {
        return CellInfoUtil.convertOperatorInfoToCellInfo((OperatorInfo) obj, this.f$0);
    }
}
