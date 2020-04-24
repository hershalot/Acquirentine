package com.fenapnu.acquirentine_android;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {


    double money;
    Map<String, Long> cards;
    String userId;
    String startTile;

    //should be no more than 6 in length
    List<String> tiles = Arrays.asList("", "", "", "", "", "");

    int turnOrder;
    String name = "";
    boolean isGoing = false;




    public Player(Map<String, Object> data){



        if(data.get("money") != null){
            money = (double) data.get("money");
        }else{
            money = 0;
        }
        if(data.get("cards") != null){
            cards = (Map<String, Long>) data.get("cards");
        }else{
            cards = new HashMap<>();
        }
        if(data.get("userId") != null){
            userId = (String) data.get("userId");
        }else{
            userId = "";
        }
        if(data.get("tiles") != null){
            tiles = (List<String>) data.get("tiles");
        }else{
            tiles = Arrays.asList("", "", "", "", "", "");
        }
        if(data.get("name") != null){
            name = (String) data.get("name");
        }else{
            name = "";
        }
        if(data.get("turnOrder") != null){
            turnOrder = (int) data.get("turnOrder");
        }else{
            turnOrder = 0;
        }
        if(data.get("startTile") != null){
            startTile = (String) data.get("startTile");
        }else{
            startTile = "";
        }
        if(data.get("isGoing") != null) {
            isGoing = (boolean) data.get("isGoing");
        }else{
            isGoing = false;
        }
    }






    public Player (String userId, String startTile,String name) {

        this.tiles = Arrays.asList("", "", "", "", "" , "");
        this.turnOrder = 0;
        this.userId = userId;
        this.money = 0;
        this.cards = new HashMap<>();
        this.cards.put(Corporation.SPARK.label, (long) 0);
        this.cards.put(Corporation.NESTOR.label, (long) 0);
        this.cards.put(Corporation.ROVE.label, (long)0);
        this.cards.put(Corporation.FLEET.label, (long)0);
        this.cards.put(Corporation.ETCH.label, (long)0);
        this.cards.put(Corporation.BOLT.label, (long)0);
        this.cards.put(Corporation.ECHO.label, (long)0);
        this.startTile = startTile;
        this.name = name;
        this.isGoing = false;
    }





    public Player (){

        tiles = Arrays.asList("", "", "", "", "" , "");
        userId = "";
        money = 0;
        cards = new HashMap<>();
        cards.put(Corporation.SPARK.label, (long)0);
        cards.put(Corporation.NESTOR.label, (long)0);
        cards.put(Corporation.ROVE.label, (long)0);
        cards.put(Corporation.FLEET.label, (long)0);
        cards.put(Corporation.ETCH.label, (long)0);
        cards.put(Corporation.BOLT.label, (long)0);
        cards.put(Corporation.ECHO.label, (long)0);
        startTile = "";
        turnOrder = 0;

    }





    public String getUserId() {
        return userId;
    }

    public String getName() {

        return name;
    }

    public double getMoney() {
        return money;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    public Map<String, Long> getCards() {
        return cards;
    }

    public List<String> getTiles() {
        return tiles;
    }

    public String getStartTile() {
        return startTile;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCards(Map<String, Long> cards) {
        this.cards = cards;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setStartTile(String startTile) {
        this.startTile = startTile;
    }

    public void setTiles(List<String> tiles) {
        this.tiles = tiles;
    }

    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }


    //adds cards to hand
//    public void addCards(Map<String, Integer> newCards){
//
//        for element in newCards{
//
//            if(cards[element.key] != nil){
//
//                let toAdd = element.value
//                let available = cards[element.key]
//                cards.updateValue(available! + toAdd, forKey: element.key)
//
//            }else{
//                cards.updateValue(element.value, forKey: element.key)
//            }
//        }
//    }
//
//
//
//    //removes cards from hand
//    public void removeCards(Map<String, Integer> removeCards){
//
//        for element in removeCards{
//
//            if(cards[element.key] != nil){
//
//                let toRemove = element.value
//                let available = cards[element.key]
//
//                if(toRemove < available!){
//                    cards.updateValue(available! - toRemove, forKey: element.key)
//                }else if(toRemove == available){
//                    cards.removeValue(forKey: element.key)
//                }
//            }
//        }
//    }






    //adds money to your hand
    public void addMoney(Double amount){
        money = money + amount;
    }




    //removes money from your hand -> returns false if it's unaffordable
    public boolean removeMoney(Double amount){

        boolean canRemove = false;
        if(money - amount > 0){

            money =  money - amount;
            canRemove = true;

        }

        return canRemove;
    }









    public Map<String, Object> toDictionary(){

        Map<String, Object> data = new HashMap<>();
        data.put("money", money);
        data.put("cards",cards);
        data.put("userId",userId);
        data.put("name",name);
        data.put("tiles", tiles);
        data.put("startTile",startTile);


        return data;


    }
}
