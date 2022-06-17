package com.android.settings.users;

import android.content.pm.UserInfo;
import java.util.function.Predicate;

public final /* synthetic */ class UserSettings$$ExternalSyntheticLambda7 implements Predicate {
    public static final /* synthetic */ UserSettings$$ExternalSyntheticLambda7 INSTANCE = new UserSettings$$ExternalSyntheticLambda7();

    private /* synthetic */ UserSettings$$ExternalSyntheticLambda7() {
    }

    public final boolean test(Object obj) {
        return ((UserInfo) obj).isGuest();
    }
}
