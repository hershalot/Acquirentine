package com.fenapnu.acquirentine_android;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;




public class DataManager {



    /**
     Shared Preferences

     */

    static String getUserId(Context c) {


        SharedPreferences mPrefs = c.getSharedPreferences(String.valueOf(R.string.shared_preferences_key), MODE_PRIVATE);

        return mPrefs.getString(String.valueOf(R.string.shared_preferences_user_id), "");

    }



    static void setUserId(String userId, Context c) {

        SharedPreferences mPrefs = c.getSharedPreferences(String.valueOf(R.string.shared_preferences_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString(String.valueOf(R.string.shared_preferences_user_id), userId);
        prefsEditor.apply();
    }






    //data serialization for appointments
    static GameObject deserializeGameData(String json) {

        Gson gson = new Gson();
        return gson.fromJson(json, GameObject.class);
    }


    public static String serializeGameData(GameObject go) {

        Gson gson = new Gson();
        return gson.toJson(go);
    }
}
