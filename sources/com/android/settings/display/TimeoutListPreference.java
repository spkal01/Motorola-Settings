package com.android.settings.display;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.RestrictedListPreference;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;

public class TimeoutListPreference extends RestrictedListPreference {
    /* access modifiers changed from: private */
    public RestrictedLockUtils.EnforcedAdmin mAdmin;
    private final CharSequence[] mInitialEntries = getEntries();
    private final CharSequence[] mInitialValues = getEntryValues();
    private long mMaxTimeout;

    public TimeoutListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        if (this.mAdmin != null) {
            builder.setView(C1987R$layout.admin_disabled_other_options_footer);
        } else {
            builder.setView((View) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogCreated(Dialog dialog) {
        super.onDialogCreated(dialog);
        dialog.create();
        if (this.mAdmin != null) {
            dialog.findViewById(C1985R$id.admin_disabled_other_options).findViewById(C1985R$id.admin_more_details_link).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(TimeoutListPreference.this.getContext(), TimeoutListPreference.this.mAdmin);
                }
            });
        }
    }

    public String getMaxTimeoutString() {
        long j = this.mMaxTimeout;
        if (j == 0) {
            return null;
        }
        return getTimeoutString(j);
    }

    public boolean isDevicePolicyEnforced() {
        return this.mAdmin != null;
    }

    public long getMaxTimeout() {
        return this.mMaxTimeout;
    }

    public void removeUnusableTimeouts(long j, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, boolean z) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService("device_policy");
        int parseInt = Integer.parseInt(getValue());
        if (devicePolicyManager != null) {
            if (enforcedAdmin != null || this.mAdmin != null || isDisabledByAdmin()) {
                if (enforcedAdmin == null) {
                    j = Long.MAX_VALUE;
                }
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.mInitialValues;
                    if (i >= charSequenceArr.length) {
                        break;
                    }
                    if (Long.parseLong(charSequenceArr[i].toString()) <= j) {
                        arrayList.add(this.mInitialEntries[i]);
                        arrayList2.add(this.mInitialValues[i]);
                    }
                    i++;
                }
                if (arrayList2.size() == 0) {
                    setDisabledByAdmin(enforcedAdmin);
                    return;
                }
                setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin) null);
                if (arrayList.size() != getEntries().length) {
                    if (z && !arrayList2.contains(String.valueOf(j))) {
                        this.mMaxTimeout = j;
                        arrayList.add(getContext().getString(C1992R$string.security_lock_screen_max, new Object[]{getMaxTimeoutString()}));
                        arrayList2.add(String.valueOf(this.mMaxTimeout));
                    }
                    setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
                    setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
                    this.mAdmin = enforcedAdmin;
                }
                long j2 = (long) parseInt;
                if (j2 <= j) {
                    if (!isTimeoutInDefaultList(j2) && !arrayList2.contains(String.valueOf(parseInt))) {
                        addCurrentSelectionToTimeoutsList(j2);
                    }
                    setValue(String.valueOf(parseInt));
                } else if (arrayList2.size() <= 0 || Long.parseLong(((CharSequence) arrayList2.get(arrayList2.size() - 1)).toString()) != j) {
                    Log.w("TimeoutListPreference", "Default to longest timeout. Value disabled by admin:" + parseInt);
                    setValue(((CharSequence) arrayList2.get(arrayList2.size() - 1)).toString());
                } else {
                    setValue(String.valueOf(j));
                }
            }
        }
    }

    public void addCurrentSelectionToTimeoutsList(long j) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        int i = 0;
        while (i < entryValues.length && j > Long.parseLong(entryValues[i].toString())) {
            arrayList.add(entries[i]);
            arrayList2.add(entryValues[i]);
            i++;
        }
        arrayList.add(getTimeoutString(j));
        arrayList2.add(String.valueOf(j));
        if (i < entryValues.length) {
            while (i < entryValues.length) {
                arrayList.add(entries[i]);
                arrayList2.add(entryValues[i]);
                i++;
            }
        }
        setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
        setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
    }

    public void resetTimeoutList() {
        setEntries(this.mInitialEntries);
        setEntryValues(this.mInitialValues);
    }

    public boolean isTimeoutInDefaultList(long j) {
        int i = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.mInitialValues;
            if (i >= charSequenceArr.length) {
                return false;
            }
            if (Long.parseLong(charSequenceArr[i].toString()) == j) {
                return true;
            }
            i++;
        }
    }

    private String getTimeoutString(long j) {
        Resources resources = getContext().getResources();
        int i = ((int) (j / 1000)) % 60;
        int i2 = (int) (j / 60000);
        String quantityString = resources.getQuantityString(C1990R$plurals.Nseconds_description, i, new Object[]{Integer.valueOf(i)});
        String quantityString2 = resources.getQuantityString(C1990R$plurals.Nminutes_description, i2, new Object[]{Integer.valueOf(i2)});
        if (i <= 0 || i2 <= 0) {
            return i > 0 ? quantityString : quantityString2;
        }
        return resources.getString(C1992R$string.minutes_and_seconds, new Object[]{quantityString2, quantityString});
    }
}
