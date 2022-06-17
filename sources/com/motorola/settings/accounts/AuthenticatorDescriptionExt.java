package com.motorola.settings.accounts;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthenticatorDescriptionExt implements Parcelable {
    public static final Parcelable.Creator<AuthenticatorDescriptionExt> CREATOR = new Parcelable.Creator<AuthenticatorDescriptionExt>() {
        public AuthenticatorDescriptionExt createFromParcel(Parcel parcel) {
            return new AuthenticatorDescriptionExt(parcel);
        }

        public AuthenticatorDescriptionExt[] newArray(int i) {
            return new AuthenticatorDescriptionExt[i];
        }
    };
    public final String accountDisplayName;
    public final String editSettingActivity;
    public final boolean isAccountHidden;
    public final boolean isAuthenticatorHidden;
    public final boolean isRemoveAllowed;
    public final boolean showSyncOption;
    public final String type;

    public int describeContents() {
        return 0;
    }

    public AuthenticatorDescriptionExt(String str, boolean z, boolean z2, boolean z3, boolean z4, String str2, String str3) {
        if (str != null) {
            this.type = str;
            this.isAccountHidden = z;
            this.isAuthenticatorHidden = z2;
            this.showSyncOption = z3;
            this.isRemoveAllowed = z4;
            this.accountDisplayName = str2;
            this.editSettingActivity = str3;
            return;
        }
        throw new IllegalArgumentException("type cannot be null");
    }

    public AuthenticatorDescriptionExt(String str) {
        this.type = str;
        this.isAccountHidden = false;
        this.isAuthenticatorHidden = false;
        this.showSyncOption = true;
        this.isRemoveAllowed = true;
        this.accountDisplayName = null;
        this.editSettingActivity = null;
    }

    private AuthenticatorDescriptionExt(Parcel parcel) {
        this.type = parcel.readString();
        boolean z = false;
        this.isAccountHidden = parcel.readInt() == 1;
        this.isAuthenticatorHidden = parcel.readInt() == 1;
        this.showSyncOption = parcel.readInt() == 1;
        this.isRemoveAllowed = parcel.readInt() == 1 ? true : z;
        this.accountDisplayName = parcel.readString();
        this.editSettingActivity = parcel.readString();
    }

    public static AuthenticatorDescriptionExt newKey(String str) {
        if (str != null) {
            return new AuthenticatorDescriptionExt(str);
        }
        throw new IllegalArgumentException("type cannot be null");
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AuthenticatorDescriptionExt)) {
            return false;
        }
        return this.type.equals(((AuthenticatorDescriptionExt) obj).type);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.type);
        parcel.writeInt(this.isAccountHidden ? 1 : 0);
        parcel.writeInt(this.isAuthenticatorHidden ? 1 : 0);
        parcel.writeInt(this.showSyncOption ? 1 : 0);
        parcel.writeInt(this.isRemoveAllowed ? 1 : 0);
        parcel.writeString(this.accountDisplayName);
        parcel.writeString(this.editSettingActivity);
    }
}
