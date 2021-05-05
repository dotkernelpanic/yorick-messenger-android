package com.kernelpanic.yorickmessenger.util;

public interface Prefs {
    /***
     * SharedPreferences constants for
     * {@link com.kernelpanic.yorickmessenger.activity.NewUserSupportActivity}
     */
    String PREFERENCE_NAME_NEWUSERSUPPORT =
            "com.kernelpan1c.yorickmessenger.preferences.newUserSupport";
    String PREFERENCE_KEY_NEWUSERSUPPORT_LOCALE = "localeDefault";
    String PREFERENCE_KEY_NEWUSERSUPPORT_WATCHED = "isUserWatchedIntoActivity";

    /***
     * SharedPreferences constants for
     * {@link com.kernelpanic.yorickmessenger.activity.fragments.CreateProfileFragment}
     */
    String PREFERENCE_NAME_CREATEPROFILE =
            "com.kernelpan1c.yorickmessenger.preferences.userProfile";
    String PREFERENCE_KEY_PROFILE_CREATED =
            "isProfileCreated";

    /***
     * SharedPreferences constants for
     * {@link com.kernelpanic.yorickmessenger.activity.fragments.CreatePINCodeFragment}
     */
    String PREFERENCE_NAME_CREATEPINCODE =
            "com.kernelpan1c.yorickmessenger.preferences.createPINCode";
    String PREFERENCE_KEY_PIN_VALUE = "userPinCode";
    String PREFERENCE_KEY_PIN_CREATED = "isUserPinCodeCreated";
    String PREFERENCE_KEY_PIN_WANT = "isUserWantPinCode";

    String PREFERENCE_NAME_NIGHTMODE =
            "com.kernelpan1c.yorickmessenger.preferences.appNightMode";
    String PREFERENCE_KEY_NIGHTMODE =
            "defaultNightMode";
}
