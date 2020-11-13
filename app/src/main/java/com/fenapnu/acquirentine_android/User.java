package com.fenapnu.acquirentine_android;

import java.util.HashMap;
import java.util.Map;

public class User {


    String uid;
    boolean inGame;
    String gameId;
    String name;
    String email;
    String token;
    String deviceOS;


    public String getGameId() {
        return gameId;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public String getName() {
        return name;
    }
    public String getToken() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setName(String name) {
        this.name = name;
    }




    public Map<String, Object> toDictionary(){
        Map<String, Object> newDict = new HashMap<>();
        newDict.put("uid", this.uid);
        newDict.put("inGame", this.inGame);
        newDict.put("gameId", this.gameId);
        newDict.put("name", this.name);

        return newDict;

    }


}

