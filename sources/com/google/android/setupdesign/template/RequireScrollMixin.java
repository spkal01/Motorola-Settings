package com.google.android.setupdesign.template;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.template.Mixin;

public class RequireScrollMixin implements Mixin {
    /* access modifiers changed from: private */
    public ScrollHandlingDelegate delegate;
    private boolean everScrolledToBottom = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public OnRequireScrollStateChangedListener listener;
    /* access modifiers changed from: private */
    public boolean requiringScrollToBottom = false;

    public interface OnRequireScrollStateChangedListener {
        void onRequireScrollStateChanged(boolean z);
    }

    interface ScrollHandlingDelegate {
        void pageScrollDown();

        void startListening();
    }

    public RequireScrollMixin(TemplateLayout templateLayout) {
    }

    public void setScrollHandlingDelegate(ScrollHandlingDelegate scrollHandlingDelegate) {
        this.delegate = scrollHandlingDelegate;
    }

    public void setOnRequireScrollStateChangedListener(OnRequireScrollStateChangedListener onRequireScrollStateChangedListener) {
        this.listener = onRequireScrollStateChangedListener;
    }

    public View.OnClickListener createOnClickListener(final View.OnClickListener onClickListener) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                if (RequireScrollMixin.this.requiringScrollToBottom) {
                    RequireScrollMixin.this.delegate.pageScrollDown();
                    return;
                }
                View.OnClickListener onClickListener = onClickListener;
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        };
    }

    public void requireScrollWithButton(Context context, FooterButton footerButton, int i, View.OnClickListener onClickListener) {
        requireScrollWithButton(footerButton, context.getText(i), onClickListener);
    }

    public void requireScrollWithButton(final FooterButton footerButton, final CharSequence charSequence, View.OnClickListener onClickListener) {
        final CharSequence text = footerButton.getText();
        footerButton.setOnClickListener(createOnClickListener(onClickListener));
        setOnRequireScrollStateChangedListener(new OnRequireScrollStateChangedListener() {
            public void onRequireScrollStateChanged(boolean z) {
                footerButton.setText(z ? charSequence : text);
            }
        });
        requireScroll();
    }

    public void requireScroll() {
        this.delegate.startListening();
    }

    /* access modifiers changed from: package-private */
    public void notifyScrollabilityChange(boolean z) {
        if (z != this.requiringScrollToBottom) {
            if (!z) {
                postScrollStateChange(false);
                this.requiringScrollToBottom = false;
                this.everScrolledToBottom = true;
            } else if (!this.everScrolledToBottom) {
                postScrollStateChange(true);
                this.requiringScrollToBottom = true;
            }
        }
    }

    private void postScrollStateChange(final boolean z) {
        this.handler.post(new Runnable() {
            public void run() {
                if (RequireScrollMixin.this.listener != null) {
                    RequireScrollMixin.this.listener.onRequireScrollStateChanged(z);
                }
            }
        });
    }
}
