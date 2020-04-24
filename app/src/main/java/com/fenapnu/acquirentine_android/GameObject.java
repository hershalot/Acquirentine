package com.fenapnu.acquirentine_android;


import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class GameObject {

    String  gameId = "";
    Map<String, Player> players;

    List<String> playerOrder;
    Map<String, Long> tiles;
    Map<String, Long> cards;


    boolean searchable = true;
    boolean gameStarted = false;
    boolean gameComplete = false;
    String lastTilePlayed = "";
    String discardedTile = "";
    String moveDescription = "";

    String gameName = "";
    String creator = "";




    public GameObject(){

        List<String> grid =  Arrays.asList("1A","2A","3A","4A","5A","6A","7A","8A","9A","10A","1B","2B","3B","4B","5B","6B","7B","8B","9B","10B","1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","1E","2E","3E","4E","5E","6E","7E","8E","9E","10E","1F","2F","3F","4F","5F","6F","7F","8F","9F","10F","1G","2G","3G","4G","5G","6G","7G","8G","9G","10G","1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","1I","2I","3I","4I","5I","6I","7I","8I","9I","10I","1J","2J","3J","4J","5J","6J","7J","8J","9J","10J");
        tiles = new HashMap<>();
        cards = new HashMap<>();
        players = new HashMap<>();
        playerOrder = new ArrayList<>();

        long DEFAULT_STOCK_COUNT = 24;

        cards.put(Corporation.SPARK.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.NESTOR.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.ROVE.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.FLEET.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.ETCH.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.BOLT.label, DEFAULT_STOCK_COUNT);
        cards.put(Corporation.ECHO.label, DEFAULT_STOCK_COUNT);

        for (String s: grid) {
            getTiles().put(s, (long) 1);
        }
    }


    public void setTiles(Map<String, Long> tiles) {
        this.tiles = tiles;
    }

    public void setCards(Map<String, Long> cards) {
        this.cards = cards;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setPlayerOrder(List<String> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public void setGameComplete(boolean gameComplete) {
        this.gameComplete = gameComplete;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setLastTilePlayed(String lastTilePlayed) {
        this.lastTilePlayed = lastTilePlayed;
    }

    public void setDiscardedTile(String discardedTile) {
        this.discardedTile = discardedTile;
    }

    public String getLastTilePlayed() {
        return lastTilePlayed;
    }

    public String getDiscardedTile() {
        return discardedTile;
    }

    public void setMoveDescription(String moveDescription) {
        this.moveDescription = moveDescription;
    }

    public String getMoveDescription() {
        return moveDescription;
    }

    public Map<String, Long> getTiles() {
        return tiles;
    }

    public Map<String, Long> getCards() {
        return cards;
    }

    public String getGameId() {
        return gameId;
    }

    public List<String> getPlayerOrder() {
        return playerOrder;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public boolean isGameComplete() {
        return gameComplete;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public String getCreator() {
        return creator;
    }



    public Player addPlayer(String currentUid, String name ){

        String tile = drawStartTile(currentUid);
        Player currentPlayer = new Player(currentUid, tile, name);

        currentPlayer.setStartTile(tile);

        players.put(currentUid, currentPlayer);
        playerOrder.add(currentUid);

        String fieldString = "players." + currentUid;

        Map<String, Object> add = new HashMap<>();
        add.put(fieldString, currentPlayer.toDictionary());
        add.put("playerOrder", playerOrder);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(add);

        return currentPlayer;


    }




    public void endGame(){

        Map<String, Object> endgame = new HashMap<>();
        endgame.put("gameComplete", true);
        endgame.put("searchable", false);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(endgame);
    }





    public void removePlayer(String userId, String startTile){

        String fieldString = "players." + userId;
        String tileString = "tiles." + startTile;

        playerOrder.remove(userId);
        players.remove(userId);
        tiles.put(tileString, (long) 1);



        Map<String, Object> removePlayer = new HashMap<>();
        removePlayer.put(fieldString, FieldValue.delete());
        removePlayer.put("playerOrder",playerOrder );
        removePlayer.put(tileString, 1);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(removePlayer);
    }






    //Tile Functions
    public void playTile(String tile, Player player){

        removeTileFromHand(tile, player);
        //remove tile from dictionary

        lastTilePlayed = tile;

        Map<String, Object> tilePlayed = new HashMap<>();
        tilePlayed.put("lastTilePlayed", tile);
        String s = player.getName() + " played tile " + tile;
        tilePlayed.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(tilePlayed);


    }





    public String drawStartTile(String userId){

        if(getTiles() == null){
            return "";
        }

        Object[] o = (Object[]) getTiles().keySet().toArray();

        String tile = (String) o[new Random().nextInt(o.length)];
        getTiles().remove(tile);

        removeTileFromMainPile(tile);

        return tile;


    }





    public String drawTile(Player player){
        if(getTiles() == null || getTiles().size() < 1){
            return "";
        }

        Object[] o = (Object[]) this.getTiles().keySet().toArray();

        String tile = (String) o[new Random().nextInt(o.length)];
        getTiles().remove(tile);

        addTileToHand(tile, player);

        removeTileFromMainPile(tile);


        return tile;
    }



    public void removeTileFromMainPile(String tile){

        String tileUpdateString = "tiles." + tile;

        Map<String, Object> toRemove = new HashMap<>();
        toRemove.put(tileUpdateString, FieldValue.delete());

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(toRemove);

    }


    public void removeTileFromHand(String tile, Player player){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        Map<String, Object> toRemove = new HashMap<>();
        toRemove.put(playerUpdateString, player.getTiles());

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(toRemove);
    }



    public void addTileToHand(String tile, Player player){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        Map<String, Object> add = new HashMap<>();

        List<String> allTiles = new ArrayList<>(player.getTiles());

        for(int i = 0; i < allTiles.size(); i++){

            String t = allTiles.get(i);
            if(t.equals("")){
                player.getTiles().set(i, tile);
                break;
            }
        }


        add.put(playerUpdateString, player.getTiles());

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(add);

    }



    //trade in if available
    public void tradeIn(long number, String underCorp, String remainingCorp, Player player){

        String sBankCorpGain = "cards." + underCorp;
        String sBankCorpRemove = "cards." + remainingCorp;

        long halfNumber = number / 2;

        long newCountBankGain = getCards().get(underCorp) + number;
        long newCountBankRemove = getCards().get(remainingCorp) - halfNumber;

        String sPlayerRemoving = "players." + player.getUserId() + ".cards." + underCorp;
        String sPlayerGaining = "players." + player.getUserId() + ".cards." + remainingCorp;

        long newCountPlayerRemove = player.getCards().get(underCorp) - number;
        long newCountPlayerGain = player.getCards().get(remainingCorp) + halfNumber;


        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(sBankCorpGain,newCountBankGain);
        bankUpdate.put(sBankCorpRemove,newCountBankRemove);

        Map<String, Object> playerUpdate = new HashMap<>();
        bankUpdate.put(sPlayerRemoving,newCountPlayerRemove);
        bankUpdate.put(sPlayerGaining,newCountPlayerGain);

        String s = player.getName() + " traded " + number + " " + underCorp + " for " + halfNumber + " " + remainingCorp;
        bankUpdate.put("moveDescription", s);



        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(playerUpdate);



    }




    //sell cards after merger
    public void sell(long number, Player player, String corporation){


        String sBank = "cards." + corporation;
        String sPlayer = "players." + player.getUserId() + ".cards." + corporation;
        long newCountBank = getCards().get(corporation) + number;
        long newCountPlayer = player.getCards().get(corporation) - number;


        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(sBank,newCountBank);

        Map<String, Object> playerUpdate = new HashMap<>();
        bankUpdate.put(sPlayer,newCountPlayer);
        String s = player.getName() + " sold " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(playerUpdate);

    }



    //sell cards after merger
    public void buy(long number,  Player player, String corporation){


        String sBank = "cards." + corporation;
        String sPlayer = "players." + player.userId + ".cards." + corporation;

        long newCountBank = getCards().get(corporation) - number;
        long newCountPlayer = player.getCards().get(corporation) + number;


        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(sBank,newCountBank);

        Map<String, Object> playerUpdate = new HashMap<>();
        bankUpdate.put(sPlayer,newCountPlayer);

        String s = player.getName() + " buys " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(playerUpdate);
    }



    public void startGame(){
        if(getGameId().equals("")){
            return;
        }

        //set game is started
        this.setGameStarted(true);
        Map<String, Object> start = new HashMap<>();
        start.put("gameStarted", true);
        start.put("moveDescription", "Game Started");
        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(start);


    }


}
