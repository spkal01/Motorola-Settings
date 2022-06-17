package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.accessibility.ItemInfoArrayAdapter;
import com.android.settings.utils.AnnotationSpan;
import java.util.List;

public class AccessibilityDialogUtils {

    interface CustomButtonsClickListener {
        void onClick(int i);
    }

    public static AlertDialog showEditShortcutDialog(Context context, int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog createDialog = createDialog(context, i, charSequence, onClickListener);
        createDialog.show();
        setScrollIndicators(createDialog);
        return createDialog;
    }

    public static Dialog createMagnificationSwitchShortcutDialog(Context context, CustomButtonsClickListener customButtonsClickListener) {
        View createSwitchShortcutDialogContentView = createSwitchShortcutDialogContentView(context);
        AlertDialog create = new AlertDialog.Builder(context).setView(createSwitchShortcutDialogContentView).setTitle((CharSequence) context.getString(C1992R$string.accessibility_magnification_switch_shortcut_title)).create();
        setCustomButtonsClickListener(create, createSwitchShortcutDialogContentView, customButtonsClickListener, (CustomButtonsClickListener) null);
        setScrollIndicators(createSwitchShortcutDialogContentView);
        return create;
    }

    private static AlertDialog createDialog(Context context, int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setView(createEditDialogContentView(context, i)).setTitle(charSequence).setPositiveButton(C1992R$string.save, onClickListener).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) AccessibilityDialogUtils$$ExternalSyntheticLambda0.INSTANCE).create();
    }

    private static void setScrollIndicators(AlertDialog alertDialog) {
        setScrollIndicators((View) (ScrollView) alertDialog.findViewById(C1985R$id.container_layout));
    }

    private static void setScrollIndicators(View view) {
        view.setScrollIndicators(3, 3);
    }

    private static void setCustomButtonsClickListener(Dialog dialog, View view, CustomButtonsClickListener customButtonsClickListener, CustomButtonsClickListener customButtonsClickListener2) {
        Button button = (Button) view.findViewById(C1985R$id.custom_positive_button);
        Button button2 = (Button) view.findViewById(C1985R$id.custom_negative_button);
        if (button != null) {
            button.setOnClickListener(new AccessibilityDialogUtils$$ExternalSyntheticLambda4(customButtonsClickListener, dialog));
        }
        if (button2 != null) {
            button2.setOnClickListener(new AccessibilityDialogUtils$$ExternalSyntheticLambda3(customButtonsClickListener2, dialog));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setCustomButtonsClickListener$1(CustomButtonsClickListener customButtonsClickListener, Dialog dialog, View view) {
        if (customButtonsClickListener != null) {
            customButtonsClickListener.onClick(1);
        }
        dialog.dismiss();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setCustomButtonsClickListener$2(CustomButtonsClickListener customButtonsClickListener, Dialog dialog, View view) {
        if (customButtonsClickListener != null) {
            customButtonsClickListener.onClick(2);
        }
        dialog.dismiss();
    }

    private static View createSwitchShortcutDialogContentView(Context context) {
        return createEditDialogContentView(context, 4);
    }

    private static View createEditDialogContentView(Context context, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i == 0) {
            View inflate = layoutInflater.inflate(C1987R$layout.accessibility_edit_shortcut, (ViewGroup) null);
            initSoftwareShortcut(context, inflate);
            initHardwareShortcut(context, inflate);
            return inflate;
        } else if (i == 1) {
            View inflate2 = layoutInflater.inflate(C1987R$layout.accessibility_edit_shortcut, (ViewGroup) null);
            initSoftwareShortcutForSUW(context, inflate2);
            initHardwareShortcut(context, inflate2);
            return inflate2;
        } else if (i == 2) {
            View inflate3 = layoutInflater.inflate(C1987R$layout.accessibility_edit_shortcut_magnification, (ViewGroup) null);
            initSoftwareShortcut(context, inflate3);
            initHardwareShortcut(context, inflate3);
            initMagnifyShortcut(context, inflate3);
            initAdvancedWidget(inflate3);
            return inflate3;
        } else if (i == 3) {
            View inflate4 = layoutInflater.inflate(C1987R$layout.accessibility_edit_shortcut_magnification, (ViewGroup) null);
            initSoftwareShortcutForSUW(context, inflate4);
            initHardwareShortcut(context, inflate4);
            initMagnifyShortcut(context, inflate4);
            initAdvancedWidget(inflate4);
            return inflate4;
        } else if (i == 4) {
            View inflate5 = layoutInflater.inflate(C1987R$layout.accessibility_edit_magnification_shortcut, (ViewGroup) null);
            ((ImageView) inflate5.findViewById(C1985R$id.image)).setImageResource(retrieveSoftwareShortcutImageResId(context));
            return inflate5;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void setupShortcutWidget(View view, CharSequence charSequence, CharSequence charSequence2, int i) {
        ((CheckBox) view.findViewById(C1985R$id.checkbox)).setText(charSequence);
        TextView textView = (TextView) view.findViewById(C1985R$id.summary);
        if (TextUtils.isEmpty(charSequence2)) {
            textView.setVisibility(8);
        } else {
            textView.setText(charSequence2);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setFocusable(false);
        }
        ((ImageView) view.findViewById(C1985R$id.image)).setImageResource(i);
    }

    private static void initSoftwareShortcutForSUW(Context context, View view) {
        View findViewById = view.findViewById(C1985R$id.software_shortcut);
        setupShortcutWidget(findViewById, context.getText(C1992R$string.accessibility_shortcut_edit_dialog_title_software), retrieveSoftwareShortcutSummaryForSUW(context, ((TextView) findViewById.findViewById(C1985R$id.summary)).getLineHeight()), retrieveSoftwareShortcutImageResId(context));
    }

    private static void initSoftwareShortcut(Context context, View view) {
        View findViewById = view.findViewById(C1985R$id.software_shortcut);
        setupShortcutWidget(findViewById, context.getText(C1992R$string.accessibility_shortcut_edit_dialog_title_software), retrieveSoftwareShortcutSummary(context, ((TextView) findViewById.findViewById(C1985R$id.summary)).getLineHeight()), retrieveSoftwareShortcutImageResId(context));
    }

    private static void initHardwareShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(C1985R$id.hardware_shortcut), context.getText(C1992R$string.accessibility_shortcut_edit_dialog_title_hardware), context.getText(C1992R$string.accessibility_shortcut_edit_dialog_summary_hardware), C1983R$drawable.accessibility_shortcut_type_hardware);
    }

    private static void initMagnifyShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(C1985R$id.triple_tap_shortcut), context.getText(C1992R$string.accessibility_shortcut_edit_dialog_title_triple_tap), MessageFormat.format(context.getString(C1992R$string.accessibility_shortcut_edit_dialog_summary_triple_tap), new Object[]{3}), C1983R$drawable.accessibility_shortcut_type_triple_tap);
    }

    private static void initAdvancedWidget(View view) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(C1985R$id.advanced_shortcut);
        linearLayout.setOnClickListener(new AccessibilityDialogUtils$$ExternalSyntheticLambda2(linearLayout, view.findViewById(C1985R$id.triple_tap_shortcut)));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$initAdvancedWidget$3(LinearLayout linearLayout, View view, View view2) {
        linearLayout.setVisibility(8);
        view.setVisibility(0);
    }

    private static CharSequence retrieveSoftwareShortcutSummaryForSUW(Context context, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!AccessibilityUtil.isFloatingMenuEnabled(context)) {
            spannableStringBuilder.append(getSummaryStringWithIcon(context, i));
        }
        return spannableStringBuilder;
    }

    private static CharSequence retrieveSoftwareShortcutSummary(Context context, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!AccessibilityUtil.isFloatingMenuEnabled(context)) {
            spannableStringBuilder.append(getSummaryStringWithIcon(context, i));
            spannableStringBuilder.append("\n\n");
        }
        spannableStringBuilder.append(getCustomizeAccessibilityButtonLink(context));
        return spannableStringBuilder;
    }

    private static int retrieveSoftwareShortcutImageResId(Context context) {
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            return C1983R$drawable.accessibility_shortcut_type_software_floating;
        }
        return C1983R$drawable.accessibility_shortcut_type_software;
    }

    private static CharSequence getCustomizeAccessibilityButtonLink(Context context) {
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo("link", new AccessibilityDialogUtils$$ExternalSyntheticLambda1(context));
        return AnnotationSpan.linkify(context.getText(C1992R$string.accessibility_shortcut_edit_dialog_summary_software_floating), linkInfo);
    }

    private static SpannableString getSummaryStringWithIcon(Context context, int i) {
        String string = context.getString(C1992R$string.accessibility_shortcut_edit_dialog_summary_software);
        SpannableString valueOf = SpannableString.valueOf(string);
        int indexOf = string.indexOf("%s");
        Drawable drawable = context.getDrawable(C1983R$drawable.ic_accessibility_new);
        ImageSpan imageSpan = new ImageSpan(drawable);
        imageSpan.setContentDescription("");
        drawable.setBounds(0, 0, i, i);
        valueOf.setSpan(imageSpan, indexOf, indexOf + 2, 33);
        return valueOf;
    }

    public static Dialog createCustomDialog(Context context, CharSequence charSequence, View view, DialogInterface.OnClickListener onClickListener) {
        AlertDialog create = new AlertDialog.Builder(context).setView(view).setTitle(charSequence).setCancelable(true).setPositiveButton(C1992R$string.save, onClickListener).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).create();
        if ((view instanceof ScrollView) || (view instanceof AbsListView)) {
            setScrollIndicators(view);
        }
        return create;
    }

    public static ListView createSingleChoiceListView(Context context, List<? extends ItemInfoArrayAdapter.ItemInfo> list, AdapterView.OnItemClickListener onItemClickListener) {
        ListView listView = new ListView(context);
        listView.setId(16908298);
        listView.setDivider((Drawable) null);
        listView.setChoiceMode(1);
        listView.setAdapter(new ItemInfoArrayAdapter(context, list));
        listView.setOnItemClickListener(onItemClickListener);
        return listView;
    }
}
