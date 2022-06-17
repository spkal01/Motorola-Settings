package com.motorola.settings.display;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.ScreenZoomSettings;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public abstract class PreviewTabLayoutPreferenceFragment extends SettingsPreferenceFragment {
    private static final Interpolator FADE_IN_INTERPOLATOR = new DecelerateInterpolator();
    private static final Interpolator FADE_OUT_INTERPOLATOR = new AccelerateInterpolator();
    private View mContent;
    protected int mCurrentIndex;
    protected String[] mEntries;
    protected int[] mIconRes;
    protected int mInitialIndex;
    /* access modifiers changed from: private */
    public long mLastCommitTime;
    private final TabLayout.OnTabSelectedListener mOnTabSelectedListener = new onPreviewTabChangeListener();
    private FrameLayout mPreviewFrames;
    private TabLayout mTabLayout;
    private boolean[] mViewStubInflated;

    /* access modifiers changed from: protected */
    public abstract void commit();

    /* access modifiers changed from: protected */
    public abstract Configuration createConfig(Configuration configuration, int i);

    /* access modifiers changed from: protected */
    public abstract int getActivityLayoutResId();

    /* access modifiers changed from: protected */
    public abstract int getPreviewSampleResIds();

    private class onPreviewTabChangeListener implements TabLayout.OnTabSelectedListener {
        private final Choreographer.FrameCallback mCommit;
        private long mCommitDelayMs;

        public void onTabReselected(TabLayout.Tab tab) {
        }

        public void onTabUnselected(TabLayout.Tab tab) {
        }

        private onPreviewTabChangeListener() {
            this.mCommitDelayMs = 200;
            this.mCommit = new C1914x22dc70c1(this);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(long j) {
            PreviewTabLayoutPreferenceFragment.this.commit();
            long unused = PreviewTabLayoutPreferenceFragment.this.mLastCommitTime = SystemClock.elapsedRealtime();
        }

        public void onTabSelected(TabLayout.Tab tab) {
            PreviewTabLayoutPreferenceFragment.this.setPreviewLayer(tab.getPosition(), false);
            if (!(PreviewTabLayoutPreferenceFragment.this.getActivity() instanceof MotoDisplaySettingsActivity)) {
                commitOnNextFrame();
            }
        }

        private void commitOnNextFrame() {
            Choreographer instance = Choreographer.getInstance();
            instance.removeFrameCallback(this.mCommit);
            instance.postFrameCallbackDelayed(this.mCommit, this.mCommitDelayMs);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong("mLastCommitTime", this.mLastCommitTime);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (bundle != null) {
            this.mLastCommitTime = bundle.getLong("mLastCommitTime");
        }
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(16908351);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(getActivityLayoutResId(), viewGroup2, false);
        this.mContent = inflate;
        viewGroup2.addView(inflate);
        return onCreateView;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity().getResources().getConfiguration().orientation == 2) {
            FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(C1985R$id.content_frame);
            ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
            layoutParams.height = -1;
            frameLayout.setLayoutParams(layoutParams);
        }
        if (this instanceof ScreenZoomSettings) {
            setWallPaperBitmap((ImageView) view.findViewById(C1985R$id.wallpaper_bg));
        }
        this.mTabLayout = (TabLayout) this.mContent.findViewById(C1985R$id.tab_layout);
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= this.mIconRes.length) {
                break;
            }
            TabLayout.Tab newTab = this.mTabLayout.newTab();
            View inflate = View.inflate(getContext(), C1987R$layout.display_size_tab, (ViewGroup) null);
            ImageView imageView = (ImageView) inflate.findViewById(C1985R$id.icon);
            imageView.setBackgroundResource(C1983R$drawable.ic_font_size_tab_personalize_bg);
            imageView.setImageResource(this.mIconRes[i]);
            ((TextView) inflate.findViewById(C1985R$id.text1)).setText(this.mEntries[i]);
            newTab.setCustomView(inflate);
            TabLayout tabLayout = this.mTabLayout;
            if (i != this.mInitialIndex) {
                z = false;
            }
            tabLayout.addTab(newTab, z);
            i++;
        }
        Context context = getContext();
        Configuration configuration = context.getResources().getConfiguration();
        Configuration[] configurationArr = new Configuration[this.mIconRes.length];
        for (int i2 = 0; i2 < this.mIconRes.length; i2++) {
            configurationArr[i2] = createConfig(configuration, i2);
        }
        initPreviewFrame(context, getPreviewSampleResIds(), configurationArr);
        if (getActivity() instanceof MotoDisplaySettingsActivity) {
            ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(C1985R$id.content_parent);
            FloatingActionButton floatingActionButton = (FloatingActionButton) LayoutInflater.from(getActivity()).inflate(C1987R$layout.settings_floating_confirm, viewGroup, false);
            floatingActionButton.setSupportImageTintMode(PorterDuff.Mode.SRC);
            viewGroup.addView(floatingActionButton);
            floatingActionButton.setOnClickListener(new PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda1(this));
        }
        int i3 = this.mInitialIndex;
        int[] iArr = this.mIconRes;
        if (i3 >= iArr.length) {
            setPreviewLayer(iArr.length - 1, false);
            Choreographer.getInstance().postFrameCallbackDelayed(new PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda0(this), 400);
            return;
        }
        setPreviewLayer(i3, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(View view) {
        commit();
        getActivity().finish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$1(long j) {
        commit();
    }

    public void onStart() {
        super.onStart();
        this.mTabLayout.getTabAt(this.mCurrentIndex).select();
        this.mTabLayout.addOnTabSelectedListener(this.mOnTabSelectedListener);
    }

    public void onStop() {
        super.onStop();
        this.mTabLayout.removeOnTabSelectedListener(this.mOnTabSelectedListener);
    }

    /* access modifiers changed from: private */
    public void setPreviewLayer(int i, boolean z) {
        setPreviewLayer(i, this.mCurrentIndex, z);
        this.mCurrentIndex = i;
    }

    public void initPreviewFrame(Context context, int i, Configuration[] configurationArr) {
        this.mViewStubInflated = new boolean[configurationArr.length];
        FrameLayout frameLayout = new FrameLayout(context);
        this.mPreviewFrames = frameLayout;
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.mPreviewFrames.setClipToPadding(true);
        this.mPreviewFrames.setClipChildren(true);
        for (int i2 = 0; i2 < configurationArr.length; i2++) {
            Context createConfigurationContext = context.createConfigurationContext(configurationArr[i2]);
            createConfigurationContext.getTheme().setTo(context.getTheme());
            ViewStub viewStub = new ViewStub(createConfigurationContext);
            viewStub.setLayoutResource(i);
            viewStub.setOnInflateListener(new PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda2(this, i2));
            this.mPreviewFrames.addView(viewStub);
        }
        ((FrameLayout) this.mContent.findViewById(C1985R$id.preview_pager)).addView(this.mPreviewFrames);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initPreviewFrame$2(int i, ViewStub viewStub, View view) {
        view.setVisibility(viewStub.getVisibility());
        this.mViewStubInflated[i] = true;
    }

    public void setPreviewLayer(int i, int i2, boolean z) {
        if (i2 >= 0) {
            View childAt = this.mPreviewFrames.getChildAt(i2);
            if (this.mViewStubInflated[i2]) {
                setVisibility(childAt, 4, z);
            }
        }
        View childAt2 = this.mPreviewFrames.getChildAt(i);
        if (!this.mViewStubInflated[i]) {
            childAt2 = ((ViewStub) childAt2).inflate();
            childAt2.setAlpha(0.0f);
        }
        setVisibility(childAt2, 0, z);
    }

    private void setWallPaperBitmap(ImageView imageView) {
        ThreadUtils.postOnBackgroundThread((Runnable) new PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda4(this, imageView));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setWallPaperBitmap$4(ImageView imageView) {
        Bitmap bitmap;
        Bitmap bitmap2;
        WallpaperManager instance = WallpaperManager.getInstance(getContext());
        WallpaperInfo wallpaperInfo = instance.getWallpaperInfo();
        if (wallpaperInfo != null) {
            bitmap = ((BitmapDrawable) wallpaperInfo.loadThumbnail(getContext().getPackageManager())).getBitmap();
        } else {
            bitmap = instance.getBitmap(1);
        }
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = Math.min(displayMetrics.heightPixels, displayMetrics.widthPixels);
        int max = Math.max(displayMetrics.heightPixels, displayMetrics.widthPixels);
        if (width < height) {
            int i = (int) (((((float) width) * 1.0f) * ((float) max)) / ((float) min));
            if (i > height) {
                bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            } else {
                bitmap2 = Bitmap.createBitmap(bitmap, 0, Math.abs((i - height) / 2), width, i);
            }
        } else {
            bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, (int) (((((float) height) * 1.0f) * ((float) min)) / ((float) max)), height);
        }
        ThreadUtils.postOnMainThread(new PreviewTabLayoutPreferenceFragment$$ExternalSyntheticLambda3(imageView, bitmap2));
    }

    private void setVisibility(final View view, final int i, boolean z) {
        float f = i == 0 ? 1.0f : 0.0f;
        if (!z) {
            view.setAlpha(f);
            view.setVisibility(i);
            return;
        }
        Interpolator interpolator = FADE_IN_INTERPOLATOR;
        if (i == 0) {
            view.animate().alpha(f).setInterpolator(interpolator).setDuration(400).withStartAction(new Runnable() {
                public void run() {
                    view.setVisibility(i);
                }
            });
        } else {
            view.animate().alpha(f).setInterpolator(FADE_OUT_INTERPOLATOR).setDuration(400).withEndAction(new Runnable() {
                public void run() {
                    view.setVisibility(i);
                }
            });
        }
    }
}
