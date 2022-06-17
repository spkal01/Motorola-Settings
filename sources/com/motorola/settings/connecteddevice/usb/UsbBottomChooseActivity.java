package com.motorola.settings.connecteddevice.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.connecteddevice.usb.UsbBackend;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.utils.MotoReadyForUtils;
import com.motorola.settings.utils.MotoWebcamUtils;

public class UsbBottomChooseActivity extends Activity {
    private final int READY_FOR_ENABLED = 1;
    private final String TAG = "UsbBottomChooseActivity";
    private final int WHAT_CLOSE = 1;
    private BottomSheetBehavior mBottomChooseBehavior;
    private View mBottomChooseView;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        public void onSlide(View view, float f) {
        }

        public void onStateChanged(View view, int i) {
            if (i == 4) {
                UsbBottomChooseActivity.this.hiddenAndFinish();
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                UsbBottomChooseActivity.this.finish();
            }
        }
    };
    private Button mReadyFor;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!(intent.getExtras().getBoolean("connected") || intent.getExtras().getBoolean("host_connected"))) {
                UsbBottomChooseActivity.this.finish();
            }
        }
    };
    private UsbBackend mUsbBackend;
    private Button mUsbWebcam;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addPrivateFlags(536870912);
        this.mUsbBackend = new UsbBackend(this);
        setContentView(C1987R$layout.usb_bottom_choose);
        View findViewById = findViewById(C1985R$id.bottom_sheet);
        this.mBottomChooseView = findViewById;
        BottomSheetBehavior from = BottomSheetBehavior.from(findViewById);
        this.mBottomChooseBehavior = from;
        from.setState(3);
        ((ImageButton) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_close)).setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda3(this));
        ((ImageButton) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_settings)).setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda2(this));
        Button button = (Button) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_ready_for);
        this.mReadyFor = button;
        button.setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda1(this));
        if (MotorolaSettings.Global.getInt(getContentResolver(), "ready_for_rdp", 1) != 1) {
            this.mReadyFor.setAlpha(0.5f);
            this.mReadyFor.setClickable(false);
        }
        Button button2 = (Button) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_usb_webcam);
        this.mUsbWebcam = button2;
        button2.setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda0(this));
        Button button3 = (Button) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_file_transfer);
        button3.setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda5(this, button3));
        Button button4 = (Button) this.mBottomChooseView.findViewById(C1985R$id.usb_bottom_photo_video_transfer);
        button4.setOnClickListener(new UsbBottomChooseActivity$$ExternalSyntheticLambda4(this, button4));
        ImageView imageView = (ImageView) this.mBottomChooseView.findViewById(C1985R$id.bottom_charging);
        imageView.setBackgroundResource(C1983R$drawable.ic_usb_charging);
        ((AnimationDrawable) imageView.getBackground()).start();
        this.mHandler.sendEmptyMessageDelayed(1, 10000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        hiddenAndFinish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        startActivity(Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.Settings$UsbDetailsActivity")));
        hiddenAndFinish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View view) {
        this.mUsbBackend.setCurrentFunctions(MotoReadyForUtils.FUNCTION_READYFOR);
        MotoReadyForUtils.startReadyForConnect(this);
        hiddenAndFinish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(View view) {
        this.mUsbBackend.setCurrentFunctions(MotoWebcamUtils.FUNCTION_WEBCAM);
        hiddenAndFinish();
        showToast(this.mUsbWebcam.getText().toString());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$4(Button button, View view) {
        this.mUsbBackend.setCurrentFunctions(4);
        hiddenAndFinish();
        showToast(button.getText().toString());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$5(Button button, View view) {
        this.mUsbBackend.setCurrentFunctions(16);
        hiddenAndFinish();
        showToast(button.getText().toString());
    }

    /* access modifiers changed from: private */
    public void hiddenAndFinish() {
        this.mBottomChooseBehavior.setState(5);
        finish();
        this.mHandler.removeMessages(1);
    }

    private void refresh() {
        if (MotoReadyForUtils.isReadyForFeatureSupported(this)) {
            this.mReadyFor.setVisibility(0);
        } else {
            this.mReadyFor.setVisibility(8);
        }
        if (MotoWebcamUtils.isWebcamFeatureSupported(this)) {
            this.mUsbWebcam.setVisibility(0);
        } else {
            this.mUsbWebcam.setVisibility(8);
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, getString(C1992R$string.usb_bottom_choose_toast, new Object[]{str}), 0).show();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        register();
        refresh();
        this.mBottomChooseBehavior.addBottomSheetCallback(this.mBottomSheetCallback);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        unregister();
        this.mBottomChooseBehavior.removeBottomSheetCallback(this.mBottomSheetCallback);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        BottomSheetBehavior bottomSheetBehavior = this.mBottomChooseBehavior;
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == 3) {
            Rect rect = new Rect();
            this.mBottomChooseView.getGlobalVisibleRect(rect);
            if (!rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.mBottomChooseBehavior.setState(5);
                finish();
                return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        registerReceiver(this.mReceiver, intentFilter);
    }

    public void unregister() {
        unregisterReceiver(this.mReceiver);
    }
}
