package com.android.settings.users;

import android.content.pm.UserInfo;
import java.util.function.Predicate;

public final /* synthetic */ class UserSettings$$ExternalSyntheticLambda8 implements Predicate {
    public static final /* synthetic */ UserSettings$$ExternalSyntheticLambda8 INSTANCE = new UserSettings$$ExternalSyntheticLambda8();

    private /* synthetic */ UserSettings$$ExternalSyntheticLambda8() {
    }

    public final boolean test(Object obj) {
        return UserSettings.lambda$getRealUsersCount$6((UserInfo) obj);
    }
}
