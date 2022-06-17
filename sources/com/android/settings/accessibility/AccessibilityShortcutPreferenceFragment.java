package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.BreakIterator;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceCategory;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.utils.LocaleUtils;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.Locale;

public abstract class AccessibilityShortcutPreferenceFragment extends DashboardFragment implements ShortcutPreference.OnClickCallback {
    private CheckBox mHardwareTypeCheckBox;
    protected int mSavedCheckBoxValue = -1;
    private SettingsContentObserver mSettingsContentObserver;
    protected ShortcutPreference mShortcutPreference;
    private CheckBox mSoftwareTypeCheckBox;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;

    private boolean hasShortcutType(int i, int i2) {
        return (i & i2) == i2;
    }

    /* access modifiers changed from: protected */
    public abstract ComponentName getComponentName();

    public int getDialogMetricsCategory(int i) {
        if (i != 1) {
            return i != 1008 ? 0 : 1810;
        }
        return 1812;
    }

    /* access modifiers changed from: protected */
    public abstract CharSequence getLabelName();

    /* access modifiers changed from: protected */
    public String getShortcutPreferenceKey() {
        return "shortcut_preference";
    }

    /* access modifiers changed from: protected */
    public boolean showGeneralCategory() {
        return false;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null && bundle.containsKey("shortcut_type")) {
            this.mSavedCheckBoxValue = bundle.getInt("shortcut_type", -1);
        }
        if (getPreferenceScreenResId() <= 0) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPrefContext()));
        }
        if (showGeneralCategory()) {
            initGeneralCategory();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(new Handler(), arrayList) {
            public void onChange(boolean z, Uri uri) {
                AccessibilityShortcutPreferenceFragment.this.updateShortcutPreferenceData();
                AccessibilityShortcutPreferenceFragment.this.updateShortcutPreference();
            }
        };
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), (AttributeSet) null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle((CharSequence) getString(C1992R$string.accessibility_shortcut_title, getLabelName()));
        getPreferenceScreen().addPreference(this.mShortcutPreference);
        this.mTouchExplorationStateChangeListener = new C0608x835f3310(this);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.register(getContentResolver());
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    public void onSaveInstanceState(Bundle bundle) {
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        if (shortcutTypeCheckBoxValue != -1) {
            bundle.putInt("shortcut_type", shortcutTypeCheckBoxValue);
        }
        super.onSaveInstanceState(bundle);
    }

    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            String string = getPrefContext().getString(C1992R$string.accessibility_shortcut_title, new Object[]{getLabelName()});
            boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
            AlertDialog showEditShortcutDialog = AccessibilityDialogUtils.showEditShortcutDialog(getPrefContext(), isAnySetupWizard ? 1 : 0, string, new C0606x835f330e(this));
            setupEditShortcutDialog(showEditShortcutDialog);
            return showEditShortcutDialog;
        } else if (i == 1008) {
            AlertDialog createAccessibilityTutorialDialog = AccessibilityGestureNavigationTutorial.createAccessibilityTutorialDialog(getPrefContext(), getUserShortcutTypes());
            createAccessibilityTutorialDialog.setCanceledOnTouchOutside(false);
            return createAccessibilityTutorialDialog;
        } else {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
    }

    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showDialog(1);
    }

    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        if (getComponentName() != null) {
            int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1);
            if (shortcutPreference.isChecked()) {
                AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, getComponentName());
                showDialog(1008);
            } else {
                AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, getComponentName());
            }
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }

    /* access modifiers changed from: package-private */
    public void setupEditShortcutDialog(Dialog dialog) {
        View findViewById = dialog.findViewById(C1985R$id.software_shortcut);
        int i = C1985R$id.checkbox;
        CheckBox checkBox = (CheckBox) findViewById.findViewById(i);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = dialog.findViewById(C1985R$id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(i);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        updateEditShortcutDialogCheckBox();
    }

    /* access modifiers changed from: protected */
    public int getShortcutTypeCheckBoxValue() {
        CheckBox checkBox = this.mSoftwareTypeCheckBox;
        if (checkBox == null || this.mHardwareTypeCheckBox == null) {
            return -1;
        }
        boolean isChecked = checkBox.isChecked();
        return this.mHardwareTypeCheckBox.isChecked() ? isChecked | true ? 1 : 0 : isChecked ? 1 : 0;
    }

    /* access modifiers changed from: protected */
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), getComponentName());
    }

    /* access modifiers changed from: protected */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        if (getComponentName() != null) {
            int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
            saveNonEmptyUserShortcutType(shortcutTypeCheckBoxValue);
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), shortcutTypeCheckBoxValue, getComponentName());
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), ~shortcutTypeCheckBoxValue, getComponentName());
            this.mShortcutPreference.setChecked(shortcutTypeCheckBoxValue != 0);
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }

    /* access modifiers changed from: package-private */
    public void initGeneralCategory() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("general_categories");
        preferenceCategory.setTitle(getGeneralCategoryDescription((CharSequence) null));
        getPreferenceScreen().addPreference(preferenceCategory);
    }

    /* access modifiers changed from: package-private */
    public void saveNonEmptyUserShortcutType(int i) {
        if (i != 0) {
            PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(getComponentName().flattenToString(), i));
        }
    }

    /* access modifiers changed from: protected */
    public CharSequence getGeneralCategoryDescription(CharSequence charSequence) {
        if (charSequence == null || charSequence.toString().isEmpty()) {
            return getContext().getString(C1992R$string.accessibility_screen_option);
        }
        return charSequence;
    }

    private void setDialogTextAreaClickListener(View view, CheckBox checkBox) {
        view.findViewById(C1985R$id.container).setOnClickListener(new C0607x835f330f(checkBox));
    }

    /* access modifiers changed from: protected */
    public CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isSettingsEditable()) {
            return context.getText(C1992R$string.accessibility_shortcut_edit_dialog_title_hardware);
        }
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(C1992R$string.switch_off_text);
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(context, getComponentName().flattenToString(), 1);
        ArrayList arrayList = new ArrayList();
        CharSequence text = context.getText(C1992R$string.accessibility_shortcut_edit_summary_software);
        if (hasShortcutType(retrieveUserShortcutType, 1)) {
            arrayList.add(text);
        }
        if (hasShortcutType(retrieveUserShortcutType, 2)) {
            arrayList.add(context.getText(C1992R$string.accessibility_shortcut_hardware_keyword));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(text);
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), (BreakIterator) null, LocaleUtils.getConcatenatedString(arrayList));
    }

    private void updateEditShortcutDialogCheckBox() {
        int restoreOnConfigChangedValue = restoreOnConfigChangedValue();
        if (restoreOnConfigChangedValue == -1) {
            restoreOnConfigChangedValue = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1);
            if (!this.mShortcutPreference.isChecked()) {
                restoreOnConfigChangedValue = 0;
            }
        }
        this.mSoftwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 1));
        this.mHardwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 2));
    }

    private int restoreOnConfigChangedValue() {
        int i = this.mSavedCheckBoxValue;
        this.mSavedCheckBoxValue = -1;
        return i;
    }

    /* access modifiers changed from: protected */
    public void updateShortcutPreferenceData() {
        int userShortcutTypesFromSettings;
        if (getComponentName() != null && (userShortcutTypesFromSettings = AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), getComponentName())) != 0) {
            PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(getComponentName().flattenToString(), userShortcutTypesFromSettings));
        }
    }

    /* access modifiers changed from: protected */
    public void updateShortcutPreference() {
        if (getComponentName() != null) {
            this.mShortcutPreference.setChecked(AccessibilityUtil.hasValuesInSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1), getComponentName()));
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }
}
