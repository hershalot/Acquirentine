package com.fenapnu.acquirentine_android;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class DataManager {


    /***
     Shared Preferences
     ***/

    static Long nowInSeconds(){

        long millis = System.currentTimeMillis();
        return millis / 1000;
    }


    static CollectionReference getUsersPath(){

        return FirebaseFirestore.getInstance().collection("Users");
    }

    static CollectionReference getTurnDataPath(String id){

        return activeGamesPath().document(id).collection("TurnData");
    }

    static CollectionReference activeGamesPath() {

        return FirebaseFirestore.getInstance().collection("ActiveGames");
    }

    public static User getLocalUserObject() {

        Context context = MyApplication.getContext();
        SharedPreferences mPrefs = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_key), MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(String.valueOf(R.string.shared_preferences_user), "");

        return gson.fromJson(json, User.class);
    }


    public static void setLocalUserObject(User user) {

        Context context = MyApplication.getContext();
        SharedPreferences mPrefs = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString(String.valueOf(R.string.shared_preferences_user), json);
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
