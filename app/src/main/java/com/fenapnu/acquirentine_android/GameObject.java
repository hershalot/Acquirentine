package com.fenapnu.acquirentine_android;


import android.widget.ArrayAdapter;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
    Map<String, Long> discarded;
    Map<String, Long> cards;
    Map<String, List<String>> liveCorporations = new HashMap<>();

    boolean searchable = true;
    boolean gameStarted = false;
    boolean gameComplete = false;
    String lastTilePlayed = "";
    String discardedTile = "";
    String moveDescription = "";

    String gameName = "";
    String creator = "";

    List<String> totalBoard = new ArrayList<>() ;
    List<String> corporationNames = new ArrayList<>();

    int maxRowSize = 11;
    int buysellCollumn = 3;
    int primaryCollumn = 8;
    int secondaryCollumn = 9;
    int tertiaryCollumn = 10;
    Long[][] moneyGrid = new Long[maxRowSize][7];


    final int TIER1_CORP = 0;
    final int TIER2_CORP = 1;
    final int TIER3_CORP = 2;




    public GameObject(){

        List<String> grid =  Arrays.asList("1A","2A","3A","4A","5A","6A","7A","8A","9A","10A","1B","2B","3B","4B","5B","6B","7B","8B","9B","10B","1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","1E","2E","3E","4E","5E","6E","7E","8E","9E","10E","1F","2F","3F","4F","5F","6F","7F","8F","9F","10F","1G","2G","3G","4G","5G","6G","7G","8G","9G","10G","1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","1I","2I","3I","4I","5I","6I","7I","8I","9I","10I","1J","2J","3J","4J","5J","6J","7J","8J","9J","10J");


        moneyGrid[0] = new Long[] {(long)2, (long)-1, (long)-1, (long)200, (long)2000, (long)1500, (long)1000};
        moneyGrid[1] = new Long[] {(long)3, (long)2, (long)-1, (long)300, (long)3000, (long)2200, (long)1500};
        moneyGrid[2] = new Long[] {(long)4, (long)3, (long)2, (long)400, (long)4000, (long)3000, (long)2000};
        moneyGrid[3] = new Long[] {(long)5, (long)4, (long)3, (long)500, (long)5000,(long) 3700, (long)2500};
        moneyGrid[4] = new Long[] {(long)7, (long)5, (long)4, (long)600, (long)6000, (long)4200, (long)3000};
        moneyGrid[5] = new Long[] {(long)17, (long)7, (long)5, (long)700, (long)7000, (long)5000, (long)3500};
        moneyGrid[6] = new Long[] {(long)27, (long)17, (long)7, (long)800, (long)8000, (long)5700, (long)4000};
        moneyGrid[7] = new Long[] {(long)37, (long)27, (long)17, (long)900, (long)9000, (long)6200, (long)4500};
        moneyGrid[8] = new Long[] {(long)100, (long)37, (long)27, (long)1000, (long)10000, (long)7000, (long)5000};
        moneyGrid[9] = new Long[] {(long)100, (long)100, (long)3,(long)7, (long)1100, (long)11000, (long)7700, (long)5500};
        moneyGrid[10] = new Long[] {(long)100, (long)100, (long)100, (long)1200, (long)12000, (long)8200, (long)6000};

        totalBoard.addAll(grid);
        //setting a 2D board to check against for corps, unplayable tiles etc

        tiles = new HashMap<>();
        discarded = new HashMap<>();
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

        corporationNames = Arrays.asList("Spark", "Nestor", "Rove", "Fleet", "Etch", "Echo", "Bolt");

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

    public Map<String, List<String>> getLiveCorporations() {
        return liveCorporations;
    }


    public void setLiveCorporations(Map<String, List<String>> liveCorporations) {
        this.liveCorporations = liveCorporations;
    }

    public void setMoveDescription(String moveDescription) {
        this.moveDescription = moveDescription;
    }

    public void setDiscarded(Map<String, Long> discarded) {
        this.discarded = discarded;
    }

    public Map<String, Long> getDiscarded() {
        return discarded;
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

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        lastTilePlayed = tile;

        Map<String, Object> tilePlayed = new HashMap<>();
        tilePlayed.put("lastTilePlayed", tile);
        tilePlayed.put(playerUpdateString, player.getTiles());

        String s = player.getName() + " played tile " + tile;
        tilePlayed.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(tilePlayed);

    }





    public String drawStartTile(String userId){

        if(getTiles() == null){
            return "";
        }

        String tile = drawRandomTile();
        removeTileFromMainPile(tile);
        return tile;


    }






    public String drawTile(Player player){
        if(getTiles() == null || getTiles().size() < 1){
            return "";
        }

        String tile = drawRandomTile();

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


    public void discardTile(String tile, Player player){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        Map<String, Object> toRemove = new HashMap<>();
        toRemove.put(playerUpdateString, player.getTiles());

        this.discarded.put(tile, (long)1);
        toRemove.put("discarded", this.discarded);

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

//        String sBankCorpGain = "cards." + underCorp;
        String sBankCorpRemove = "cards." + remainingCorp;

        long halfNumber = number / 2;

//        long newCountBankGain = getCards().get(underCorp) + number;
//        long newCountBankRemove = getCards().get(remainingCorp) - halfNumber;

        String sPlayerRemoving = "players." + player.getUserId() + ".cards." + underCorp;
        String sPlayerGaining = "players." + player.getUserId() + ".cards." + remainingCorp;

        long newCountPlayerRemove = player.getCards().get(underCorp) - number;
        long newCountPlayerGain = player.getCards().get(remainingCorp) + halfNumber;


        Map<String, Object> bankUpdate = new HashMap<>();

//        bankUpdate.put(sBankCorpGain,newCountBankGain);
//        bankUpdate.put(sBankCorpRemove,newCountBankRemove);
        bankUpdate.put(sPlayerRemoving,newCountPlayerRemove);
        bankUpdate.put(sPlayerGaining,newCountPlayerGain);

        String s = player.getName() + " traded " + number + " " + underCorp + " for " + halfNumber + " " + remainingCorp;
        bankUpdate.put("moveDescription", s);


        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);


    }




    //sell cards after merger
    public void sell(long number, Player player, String corporation){

        long paid = determineCost(corporation, number);
        String money = "players." + player.getUserId() + ".money";
        String sPlayer = "players." + player.getUserId() + ".cards." + corporation;

        long newCountPlayer = player.getCards().get(corporation) - number;
        player.addMoney(paid);

        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(money, player.money);
        bankUpdate.put(sPlayer,newCountPlayer);

        String s = player.getName() + " sold " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);

    }



    //sell cards after merger
    public void buy(long number,  Player player, String corporation){


        long payment = determineCost(corporation, number);

        String money = "players." + player.getUserId() + ".money";
        String sPlayer = "players." + player.userId + ".cards." + corporation;

        if(!player.removeMoney(payment)){
           return;
        }

        long newCountPlayer = player.getCards().get(corporation) + number;


        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(sPlayer,newCountPlayer);
        bankUpdate.put(money, payment);

        String s = player.getName() + " buys " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(bankUpdate);
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
        start.put("liveCorporations", new HashMap<>());

        setInitialTiles();

        for(Player p : players.values()){
            String playerString = "players." + p.getUserId() + ".tiles";
            start.put(playerString, p.getTiles());

        }
        start.put("tiles", tiles);
        FirebaseFirestore.getInstance().collection("ActiveGames").document(getGameId()).update(start);

    }




    public void setInitialTiles(){

        int i = 0;
        while (i < 6){

            for (Player p : players.values()){

                String s = drawRandomTile();
                p.getTiles().set(i, s);
            }
            i++;
        }
    }


    public String drawRandomTile(){
        Object[] o =  getTiles().keySet().toArray();

        String tile = (String) o[new Random().nextInt(o.length)];
        getTiles().remove(tile);

        return tile;

    }



    public long determineCost(String corp, long number){

        int cost = 0;


        long size = liveCorporations.get(corp).size();
        int tier = determinCorporationTier(corp);

        long row = determineRow(tier, size);


        long unitCost = row * buysellCollumn;

        return unitCost * number;
    }


    public int determinCorporationTier (String corporation){

        int tier = TIER1_CORP;

        if(corporation.equals(Corporation.ROVE.label) || corporation.equals(Corporation.FLEET.label) || corporation.equals(Corporation.ETCH.label)){

            tier = TIER2_CORP;
        }else if(corporation.equals(Corporation.BOLT.label) || corporation.equals(Corporation.ECHO.label)){

            tier = TIER3_CORP;

        }

        return tier;

    }



    public long determineRow(int tier, long size){



        long row = 0;

        for(int i = 0;i<11; i++){

            if(size <= moneyGrid[i][tier]){
                row = i;
                break;
            }
        }

        return row;


    }











    //Here we need to check this tile agains the main tile pile and existing corporations
    //If adjacent to 2 or more "safe corporations (10 tiles or more) it's unplayable"

    //Not great time complexity in this function but the corporations map is a max 7 count while adjacent is max 4.  Each corporation string list is a max of 100 but
    // it rarely gets over 45 in my experience. game should be ending once one corporation reaches 38

    /*

    General Algorithm.

     Iterate current corporations, check against adjacent tiles within each corporation. If adjacent add to corp count and append to adjacentCorporations.
     */

    //Will return an array of adjacent corp names, if only entry is ["starter"] it's a starter piece. If returned ["uplayable"] tile is unplayable
    //if it's playable with no adjacent corporations, return ["playable"]


    public List<String> evaluateAdjacentTiles(String tile){

        boolean playable = true;
        List<String> adjacentTiles = getAdjacentTileArray(tile);


        List<String> adjacentCorporations = new ArrayList<>();

        //first gather adjacent Corporations
        for (String corp : liveCorporations.keySet()) {

            List<String> corpTiles = liveCorporations.get(corp);

            for(String t : adjacentTiles){

                if(corpTiles.contains(t)){

                    adjacentCorporations.add(corp);
                    break;
                }
            }
        }

        //if only adjacent to 1, return immediately because it must be playable
        if(adjacentCorporations.size() == 1){
            List<String> allData = new ArrayList<>();
            allData.add("appending");
            allData.add(adjacentCorporations.get(0));
            allData.addAll(adjacentTiles);


            return allData;
        }


        //then check if it's even playable
        int safeCorpCount = 0;
        for(String sc : adjacentCorporations){

            List<String> corp = liveCorporations.get(sc);
            if(sc.length() > 10){
                safeCorpCount++;
            }
        }



        //if not playable set the only entry to unplayable and return
        if(safeCorpCount > 1){
            adjacentCorporations.clear();
            adjacentCorporations.add("unplayable");
            return adjacentCorporations;

        }else if(adjacentCorporations.size() > 1){
            //a merger is happening. return adjacent corps
            return adjacentCorporations;
        }


        //check adjacent tiles and whether they've been played. If we are here, these tiles do not belong to a corp
        Map<String, Long> uplayedTiles = aggregateUnplayedTiles();
        List<String> toBeStarted  = new ArrayList<>();
        toBeStarted.add("starter");
        //here we need to check for it starting a new Corp

        //check adjacent tiles and whether they've been played. If we are here, these tiles do not belong to a corp
        for(String s : adjacentTiles){

            if(!uplayedTiles.containsKey(s)){
                //this is a starter piece because unplayed tiles doesn't contain an adjacent piece. Pass "starter and it's adjoining peices"
                toBeStarted.add(s);
            }
        }

        if(toBeStarted.size() > 1 && adjacentCorporations.size() == 0){

            adjacentCorporations.addAll(toBeStarted);
        }else{
            adjacentCorporations.add("playable");
        }

        return adjacentCorporations;
    }






    public void startCorporation(String starting, List<String> tiles, Player player){

        String startingCorpPath = "liveCorporations." + starting;
        Map<String, Object> updates = new HashMap<>();
        long count = cards.get(starting);


        if(count > 0){
            //gift player a free card
            long usercards = player.getCards().get(starting) + 1;
//            long bankcards = cards.get(starting) - 1;

            String userString = "players." + player.getUserId() + ".cards." + starting;
//            String bankString = "cards." + starting;

            updates.put(userString, usercards);
//            updates.put(bankString, bankcards);
        }

        String s = player.getName() + " started " + starting;
        updates.put("moveDescription", s);

        this.getLiveCorporations().put(starting, tiles);


        updates.put(startingCorpPath, tiles);

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(updates);

    }


    public void addTilesToCorpAndPlay(String corp, List<String> tiles, Player player, String tile){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        lastTilePlayed = tile;

        Map<String, Object> tilePlayed = new HashMap<>();

        tilePlayed.put("lastTilePlayed", tile);
        tilePlayed.put(playerUpdateString, player.getTiles());

        String s = player.getName() + " played tile " + tile;
        tilePlayed.put("moveDescription", s);


        String corpPath = "liveCorporations." + corp;
        this.getLiveCorporations().get(corp).addAll(tiles);

        tilePlayed.put(corpPath, this.getLiveCorporations().get(corp));

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(tilePlayed);

    }







    public void mergeCorporations(String winner, List<String> involved, List<String> newTiles, String tile ,Player player){

        List<String> winningTileArray = this.getLiveCorporations().get(winner);
        winningTileArray.addAll(newTiles);

        //for each corporation involved
        for(String s : involved){

            if(!s.equals(winner)){

                //determine payouts here

                List<String> underCorp = getLiveCorporations().get(s);
                winningTileArray.addAll(underCorp);
                this.getLiveCorporations().remove(s);

            }
        }


        Map<String, Object> tilePlayed = new HashMap<>();

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        lastTilePlayed = tile;

        tilePlayed.put("lastTilePlayed", tile);
        tilePlayed.put(playerUpdateString, player.getTiles());

        String s = player.getName() + " Merged into " + winner;
        tilePlayed.put("moveDescription", s);

        tilePlayed.put("liveCorporations", getLiveCorporations());

        FirebaseFirestore.getInstance().collection("ActiveGames").document(gameId).update(tilePlayed);
    }





    public Map<String, Long> determineMergerPayouts(String corporation){

        //First need to start by determining who is first second and third. If there is a tie, etc

        int tier = determinCorporationTier(corporation);
        int size = getLiveCorporations().get(corporation).size();

        int row = (int) determineRow(tier, size);

        long primaryPayout = moneyGrid[row][primaryCollumn];
        long secondaryPayout = moneyGrid[row][secondaryCollumn];
        long tertiaryPayout = moneyGrid[row][tertiaryCollumn];


        Map<String, Long> payouts = new HashMap<>();
        Map<String, Long> equal = new HashMap<>();


        List<Map<String, Long>> countsArray = Arrays.asList();

        //We need to organize the players sizes in an array and sort it

        for(String s : players.keySet()){
            int number = (int) players.get(s).getMoney();
            if(){

            }

        }






        //should return Map of userId's with a value of what they get paid
        return payouts;

    }















    //Here we determine which tiles have been played by using the unplayed tiles by adding the player and discarded tiles to the main drawable tiles pile.
    public Map<String, Long> aggregateUnplayedTiles(){

        Map<String, Long> tiles = new HashMap<>(getTiles());
        Map<String, Player> players = new HashMap<>(getPlayers());
        Map<String, Long> discarded = new HashMap<>(getDiscarded());

        for(Player p : players.values()){

            for(String s : p.getTiles()){
                tiles.put(s, (long) 1);
            }
        }


        for(String s : discarded.keySet()){

            tiles.put(s, (long)1);
        }

        return tiles;

    }








    //adjacent pieces are  top, botton: tilePosition + 10, tilePosition - 10 and left, right tilePosition - 1, tilePosition + 1

    public List<String> getAdjacentTileArray(String tile){

        List<String> adjacent = new ArrayList<>();

        int position = totalBoard.indexOf(tile);
        String left = "";
        String right = "";
        String top = "";
        String bottom = "";


        //handle left most collumn not having a left tile
        if(position % 10 != 0){
            left = totalBoard.get(position - 1);
            adjacent.add(left);
        }


        //handle right most collumn not having right adjacent
        if(position % 10 != 9){
            right = totalBoard.get(position + 1);
            adjacent.add(right);

        }

        //handle top row not having top adjacent
        if(position >= 10){
            top = totalBoard.get(position - 10);
            adjacent.add(top);
        }
        //handle bottom row not having bottom adjacent
        if(position < 90){
            bottom = totalBoard.get(position + 10);
            adjacent.add(bottom);
        }


        return adjacent;

    }





}
