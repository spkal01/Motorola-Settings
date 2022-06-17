package com.android.settings.accessibility;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Preconditions;
import androidx.core.widget.TextViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import java.util.ArrayList;
import java.util.List;

public final class AccessibilityGestureNavigationTutorial {
    private static final DialogInterface.OnClickListener mOnClickListener = AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda0.INSTANCE;

    public static void showGestureNavigationSettingsTutorialDialog(Context context, DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog create = new AlertDialog.Builder(context).setView(createTutorialDialogContentView(context, 2)).setNegativeButton(C1992R$string.accessibility_tutorial_dialog_button, mOnClickListener).setOnDismissListener(onDismissListener).create();
        create.requestWindowFeature(1);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    static AlertDialog showGestureNavigationTutorialDialog(Context context) {
        return createDialog(context, 1);
    }

    static AlertDialog createAccessibilityTutorialDialog(Context context, int i) {
        return new AlertDialog.Builder(context).setView(createShortcutNavigationContentView(context, i)).setNegativeButton(C1992R$string.accessibility_tutorial_dialog_button, mOnClickListener).create();
    }

    private static View createTutorialDialogContentView(Context context, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i == 0) {
            return layoutInflater.inflate(C1987R$layout.tutorial_dialog_launch_service_by_accessibility_button, (ViewGroup) null);
        }
        if (i == 1) {
            View inflate = layoutInflater.inflate(C1987R$layout.tutorial_dialog_launch_service_by_gesture_navigation, (ViewGroup) null);
            TextureView textureView = (TextureView) inflate.findViewById(C1985R$id.gesture_tutorial_video);
            TextView textView = (TextView) inflate.findViewById(C1985R$id.gesture_tutorial_message);
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i2 = C1991R$raw.illustration_accessibility_gesture_three_finger;
            } else {
                i2 = C1991R$raw.illustration_accessibility_gesture_two_finger;
            }
            VideoPlayer.create(context, i2, textureView);
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i3 = C1992R$string.accessibility_tutorial_dialog_message_gesture_talkback;
            } else {
                i3 = C1992R$string.accessibility_tutorial_dialog_message_gesture;
            }
            textView.setText(i3);
            return inflate;
        } else if (i != 2) {
            return null;
        } else {
            View inflate2 = layoutInflater.inflate(C1987R$layout.tutorial_dialog_launch_by_gesture_navigation_settings, (ViewGroup) null);
            TextureView textureView2 = (TextureView) inflate2.findViewById(C1985R$id.gesture_tutorial_video);
            TextView textView2 = (TextView) inflate2.findViewById(C1985R$id.gesture_tutorial_message);
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i4 = C1991R$raw.illustration_accessibility_gesture_three_finger;
            } else {
                i4 = C1991R$raw.illustration_accessibility_gesture_two_finger;
            }
            VideoPlayer.create(context, i4, textureView2);
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i5 = C1992R$string.accessibility_tutorial_dialog_message_gesture_settings_talkback;
            } else {
                i5 = C1992R$string.accessibility_tutorial_dialog_message_gesture_settings;
            }
            textView2.setText(i5);
            return inflate2;
        }
    }

    private static AlertDialog createDialog(Context context, int i) {
        AlertDialog create = new AlertDialog.Builder(context).setView(createTutorialDialogContentView(context, i)).setNegativeButton(C1992R$string.accessibility_tutorial_dialog_button, mOnClickListener).create();
        create.requestWindowFeature(1);
        create.setCanceledOnTouchOutside(false);
        create.show();
        return create;
    }

    private static class TutorialPagerAdapter extends PagerAdapter {
        private final List<TutorialPage> mTutorialPages;

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private TutorialPagerAdapter(List<TutorialPage> list) {
            this.mTutorialPages = list;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            ImageView imageView = this.mTutorialPages.get(i).getImageView();
            viewGroup.addView(imageView);
            return imageView;
        }

        public int getCount() {
            return this.mTutorialPages.size();
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView(this.mTutorialPages.get(i).getImageView());
        }
    }

    private static ImageView createImageView(Context context, int i) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(i);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    private static View createShortcutNavigationContentView(Context context, int i) {
        View inflate = ((LayoutInflater) context.getSystemService(LayoutInflater.class)).inflate(C1987R$layout.accessibility_shortcut_tutorial_dialog, (ViewGroup) null);
        List<TutorialPage> createShortcutTutorialPages = createShortcutTutorialPages(context, i);
        int i2 = 1;
        Preconditions.checkArgument(!createShortcutTutorialPages.isEmpty(), "Unexpected tutorial pages size");
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(C1985R$id.indicator_container);
        linearLayout.setVisibility(createShortcutTutorialPages.size() > 1 ? 0 : 8);
        for (TutorialPage indicatorIcon : createShortcutTutorialPages) {
            linearLayout.addView(indicatorIcon.getIndicatorIcon());
        }
        createShortcutTutorialPages.get(0).getIndicatorIcon().setEnabled(true);
        TextSwitcher textSwitcher = (TextSwitcher) inflate.findViewById(C1985R$id.title);
        textSwitcher.setFactory(new AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda2(context));
        textSwitcher.setText(createShortcutTutorialPages.get(0).getTitle());
        TextSwitcher textSwitcher2 = (TextSwitcher) inflate.findViewById(C1985R$id.instruction);
        textSwitcher2.setFactory(new AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda1(context));
        textSwitcher2.setText(createShortcutTutorialPages.get(0).getInstruction());
        ViewPager viewPager = (ViewPager) inflate.findViewById(C1985R$id.view_pager);
        viewPager.setAdapter(new TutorialPagerAdapter(createShortcutTutorialPages));
        viewPager.setContentDescription(context.getString(C1992R$string.accessibility_tutorial_pager, new Object[]{1, Integer.valueOf(createShortcutTutorialPages.size())}));
        if (createShortcutTutorialPages.size() <= 1) {
            i2 = 4;
        }
        viewPager.setImportantForAccessibility(i2);
        viewPager.addOnPageChangeListener(new TutorialPageChangeListener(context, viewPager, textSwitcher, textSwitcher2, createShortcutTutorialPages));
        return inflate;
    }

    /* access modifiers changed from: private */
    public static View makeTitleView(Context context) {
        TextView textView = new TextView(context);
        TextViewCompat.setTextAppearance(textView, C1993R$style.AccessibilityDialogTitle);
        textView.setGravity(17);
        return textView;
    }

    /* access modifiers changed from: private */
    public static View makeInstructionView(Context context) {
        TextView textView = new TextView(context);
        TextViewCompat.setTextAppearance(textView, C1993R$style.AccessibilityDialogDescription);
        return textView;
    }

    private static TutorialPage createSoftwareTutorialPage(Context context) {
        CharSequence text = context.getText(C1992R$string.accessibility_tutorial_dialog_title_button);
        ImageView createSoftwareImage = createSoftwareImage(context);
        CharSequence softwareInstruction = getSoftwareInstruction(context);
        ImageView createImageView = createImageView(context, C1983R$drawable.ic_accessibility_page_indicator);
        createImageView.setEnabled(false);
        return new TutorialPage(text, createSoftwareImage, createImageView, softwareInstruction);
    }

    private static TutorialPage createHardwareTutorialPage(Context context) {
        CharSequence text = context.getText(C1992R$string.accessibility_tutorial_dialog_title_volume);
        ImageView createImageView = createImageView(context, C1983R$drawable.accessibility_shortcut_type_hardware);
        ImageView createImageView2 = createImageView(context, C1983R$drawable.ic_accessibility_page_indicator);
        CharSequence text2 = context.getText(C1992R$string.accessibility_tutorial_dialog_message_volume);
        createImageView2.setEnabled(false);
        return new TutorialPage(text, createImageView, createImageView2, text2);
    }

    private static TutorialPage createTripleTapTutorialPage(Context context) {
        CharSequence text = context.getText(C1992R$string.accessibility_tutorial_dialog_title_triple);
        ImageView createImageView = createImageView(context, C1983R$drawable.accessibility_shortcut_type_triple_tap);
        CharSequence text2 = context.getText(C1992R$string.accessibility_tutorial_dialog_message_triple);
        ImageView createImageView2 = createImageView(context, C1983R$drawable.ic_accessibility_page_indicator);
        createImageView2.setEnabled(false);
        return new TutorialPage(text, createImageView, createImageView2, text2);
    }

    static List<TutorialPage> createShortcutTutorialPages(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if ((i & 1) == 1) {
            arrayList.add(createSoftwareTutorialPage(context));
        }
        if ((i & 2) == 2) {
            arrayList.add(createHardwareTutorialPage(context));
        }
        if ((i & 4) == 4) {
            arrayList.add(createTripleTapTutorialPage(context));
        }
        return arrayList;
    }

    private static ImageView createSoftwareImage(Context context) {
        int i;
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            i = C1983R$drawable.accessibility_shortcut_type_software_floating;
        } else {
            i = C1983R$drawable.accessibility_shortcut_type_software;
        }
        return createImageView(context, i);
    }

    private static CharSequence getSoftwareInstruction(Context context) {
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            return context.getText(C1992R$string.accessibility_tutorial_dialog_message_floating_button);
        }
        return getSoftwareInstructionWithIcon(context, context.getText(C1992R$string.accessibility_tutorial_dialog_message_button));
    }

    private static CharSequence getSoftwareInstructionWithIcon(Context context, CharSequence charSequence) {
        String charSequence2 = charSequence.toString();
        SpannableString valueOf = SpannableString.valueOf(charSequence2);
        int indexOf = charSequence2.indexOf("%s");
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(context.getDrawable(C1983R$drawable.ic_accessibility_new));
        Drawable mutate = imageView.getDrawable().mutate();
        ImageSpan imageSpan = new ImageSpan(mutate);
        imageSpan.setContentDescription("");
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
        valueOf.setSpan(imageSpan, indexOf, indexOf + 2, 33);
        return valueOf;
    }

    private static class TutorialPage {
        private final ImageView mImageView;
        private final ImageView mIndicatorIcon;
        private final CharSequence mInstruction;
        private final CharSequence mTitle;

        TutorialPage(CharSequence charSequence, ImageView imageView, ImageView imageView2, CharSequence charSequence2) {
            this.mTitle = charSequence;
            this.mImageView = imageView;
            this.mIndicatorIcon = imageView2;
            this.mInstruction = charSequence2;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public ImageView getImageView() {
            return this.mImageView;
        }

        public ImageView getIndicatorIcon() {
            return this.mIndicatorIcon;
        }

        public CharSequence getInstruction() {
            return this.mInstruction;
        }
    }

    private static class TutorialPageChangeListener implements ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TextSwitcher mInstruction;
        private int mLastTutorialPagePosition = 0;
        private final TextSwitcher mTitle;
        private final List<TutorialPage> mTutorialPages;
        private final ViewPager mViewPager;

        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        TutorialPageChangeListener(Context context, ViewPager viewPager, ViewGroup viewGroup, ViewGroup viewGroup2, List<TutorialPage> list) {
            this.mContext = context;
            this.mViewPager = viewPager;
            this.mTitle = (TextSwitcher) viewGroup;
            this.mInstruction = (TextSwitcher) viewGroup2;
            this.mTutorialPages = list;
        }

        public void onPageSelected(int i) {
            boolean z = this.mLastTutorialPagePosition > i;
            int i2 = z ? 17432578 : 17432741;
            int i3 = z ? 17432579 : 17432744;
            this.mTitle.setInAnimation(this.mContext, i2);
            this.mTitle.setOutAnimation(this.mContext, i3);
            this.mTitle.setText(this.mTutorialPages.get(i).getTitle());
            this.mInstruction.setInAnimation(this.mContext, i2);
            this.mInstruction.setOutAnimation(this.mContext, i3);
            this.mInstruction.setText(this.mTutorialPages.get(i).getInstruction());
            for (TutorialPage indicatorIcon : this.mTutorialPages) {
                indicatorIcon.getIndicatorIcon().setEnabled(false);
            }
            this.mTutorialPages.get(i).getIndicatorIcon().setEnabled(true);
            this.mLastTutorialPagePosition = i;
            this.mViewPager.setContentDescription(this.mContext.getString(C1992R$string.accessibility_tutorial_pager, new Object[]{Integer.valueOf(i + 1), Integer.valueOf(this.mTutorialPages.size())}));
        }
    }
}
