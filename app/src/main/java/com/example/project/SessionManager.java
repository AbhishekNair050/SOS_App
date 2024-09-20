package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SOS_MESSAGE = "sosMessage";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void saveSOSMessage(String message) {
        editor.putString(KEY_SOS_MESSAGE, message);
        editor.commit();
    }

    public String getSOSMessage() {
        return pref.getString(KEY_SOS_MESSAGE, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}