package com.motorola.settings;

import java.io.File;

public final /* synthetic */ class CarrierMediaUpdateJobService$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ CarrierMediaUpdateJobService f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ CarrierMediaUpdateJobService$$ExternalSyntheticLambda0(CarrierMediaUpdateJobService carrierMediaUpdateJobService, File file) {
        this.f$0 = carrierMediaUpdateJobService;
        this.f$1 = file;
    }

    public final void run() {
        this.f$0.lambda$onStartJob$0(this.f$1);
    }
}
