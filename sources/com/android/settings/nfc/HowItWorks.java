package com.android.settings.nfc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class HowItWorks extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.nfc_payment_how_it_works);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ((Button) findViewById(C1985R$id.nfc_how_it_works_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                HowItWorks.this.finish();
            }
        });
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }
}
