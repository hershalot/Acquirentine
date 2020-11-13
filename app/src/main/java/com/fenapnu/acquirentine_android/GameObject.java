package com.fenapnu.acquirentine_android;



import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class GameObject {

    //Informational Data, this is for the Function to know to run the update
//    long updateType = 0;


    String  gameId = "";
    Map<String, Player> players;

    List<String> playerOrder;
    Map<String, Long> tiles;
    Map<String, Long> discarded;
    Map<String, Long> cards;
    Map<String, List<String>> liveCorporations = new HashMap<>();

    //this is held specifically and uniquely to calculate sell costs after a merger. Should only be manipulated by functions, unless writing by client on corporation start
//    Map<String, Long> corpSizeValues = new HashMap<>();

    OnTileActionListener mListener;
//    List<List<String>> adjacentStarters = new ArrayList<>();

    Map<String, Object> mergerData = new HashMap<>();

    boolean searchable = true;
    boolean gameStarted = false;
    boolean gameComplete = false;
    boolean finalPayoutsComplete = false;
    boolean mergeRound = false;

    String lastTilePlayed = "";
    String discardedTile = "";
    String moveDescription = "";

    String gameName = "";
    String creator = "";
    String turn = "";

    List<String> totalBoard = new ArrayList<>() ;
    List<String> corporationNames = new ArrayList<>();


    Map<String, Object> turnData = new HashMap<>();


    int maxRowSize = 11;
    int buysellCollumn = 3;

    int primaryCollumn = 4;
    int secondaryCollumn = 5;
    int tertiaryCollumn = 6;

    Long[][] moneyGrid = new Long[maxRowSize][7];

    final int TIER1_CORP = 0;
    final int TIER2_CORP = 1;
    final int TIER3_CORP = 2;

    final String TAG = "Game Object";



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
        moneyGrid[9] = new Long[] {(long)100, (long)100, (long)37, (long)1100,  (long)11000, (long)7700, (long)5500};
        moneyGrid[10] = new Long[] {(long)100, (long)100, (long)100, (long)1200, (long)12000, (long)8200, (long)6000};

        totalBoard.addAll(grid);
        //setting a 2D board to check against for corps, unplayable tiles etc

        tiles = new HashMap<>();
        discarded = new HashMap<>();
        cards = new HashMap<>();
        players = new HashMap<>();
        playerOrder = new ArrayList<>();
        turnData = initTurnData(false);

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




    //GETTERS AND SETTERS -------  START
    public void setMergerData(Map<String, Object> mergerData) {
        this.mergerData = mergerData;
    }

    public void setMergeRound(boolean mergeRound) {
        this.mergeRound = mergeRound;
    }

    public void setOnTileListner(OnTileActionListener listener){
        this.mListener = listener;
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

    public void setTurnData(Map<String, Object> turnData) {
        this.turnData = turnData;
    }

    public void setFinalPayoutsComplete(boolean finalPayoutsComplete) {
        this.finalPayoutsComplete = finalPayoutsComplete;
    }

//    public void setUpdateType(long updateType) {
//        this.updateType = updateType;
//    }

    public void setTurn(String turn) {
        this.turn = turn;
    }






//    public long getUpdateType() {
//        return updateType;
//    }

    public boolean isMergeRound() {
        return mergeRound;
    }

    public Map<String, Object> getTurnData() { return turnData; }

    public Map<String, Object> getMergerData() {
        return mergerData;
    }

    public String getTurn() {
        return turn;
    }


    public boolean isFinalPayoutsComplete() {
        return finalPayoutsComplete;
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

    //GETTERS AND SETTERS -------  END









    public Player addPlayer(String currentUid, String name ){


        String tile = drawStartTile();
        Player currentPlayer = new Player(currentUid, tile, name);

        currentPlayer.setStartTile(tile);

        players.put(currentUid, currentPlayer);

        String fieldString = "players." + currentUid;

        Map<String, Object> add = new HashMap<>();
        add.put(fieldString, currentPlayer.toDictionary());
        add.put("playerOrder", FieldValue.arrayUnion(currentUid));

        DataManager.activeGamesPath().document(gameId).update(add);

        return currentPlayer;
    }



    public void endGame(){

        Map<String, Object> endgame = new HashMap<>();
        endgame.put("gameComplete", true);
        endgame.put("searchable", false);

        DataManager.activeGamesPath().document(gameId).update(endgame);

    }





    public void removePlayer(String userId, String startTile){


        String fieldString = "players." + userId;
        String tileString = "tiles." + startTile;

        playerOrder.remove(userId);
        players.remove(userId);
        tiles.put(tileString, (long) 1);

        Map<String, Object> removePlayer = new HashMap<>();
        removePlayer.put(fieldString, FieldValue.delete());
        removePlayer.put("playerOrder", playerOrder);
        String s = "tiles." + startTile;
        removePlayer.put(s,  (long) 1);

        DataManager.activeGamesPath().document(gameId).update(removePlayer);
    }





    public void removeTileFromHand(String tile, Player player){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        lastTilePlayed = tile;

        Map<String, Object> tilePlayed = new HashMap<>();
        tilePlayed.put("lastTilePlayed", tile);
        tilePlayed.put(playerUpdateString, player.getTiles());

        DataManager.activeGamesPath().document(gameId).update(tilePlayed);
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

        String mainPileDelete = "tiles." + tile;
        tilePlayed.put(mainPileDelete,FieldValue.delete());
        tilePlayed.put("moveDescription", s);
//        tilePlayed.put("updateType", 0);

        tilePlayed.put("turnData", initTurnData(true));

        DataManager.activeGamesPath().document(gameId).update(tilePlayed);
    }


    public Map<String, Object> initTurnData(boolean played){

        Map<String, Object> td = new HashMap<>();
        td.put("tilePlayed", played);
        td.put("buys",0);
        td.put("buy1", "");
        td.put("buy2", "");
        td.put("buy3", "");

        return td;

    }






    public String drawStartTile(){

        if(getTiles() == null){
            return "";
        }

        String tile = drawRandomTile();
        removeTileFromMainPile(tile);
        return tile;

    }






    public void drawTile(Player player, int pos){

        if(getTiles() == null || getTiles().size() < 1){
            return;
        }

        String tile = drawRandomTile();

        addTileToHand(tile, player, pos);

        mListener.onTileDrawn(tile, pos);

    }



    public void removeTileFromMainPile(String tile){

        String tileUpdateString = "tiles." + tile;

        Map<String, Object> toRemove = new HashMap<>();
        toRemove.put(tileUpdateString, FieldValue.delete());
        toRemove.put("gameId", gameId);
//        toRemove.put("updateType",0);
        DataManager.activeGamesPath().document(gameId).update(toRemove);

    }




    //  using a transaction we must remove the tile from the players hand and add the tile to discarded array with a transaction.
    public void discardTile(final String tile, final Player player, final int pos){

        final DocumentReference ref = DataManager.activeGamesPath().document(getGameId());

        Toast.makeText(MyApplication.getContext(), "Discarding " + tile + "...", Toast.LENGTH_SHORT).show();

        Map<String, Object> toDiscard = new HashMap<>();

        player.tiles.set(pos, "");
        String playerUpdateString = "players." + player.getUserId() + ".tiles";
        toDiscard.put(playerUpdateString, player.getTiles());


        String discardPath = "discarded." + tile;
        toDiscard.put(discardPath, 1);
//        toDiscard.put("updateType", 0);

        ref.update(toDiscard);
        mListener.onTileDiscarded(tile, pos);

    }




    public void addTileToHand(String tile, Player player, int pos){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        Map<String, Object> add = new HashMap<>();
        String tileUpdateString = "tiles." + tile;

        add.put(tileUpdateString, FieldValue.delete());
        player.tiles.set(pos, tile);
        add.put(playerUpdateString, player.getTiles());
//        add.put("updateType", 0);
        DataManager.activeGamesPath().document(gameId).update(add);

    }




    //trade in if available
    public void tradeIn(long number, String underCorp, String remainingCorp, Player player){

        long halfNumber = number / 2;
        String sPlayerRemoving = "players." + player.getUserId() + ".cards." + underCorp;
        String sPlayerGaining = "players." + player.getUserId() + ".cards." + remainingCorp;

        String sBankGain = "cards." + underCorp;
        String sBankRemoving = "cards." + remainingCorp;

        long newCountPlayerRemove = player.getCards().get(underCorp) - number;
        long newCountPlayerGain = player.getCards().get(remainingCorp) + halfNumber;

        long newCountBankGain = this.getCards().get(underCorp) + number;
        long newCountBankRemove = this.getCards().get(remainingCorp) - halfNumber;


        Map<String, Object> bankUpdate = new HashMap<>();

        bankUpdate.put(sPlayerRemoving,newCountPlayerRemove);
        bankUpdate.put(sPlayerGaining,newCountPlayerGain);
        bankUpdate.put(sBankRemoving,newCountBankRemove);
        bankUpdate.put(sBankGain, newCountBankGain);

        String s = player.getName() + " traded " + number + " " + underCorp + " for " + halfNumber + " " + remainingCorp;
        bankUpdate.put("moveDescription", s);
//        bankUpdate.put("updateType", 1);
        DataManager.activeGamesPath().document(gameId).update(bankUpdate);
    }




    //sell cards after merger
    public void sell(long number, Player player, String corporation){

        long paid = determineCost(corporation, number);

        String moneyPath = "players." + player.getUserId() + ".money";
        String sPlayer = "players." + player.getUserId() + ".cards." + corporation;
        String sBank = "cards." + corporation;

        long newCountPlayer = player.getCards().get(corporation) - number;
        long newBankCount = this.getCards().get(corporation) + number;

        long newMoney = player.money + paid;

        Map<String, Object> bankUpdate = new HashMap<>();
        bankUpdate.put(moneyPath, newMoney);
        bankUpdate.put(sBank, newBankCount);
        bankUpdate.put(sPlayer,newCountPlayer);

        String s = player.getName() + " sold " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);
//        bankUpdate.put("updateType", 1);

        DataManager.activeGamesPath().document(gameId).update(bankUpdate);

    }



    //buy cards
    public void buy(long number,  Player player, String corporation){

        if(liveCorporations.get(corporation) == null){
            return;
        }

        if(this.turn.equals(player.getUserId()) && (long) this.turnData.get("buys") + number > 3){
            Toast.makeText(MyApplication.getContext(), "Slow down, chief", Toast.LENGTH_SHORT).show();
            return;
        }

        long payment = determineCost(corporation, number);

        String moneyPath = "players." + player.getUserId() + ".money";
        String sPlayer = "players." + player.userId + ".cards." + corporation;
        String sBank = "cards." + corporation;

        long newMoney = 0;
        if(player.money - payment >= 0){
            newMoney = player.money - payment;
        }else{
            Toast.makeText(MyApplication.getContext(), "This isn't a charity", Toast.LENGTH_SHORT).show();
            return;
        }


        long newCountPlayer = player.getCards().get(corporation) + number;
        long newCountBank = this.getCards().get(corporation) - number;

        Map<String, Object> bankUpdate = new HashMap<>();

        bankUpdate.put(sPlayer,newCountPlayer);
        bankUpdate.put(sBank, newCountBank);
        bankUpdate.put(moneyPath, newMoney);

        String s = player.getName() + " buys " + number + " " + corporation;
        bankUpdate.put("moveDescription", s);
//        bankUpdate.put("updateType", 1);



        checkAndSubmitBuyData(player, bankUpdate, number, corporation);

    }





    private void checkAndSubmitBuyData(Player player, Map<String, Object> updates, long number, String corp){

        long totalPurchases = 0;
        totalPurchases = (long) this.getTurnData().get("buys");


        if(this.getTurn().equals(player.getUserId()) && totalPurchases < 4){

            String turnBuysPath = "turnData.buys";

            switch ((int) totalPurchases) {
                case 0:

                    if(number > 3){return;}

                    String turnBuy1Path = "turnData.buy1";
                    updates.put(turnBuysPath, number);
                    String s = player.getName() + " buys " + number + " " + corp;
                    updates.put(turnBuy1Path, s);

                    break;
                case 1:

                    if(number > 2){ return; }

                    String turnBuy2Path = "turnData.buy2";
                    updates.put(turnBuysPath, number + 1);
                    String st = player.getName() + " buys " + number + " " + corp;
                    updates.put(turnBuy2Path, st);

                    break;

                case 2:
                    if(number > 1){ return; }

                    String turnBuy3Path = "turnData.buy3";
                    updates.put(turnBuysPath, number + 2);
                    String str = player.getName() + " buys " + number + " " + corp;
                    updates.put(turnBuy3Path, str);

                    break;
            }


            DataManager.activeGamesPath().document(gameId).update(updates);

        }else{

            Toast.makeText(MyApplication.get(), "Not enough buys Remaining", Toast.LENGTH_SHORT);
        }



    }








    //Starts the Game
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
//        start.put("updateType", 0);


        String firstPlayer = determineFirstPlayerAndOrder();
        start.put("turn",  firstPlayer);


        DataManager.activeGamesPath().document(gameId).update(start);

    }



    //Randomly chooses 6 tiles for all players, does not write changes to DB
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



    //Determines who goes first based on the start tile
    public String determineFirstPlayerAndOrder() {

        String firstUid = "";
        int first = 100;
        for (Player p : players.values()) {

            String startTile = p.startTile;
            int pos = totalBoard.indexOf(startTile);
            if (pos < first) {
                first = pos;
                firstUid = p.userId;
            }
        }

        return firstUid;
    }



    //randomly picks a tile and removes it from the current pile
    public String drawRandomTile(){

        Object[] o =  getTiles().keySet().toArray();

        String tile = (String) o[new Random().nextInt(o.length)];
        getTiles().remove(tile);

        return tile;
    }




    //get the cost per unit of an active corporation
    public long getCostPerUnit(String corp){

        long size = 0;
        if(liveCorporations.get(corp) == null){
            size = (long) getMergerData().get(corp);
        }else{
            size = liveCorporations.get(corp).size();
        }



        int tier = determinCorporationTier(corp);
        int row = determineRow(tier, size);

        return moneyGrid[row][buysellCollumn];
    }


    //get the cost per unit of an active corporation
    public long getCostPerUnitWith(String corp, long size){


        int tier = determinCorporationTier(corp);
        int row = determineRow(tier, size);

        return moneyGrid[row][buysellCollumn];
    }


    //return cost of corporation based on the number desired
    public long determineCost(String corp, long number){


        long unitCost = getCostPerUnit(corp);

        return unitCost * number;
    }



    //return cost of corporation based on the number desired
    public long determineCostPostMerger(String corp, long preMergerSize , long number){

        long unitCost = getCostPerUnitWith(corp, preMergerSize);

        return unitCost * number;
    }





    //Each Corporation has a static Tier for the money grid. Return that here
    public int determinCorporationTier (String corporation){

        int tier = TIER1_CORP;

        if(corporation.equals(Corporation.ROVE.label) || corporation.equals(Corporation.FLEET.label) || corporation.equals(Corporation.ETCH.label)){

            tier = TIER2_CORP;

        }else if(corporation.equals(Corporation.BOLT.label) || corporation.equals(Corporation.ECHO.label)){

            tier = TIER3_CORP;

        }
        return tier;
    }




    //Using Corporation Tier and Size, determine moneyGrid Row
    public int determineRow(int tier, long size){

        int row = 0;

        for(int i = 0; i<11; i++){
            if(size <= moneyGrid[i][tier]){
                row = i;
                break;
            }
        }
        return row;
    }







    //Here we need to check this tile against the main tile pile and existing corporations
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

        List<String> adjacentTiles = getAdjacentTileArray(tile);
        List<String> adjacentCorporations = new ArrayList<>();

        //first gather adjacent Corporations
        for (String corp : liveCorporations.keySet()) {

            List<String> corpTiles = liveCorporations.get(corp);

            for(String t : adjacentTiles){

                if(corpTiles.contains(t)){

                    if (!adjacentCorporations.contains(corp)) {
                        adjacentCorporations.add(corp);

                    }
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
            if(corp.size() >= 10){
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





    //Start corporation with tile
    public void startCorporation(String starting, List<String> tiles, Player player){

        String startingCorpPath = "liveCorporations." + starting;
        Map<String, Object> updates = new HashMap<>();
        long count = cards.get(starting);


        if(count > 0){

            //gift player a free card ONLY if any remain
            long usercards = player.getCards().get(starting) + 1;
            String userString = "players." + player.getUserId() + ".cards." + starting;
            updates.put(userString, usercards);

            String bankPath = "cards." + starting;
            long bankCount = this.getCards().get(starting) - 1;
            updates.put(bankPath,bankCount);
        }


        String s = player.getName() + " started " + starting;
        updates.put("moveDescription", s);

        this.getLiveCorporations().put(starting, tiles);

        updates.put("turnData", initTurnData(true));

        updates.put(startingCorpPath, tiles);
        DataManager.activeGamesPath().document(gameId).update(updates);

        evaluateExistingTilesOnStarter(starting);

    }


    //Function for only adding a tile to an existing corp. No mergers or starters
    public void addTilesToCorpAndPlay(String corp, List<String> tiles, Player player, String tile){

        String playerUpdateString = "players." + player.getUserId() + ".tiles";

        int pos = player.getTiles().indexOf(tile);
        player.getTiles().set(pos, "");

        lastTilePlayed = tile;

        Map<String, Object> tilePlayed = new HashMap<>();

        tilePlayed.put("lastTilePlayed", tile);
        tilePlayed.put(playerUpdateString, player.getTiles());
        tilePlayed.put("turnData", initTurnData(true));

        String s = player.getName() + " played tile " + tile;

        tilePlayed.put("moveDescription", s);

        String corpPath = "liveCorporations." + corp;
        this.getLiveCorporations().get(corp).addAll(tiles);

        tilePlayed.put(corpPath, this.getLiveCorporations().get(corp));

        DataManager.activeGamesPath().document(gameId).update(tilePlayed);

    }






    //Evaluate and merge corporations based on existing counts
    public void mergeCorporations(String winner, List<String> involved, List<String> newTiles, String tile, Player player){

        //all involved must be added to winner array as well as new Tiles
        List<String> winningTileArray = this.getLiveCorporations().get(winner);


        //determine payouts here
        Map<String, Long> payouts = new HashMap<>();
        Map<String, Object> mergeData = new HashMap<>();

        //for each corporation involved
        for(String s : involved){

            if(!s.equals(winner)){

                //we need to determine each payout for each corp. And add it to the payouts map. Adding to existing entries case
                Map<String, Long> po = determinePayouts(s);

                for(String str : po.keySet()){

                    long toAdd = po.get(str);
                    if(payouts.get(str) != null){
                        //add
                        long existing = payouts.get(str);
                        payouts.put(str, existing + toAdd );
                    }else{
                        payouts.put(str, toAdd);
                    }
                }

                List<String> underCorp = getLiveCorporations().get(s);

                mergeData.put(s, underCorp.size());
                winningTileArray.addAll(underCorp);
                this.getLiveCorporations().remove(s);
            }
        }
        winningTileArray.addAll(newTiles);

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
        tilePlayed.put("mergeRound", true);


        for(String usrid : payouts.keySet()){

            String path = "players." + usrid + ".money";

            long currentFunds = players.get(usrid).money;
            long newFunds = currentFunds + payouts.get(usrid);

            mergeData.put(usrid, payouts.get(usrid));
            tilePlayed.put(path, newFunds);
        }

        //add the remaining users who didn't get payouts
//        for(String uid : playerOrder){
//            if(tilePlayed.get(uid) == null){
//                mergeData.put(uid, 0);
//            }
//        }




        List<String> unders = new ArrayList<>(involved);
        unders.remove(winner);
        List<Integer> counts = new ArrayList<>();

        Map<String, Object> cards = new HashMap<>();

        for(String str : unders){
            counts.add((int) mergeData.get(str));

            Map<String, Object> corpCounts = new HashMap<>();

            for(String id : playerOrder){

                Player p = players.get(id);
                long num = 0;
                if(p.cards != null && p.cards.get(str) != null){
                    num = p.cards.get(str);
                }

                corpCounts.put(id, num);
            }

            cards.put(str, corpCounts);
        }

        unders.sort(Comparator.comparingInt(counts::indexOf));

        mergeData.put("corporations", unders);
        mergeData.put("turnsTaken", 0);
        mergeData.put("winner", winner);
        mergeData.put("cards", cards);

        tilePlayed.put("mergeData", mergeData);
        tilePlayed.put("turnData", initTurnData(true));

        DataManager.activeGamesPath().document(gameId).update(tilePlayed);
    }




    //Determin cost of selling all cards in corporation for each player
    public Map<String, Long> determineCashSellout(String corporation){

        Map<String, Long> sellout = new HashMap<>();
        long cost = getCostPerUnit(corporation);

        for(Player p : players.values()){

            if(p.getCards().get(corporation) != null){
                long payout = p.getCards().get(corporation) * cost;
                sellout.put(p.getUserId(), payout);
            }
        }
        return sellout;
    }





    //This needs to aggregate the list of payouts for the corporation passed as a parameter
    public Map<String, Long> determinePayouts(String corporation){

        //First need to start by determining who is first second and third. If there is a tie, etc
        int tier = determinCorporationTier(corporation);

        int size = 0;
        if(getLiveCorporations().get(corporation) != null){
            size = getLiveCorporations().get(corporation).size();
        }else {

            long l = (long) getMergerData().get(corporation);
            size = Math.toIntExact(l);
        }

        int row =  determineRow(tier, size);

        long primaryPayout = moneyGrid[row][primaryCollumn];
        long secondaryPayout = moneyGrid[row][secondaryCollumn];
        long tertiaryPayout = moneyGrid[row][tertiaryCollumn];

        Map<String, Long> payouts = new HashMap<>();

        List<Map<String, Long>> countsArray = new ArrayList<>();

        for(Player p : players.values()){
            Map<String, Long> entry = new HashMap<>();
            entry.put(p.getUserId(), p.getCards().get(corporation));
            countsArray.add(entry);
        }


        //Sort array from largest to smallest
        countsArray = sortPayoutArray(countsArray);


        long primarySize = 1;
        long secondarySize = 1;
        long tertiarySize = 1;

        Map<String, Long> primary = new HashMap<>();
        Map<String, Long> secondary = new HashMap<>();
        Map<String, Long> tertiary = new HashMap<>();


        //since counts array is sorted from largest to smallest we can iterate and easily get the payouts sorted
        for(int i = 0; i<countsArray.size(); i++){

            Map<String, Long> data =  countsArray.get(i);
            Set<String> tmp = data.keySet();
            List<String> keys = new ArrayList<>(tmp);

            String key = keys.get(0);
            long count = countsArray.get(i).get(key);

            if(count > primarySize){
                //only here for the first
                primarySize = count;
                primary.put(key, count);

            }else if(count == primarySize){
                primary.put(key, count);
            }else if (count > secondarySize){
                secondarySize = count;
                secondary.put(key, count);

            }else if (count == secondarySize){
                secondary.put(key, count);
            }else if (count > tertiarySize){
                tertiarySize = count;
                tertiary.put(key, count);
            }else if (count == tertiarySize){
                tertiary.put(key, count);
            }
        }




        //now we need to determine which payouts go where
        //if 1 in primary, that's the p
        if(primary.size() > 2){
            //split the tiers between these three
            long portion = roundUp((primaryPayout + secondaryPayout + tertiaryPayout)/ primary.size()) ;
            for(String s : primary.keySet()){
                payouts.put(s, portion);
            }

        }else if (primary.size() == 2){
            //split the first and second tier between these two
            long pPortion = roundUp((primaryPayout + secondaryPayout)/2);
            for(String s : primary.keySet()){
                payouts.put(s, pPortion);
            }

            if(secondary.size() > 1){
                long sPortion  = roundUp(tertiaryPayout/secondary.size());
                for(String s : secondary.keySet()){
                    payouts.put(s, sPortion);
                }
            }else if(secondary.size() == 1){
                for(String s : secondary.keySet()){
                    payouts.put(s, tertiaryPayout);
                }
            }

        }else{
            //one top payout to primary. THere will always be at lease on primary due to the free card given.
            for(String s : primary.keySet()){
                payouts.put(s, primaryPayout);
            }

            if(secondary.size() > 1){
                //secondary splits second + tertiary payouts.
                long sPortion = roundUp((secondaryPayout + tertiaryPayout) / secondary.size());
                for(String s : secondary.keySet()){
                    payouts.put(s, sPortion);
                }

            }else if(secondary.size() == 1){
                for(String s : secondary.keySet()){
                    payouts.put(s, secondaryPayout);
                }

                if(tertiary.size() > 1){
                    long tPortion = roundUp(( tertiaryPayout / tertiary.size()));

                    for(String s : tertiary.keySet()){
                        payouts.put(s, tPortion);
                    }

                }else if(tertiary.size() == 1){
                    for(String s : tertiary.keySet()){
                        payouts.put(s, tertiaryPayout);
                    }
                }
            }
        }



        for(Player p: players.values()){
            if(payouts.get(p.getUserId()) == null){
                payouts.put(p.getUserId(), (long)0);
            }
        }



        //map of user id's and what they get paid
        return payouts;
    }









    //This needs to aggregate the list of payouts for the corporation passed as a parameter
    public Map<String, Long> determinePayoutsPostMerge(String corporation){

        //First need to start by determining who is first second and third. If there is a tie, etc

        int tier = determinCorporationTier(corporation);

        long size = 0;
        if(getMergerData().get(corporation) != null){
            size = (long) getMergerData().get(corporation);
        }

        int row =  determineRow(tier, size);

        long primaryPayout = moneyGrid[row][primaryCollumn];
        long secondaryPayout = moneyGrid[row][secondaryCollumn];
        long tertiaryPayout = moneyGrid[row][tertiaryCollumn];

        Map<String, Long> payouts = new HashMap<>();

        List<Map<String, Long>> countsArray = new ArrayList<>();
        Map<String, Map<String, Long>> cards = (Map<String, Map<String, Long>>) mergerData.get("cards");

        Map<String, Long> counts = cards.get(corporation);

        for(String u : counts.keySet()){

            Map<String, Long> entry = new HashMap<>();

            long amount = counts.get(u);
            entry.put(u, amount);

            countsArray.add(entry);
        }


        //Sort array from largest to smallest
        countsArray = sortPayoutArray(countsArray);

        long primarySize = 1;
        long secondarySize = 1;
        long tertiarySize = 1;

        Map<String, Long> primary = new HashMap<>();
        Map<String, Long> secondary = new HashMap<>();
        Map<String, Long> tertiary = new HashMap<>();

        //since counts array is sorted from largest to smallest we can iterate and easily get the payouts sorted
        for(int i = 0; i<countsArray.size(); i++){

            Map<String, Long> data =  countsArray.get(i);
            Set<String> tmp = data.keySet();
            List<String> keys = new ArrayList<>(tmp);

            String key = keys.get(0);
            long count = countsArray.get(i).get(key);

            if(count > primarySize){
                //only here for the first
                primarySize = count;
                primary.put(key, count);

            }else if(count == primarySize){
                primary.put(key, count);
            }else if (count > secondarySize){
                secondarySize = count;
                secondary.put(key, count);

            }else if (count == secondarySize){
                secondary.put(key, count);
            }else if (count > tertiarySize){
                tertiarySize = count;
                tertiary.put(key, count);
            }else if (count == tertiarySize){
                tertiary.put(key, count);
            }
        }



        //now we need to determine which payouts go where
        //if 1 in primary, that's the p
        if(primary.size() > 2){
            //split the tiers between these three
            long portion = roundUp((primaryPayout + secondaryPayout + tertiaryPayout)/ primary.size()) ;
            for(String s : primary.keySet()){
                payouts.put(s, portion);
            }
        }else if (primary.size() == 2){
            //split the first and second tier between these two
            long pPortion = roundUp((primaryPayout + secondaryPayout)/2);
            for(String s : primary.keySet()){
                payouts.put(s, pPortion);
            }

            if(secondary.size() > 1){
                long sPortion  = roundUp(tertiaryPayout/secondary.size());
                for(String s : secondary.keySet()){
                    payouts.put(s, sPortion);
                }
            }else if(secondary.size() == 1){
                for(String s : secondary.keySet()){
                    payouts.put(s, tertiaryPayout);
                }
            }

        }else{
            //one top payout to primary. THere will always be at lease on primary due to the free card given.
            for(String s : primary.keySet()){
                payouts.put(s, primaryPayout);
            }

            if(secondary.size() > 1){
                //secondary splits second + tertiary payouts.
                long sPortion = roundUp((secondaryPayout + tertiaryPayout) / secondary.size());
                for(String s : secondary.keySet()){
                    payouts.put(s, sPortion);
                }

            }else if(secondary.size() == 1){
                for(String s : secondary.keySet()){
                    payouts.put(s, secondaryPayout);
                }

                if(tertiary.size() > 1){
                    long tPortion = roundUp(( tertiaryPayout / tertiary.size()));

                    for(String s : tertiary.keySet()){
                        payouts.put(s, tPortion);
                    }

                }else if(tertiary.size() == 1){
                    for(String s : tertiary.keySet()){
                        payouts.put(s, tertiaryPayout);
                    }
                }
            }
        }

        //now add the people who don't get paid and how many cards they have
        for(String p: players.keySet()){
            if(payouts.get(p) == null){
                payouts.put(p, (long)0);
            }
        }

        //map of user id's and what they get paid
        return payouts;
    }




    private long roundUp(long num){

        return ((num + 99) / 100) * 100 ;
    }




    private List<Map<String, Long>> sortPayoutArray(List<Map<String, Long>> players){

        Collections.sort(players, new Comparator<Map<String, Long>>()
        {
            @Override
            public int compare(Map<String, Long> obj1, Map<String, Long> obj2) {
                long corpCount1 = 0;
                long corpCount2 = 0;
                for(Long l : obj1.values()){
                    corpCount1 = l;
                }

                for(Long l : obj2.values()){
                    corpCount2 = l;
                }

                return Long.compare(corpCount2, corpCount1);
            }
        });

        return players;
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



    public void performFinalPayouts(){

        //we need to determine each payout for each corp. And add it to the payouts map. Adding to existing entries case
        Map<String, Long> payouts = new HashMap<>();

        for(String s : liveCorporations.keySet()){

            //get the corp payout list
            Map<String, Long> po = determinePayouts(s);

            for(String str : po.keySet()) {

                long toAdd = po.get(str);
                if (payouts.get(str) != null) {
                    //add
                    long existing = payouts.get(str);
                    payouts.put(str, existing + toAdd);
                } else {
                    payouts.put(str, toAdd);
                }
            }


            //get money from selling here
            Map<String, Long> sell = determineCashSellout(s);
            for(String st : sell.keySet()){

                long sellAdd = sell.get(st);
                if(payouts.get(st) != null){
                    //add
                    long existing = payouts.get(st);
                    payouts.put(st, existing + sellAdd );
                }else{
                    payouts.put(st, sellAdd);
                }
            }
        }



        Map<String, Object> finalPayoutsToWrite = new HashMap<>();
        finalPayoutsToWrite.put("finalPayoutsComplete", true);
        finalPayoutsToWrite.put("searchable", false);
        finalPayoutsToWrite.put("gameComplete", true);

        //we should now have a list of the amount of money each player receives from payouts. Write the new values to the DB
        for (String s : payouts.keySet()) {
            String moneyString = "players." + s + ".money";

            long existingCash = players.get(s).money;
            long newCash = existingCash + payouts.get(s);
            finalPayoutsToWrite.put(moneyString, newCash);

        }
        DataManager.activeGamesPath().document(gameId).update(finalPayoutsToWrite);
    }




    public Map<String, Long> getInitialCards(){

        Map<String, Long> cards = new HashMap<>();
        cards.put(Corporation.SPARK.label, (long) 0);
        cards.put(Corporation.NESTOR.label, (long) 0);
        cards.put(Corporation.ROVE.label, (long)0);
        cards.put(Corporation.FLEET.label, (long)0);
        cards.put(Corporation.ETCH.label, (long)0);
        cards.put(Corporation.BOLT.label, (long)0);
        cards.put(Corporation.ECHO.label, (long)0);

        return cards;
    }



    public boolean canEndGame(){

        boolean allOver11 = true;
        boolean over38 = false;

        if(liveCorporations.size() < 1){
            allOver11 = false;
        }

        for(String s : liveCorporations.keySet()){

            List<String> corpTiles = liveCorporations.get(s);

            if(corpTiles.size() >= 38){
                over38 = true;
            }else if(corpTiles.size() < 11){
                allOver11 = false;
            }
        }

        return allOver11 || over38;

    }



    public List<String> aggregrateCorpTiles(){

        List<String> allCorpTiles = new ArrayList<>();

        for(List<String> tiles : liveCorporations.values()){
            allCorpTiles.addAll(tiles);
        }

        return allCorpTiles;
    }







    public void endTurn(){

        String current = turn;
        int pos = playerOrder.indexOf(current);

        int newPos = (pos + 1)  % playerOrder.size();
        String newTurn = playerOrder.get(newPos);

        Map<String, Object> update = new HashMap<>();
        update.put("turn", newTurn);


        if(isMergeRound()){
            //if it's a merge round we need to add this player to the existing merger turn played.
            List<String> corps = (List<String>) getMergerData().get("corporations");

            long turnsTaken = (long) getMergerData().get("turnsTaken");

            String s = "mergeData.turnsTaken";

            //now check if this is the last merger turn. If the mergerData turnsTaken count is equal to the playerSize plus the number of corporations going under
            if(turnsTaken >= (playerOrder.size() * corps.size() - 1)){
                update.put("mergeRound", false);

            }else if (turnsTaken % playerOrder.size() == 0){

                int corppos = (int) turnsTaken / playerOrder.size();
                update.put("current", corps.get(corppos));
                turnsTaken = turnsTaken + 1;
                update.put(s, turnsTaken);

            }else {

                turnsTaken = turnsTaken + 1;
                update.put(s, turnsTaken);
            }

        }else {
            update.put("turnData.buys", 0);
            update.put("turnData.tilePlayed", false);
        }

        DataManager.activeGamesPath().document(gameId).update(update);

    }



    //This is a checker function to evaluate a corporation on start. This needs to check for tiles in the new  corporation for any other straggler tiles that are not already added to the
    //new tile map
    private void evaluateExistingTilesOnStarter(String corporation){

        List<String> live = liveCorporations.get(corporation);
        if(live == null){
            return;
        }

        Map<String, Long> stragglersToAdd = new HashMap<>();

        for(String tile : live){

            List<String> adjacentTiles = getAdjacentTileArray(tile);
            Map<String, Long> unplayedTiles = aggregateUnplayedTiles();

            for(String adj : adjacentTiles) {

                if (unplayedTiles.get(adj) == null && !live.contains(adj)) {
                    //it's been played
                    stragglersToAdd.put(adj, (long) 1);
                }
            }
        }



        if(stragglersToAdd.size() > 0){
            Map<String, Object> updates = new HashMap<>();

            String updatePath = "liveCorporations." + corporation;
            liveCorporations.get(corporation).addAll(stragglersToAdd.keySet());

            updates.put(updatePath, liveCorporations.get(corporation));

            DataManager.activeGamesPath().document(gameId).update(updates);
            evaluateExistingTilesOnStarter(corporation);
        }

    }







}
