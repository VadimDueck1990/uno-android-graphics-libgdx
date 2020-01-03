package de.hhn.aib.swlab.wise1920.group06.exercise3.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for save and read values in SharedPreferences
 * Created by andriy.
 */

public class PreferenceHelper {
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    public PreferenceHelper(Context context) {
        settings = context.getSharedPreferences("PREFS", 0);
        editor = settings.edit();
    }

    public void writeAuthToken(String authToken) {
        editor.putString("authToken", authToken);
        editor.apply();
    }

    public String readAuthToken() {
        return settings.getString("authToken", "");
    }

    public void writeID(String id) {
        editor.putString("id", id);
        editor.apply();
    }

    public String readID() {
        return settings.getString("id", "");
    }


    public void writeUsername(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public String readUsername() {
        String username = settings.getString("username", "");
        return username;
    }

    public void writePW(String pw) {
        editor.putString("pw", pw);
        editor.apply();
    }

    public String readPW() {
        return settings.getString("pw", "");
    }

}
