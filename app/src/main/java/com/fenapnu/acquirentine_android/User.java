package com.fenapnu.acquirentine_android;

import java.util.HashMap;
import java.util.Map;

public class User {


    String userId;
    boolean inGame;
    String gameId;
    String name;



    public String getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isInGame() {
        return inGame;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Map<String, Object> toDictionary(){
        Map<String, Object> newDict = new HashMap<>();
        newDict.put("userId", this.userId);
        newDict.put("inGame", this.inGame);
        newDict.put("gameId", this.gameId);
        newDict.put("name", this.name);

        return newDict;

    }


}

