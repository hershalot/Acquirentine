package com.fenapnu.acquirentine_android;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;


interface OnTileActionListener{

    void onTileDrawn(String tile, int pos);
    void onTileDiscarded(String tile, int pos);


}



public class BasicGameActivity extends AppCompatActivity implements OnPlayerClickListener, OnTileActionListener{


    TextView a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10,
            d1, d2,d3, d4, d5, d6, d7, d8, d9, d10, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10,
            g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10,
            j1, j2, j3, j4, j5, j6, j7, j8, j9, j10;


    List<TextView> boardPlaceList = new ArrayList<>();



    List<Boolean> flippedTiles = new ArrayList<>();

    //CARD OPTIONs
    Button buyButton;
    Button sellButton;
    Button tradeButton;
    Button closePopup;

    Button increaseSellBtn;
    Button decreaseSellBtn;
    Button increaseBuyBtn;
    Button decreaseBuyBtn;
    Button increaseTradeBtn;
    Button decreaseTradeBtn;
    Button endTurnButton;

    Button endGame;
    ImageButton toggleTileGrid;
    GridLayout tileLayout;

    ImageButton togglePlayersBtn;
    TextView playerMoneyTV;

    TextView buyLbl;
    TextView sellLbl;
    TextView tradeLbl;
    TextView tradeForLbl;
    TextView lastPlayedTileLbl;

    EditText buyEt;
    EditText sellEt;
    EditText tradeEt;

    Spinner tradeSpinner;
    RelativeLayout buyselltradeView;
    RelativeLayout alphaView;

    //MAIN GAME
    Button sparkButton;
    TextView sparkRemaining;
    TextView sparkCount;

    Button nestorButton;
    TextView nestorRemaining;
    TextView nestorCount;

    Button roveButton;
    TextView roveRemaining;
    TextView roveCount;

    Button fleetButton;
    TextView fleetRemaining;
    TextView fleetCount;

    Button etchButton;
    TextView etchRemaining;
    TextView etchCount;

    Button echoButton;
    TextView echoRemaining;
    TextView echoCount;

    Button boltButton;
    TextView boltRemaining;
    TextView boltCount;

    Button tileBtn1;
    Button tileBtn2;
    Button tileBtn3;
    Button tileBtn4;
    Button tileBtn5;
    Button tileBtn6;

    Button playTile;
    Button discardTile;
    Button closeTiles;
    Button startGameBtn;
    ImageButton toggleCardsButton;

    LinearLayout tileButtonsLayout;
    LinearLayout cardsLayout;

    TextView gameNameTV;
    RecyclerView recyclerView;
    PlayersItemAdapter adapter;
    Button endGameWithPayouts;

    boolean drawerIsOpen = true;
    boolean gridIsOpen = true;
    boolean playerListIsOpen = true;
    boolean isCreator = false;


    String gameName = "";
    String userName = "";

    Player currentPlayer = new Player();
    ListenerRegistration gameListener;

    //THIS value must be passed from calling VC
    String currentUid = "";

    //only pass this if joining an existing game
    GameObject gameDataController = new GameObject();
    boolean gameInSession = false;
    List<Button> tileBtnArray = new ArrayList<>();
    List<Player> playerList = new ArrayList<>();

    String selectedTile = "";
    int selectedButtonIndex = -1;
    String selectedCorporation = "";

    TextView costPerEt;


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        gameDataController.mListener = null;
        String json = DataManager.serializeGameData(gameDataController);

        state.putSerializable("gameData", json);
        state.putSerializable("userId", currentUid);
        state.putSerializable("userName", userName);
        state.putSerializable("gameName", gameName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            setContentView(R.layout.basic_game_layout_landscape);
        }
        else {
            // Portrait
            setContentView(R.layout.basic_game_layout);
        }


        endTurnButton = findViewById(R.id.end_turn_button);
        recyclerView = findViewById(R.id.players_rv);
        cardsLayout = findViewById(R.id.cards_layout);
        endGameWithPayouts = findViewById(R.id.end_game_payout);

        tileBtn1 = findViewById(R.id.tile1_btn);
        tileBtn2 = findViewById(R.id.tile2_btn);
        tileBtn3 = findViewById(R.id.tile3_btn);
        tileBtn4 = findViewById(R.id.tile4_btn);
        tileBtn5 = findViewById(R.id.tile5_btn);
        tileBtn6 = findViewById(R.id.tile6_btn);

        buyEt = findViewById(R.id.buy_et);
        sellEt = findViewById(R.id.sell_et);
        tradeForLbl = findViewById(R.id.trade_for_tv);
        tradeEt = findViewById(R.id.trade_et);

        buyEt.setEnabled(false);
        sellEt.setEnabled(false);
        tradeEt.setEnabled(false);

        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDataController.endTurn();
            }
        });

        lastPlayedTileLbl = findViewById(R.id.last_tile_played);
        closeTiles = findViewById(R.id.close_tiles_btn);
        closeTiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonColors();
                selectedTile = "";
                tileButtonsLayout.setVisibility(View.INVISIBLE);
            }
        });

        sparkButton = findViewById(R.id.spark_button);
        sparkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Spark");
            }
        });
        nestorButton = findViewById(R.id.nestor_button);
        nestorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Nestor");

            }
        });
        roveButton = findViewById(R.id.rove_button);
        roveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Rove");

            }
        });
        fleetButton = findViewById(R.id.fleet_button);
        fleetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Fleet");

            }
        });
        etchButton = findViewById(R.id.etch_button);
        etchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Etch");

            }
        });
        echoButton = findViewById(R.id.echo_button);
        echoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Echo");
            }
        });
        boltButton = findViewById(R.id.bolt_button);
        boltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameDataController.isGameStarted()){
                    return;
                }
                setAndShowPopupData("Bolt");
            }
        });

        alphaView = findViewById(R.id.alpha_view);
        alphaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing, we don't want users touching things under things
            }
        });

        endGame = findViewById(R.id.end_game_button);
        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        BasicGameActivity.this);
                // set title
                alertDialogBuilder.setTitle("End game?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("This can't be undone")
                        .setCancelable(true)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                gameDataController.endGame();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing

                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });


        tileLayout = findViewById(R.id.grid_layout);
        toggleTileGrid = findViewById(R.id.toggle_tiles_btn);
        toggleTileGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTilesView();
            }
        });

        toggleCardsButton = findViewById(R.id.toggle_cards_btn);
        toggleCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCardsView();
            }
        });

        sparkCount = findViewById(R.id.spark_count);
        nestorCount = findViewById(R.id.nestor_count);
        roveCount = findViewById(R.id.rove_count);
        fleetCount = findViewById(R.id.fleet_count);
        etchCount = findViewById(R.id.etch_count);
        echoCount = findViewById(R.id.echo_count);
        boltCount = findViewById(R.id.bolt_count);

        sparkRemaining = findViewById(R.id.spark_remaining);
        nestorRemaining = findViewById(R.id.nestor_remaining);
        etchRemaining = findViewById(R.id.etch_remaining);
        fleetRemaining = findViewById(R.id.fleet_remaining);
        roveRemaining = findViewById(R.id.rove_remaining);
        boltRemaining = findViewById(R.id.bolt_remaining);
        echoRemaining = findViewById(R.id.echo_remaining);


        playerMoneyTV = findViewById(R.id.player_money_tv);
        playTile = findViewById(R.id.play_tile_btn);
        playTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determinePlayTileAction();
                resetButtonColors();
                selectedTile = "";
                hideTileActionsView();
            }
        });

        discardTile = findViewById(R.id.discard_tile_btn);
        discardTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unplayableTileAction();
                resetButtonColors();
                selectedTile = "";
                hideTileActionsView();
            }
        });
        tileButtonsLayout = findViewById(R.id.tile_button_layout);



        tileBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(0)){
                    return;
                }

                selectedButtonIndex = 0;

                if(tileBtnArray.get(0).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(0).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(0));
                    drawTileAction(0);
                    return;
                }else if(tileBtnArray.get(0).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(0).setVisibility(View.INVISIBLE);
                    return;
                }

                if(tileBtnArray.get(0).getText().equals(selectedTile) && !selectedTile.equals("")){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(0).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();
            }
        });


        tileBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectedButtonIndex = 1;
                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(1)){
                    return;
                }

                if(tileBtnArray.get(1).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(1).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(1));
                    drawTileAction(1);
                    return;
                }else if(tileBtnArray.get(1).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(1).setVisibility(View.INVISIBLE);
                    return;
                }


                if(tileBtnArray.get(1).getText().equals(selectedTile)){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(1).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();
            }
        });



        tileBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(2)){
                    return;
                }

                selectedButtonIndex = 2;
                if(tileBtnArray.get(2).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(2).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(2));
                    drawTileAction(2);
                    return;
                }else if(tileBtnArray.get(2).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(2).setVisibility(View.INVISIBLE);
                    return;
                }
                if(tileBtnArray.get(2).getText().equals(selectedTile)){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(2).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();
            }
        });

        tileBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(3)){
                    return;
                }
                selectedButtonIndex = 3;

                if(tileBtnArray.get(3).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(3).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(3));
                    drawTileAction(3);
                    return;
                }else if(tileBtnArray.get(3).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(3).setVisibility(View.INVISIBLE);
                    return;
                }
                if(tileBtnArray.get(3).getText().equals(selectedTile)){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(3).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();

            }
        });

        tileBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(4)){
                    return;
                }
                selectedButtonIndex = 4;
                if(tileBtnArray.get(4).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(4).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(4));
                    drawTileAction(4);
                    return;
                }else if(tileBtnArray.get(4).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(4).setVisibility(View.INVISIBLE);
                    return;
                }
                if(tileBtnArray.get(4).getText().equals(selectedTile)){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(4).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();

            }
        });


        tileBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((gameDataController != null && !gameDataController.isGameStarted()) || flippedTiles.get(5)){
                    return;
                }
                selectedButtonIndex = 5;

                if(tileBtnArray.get(5).getText().equals("DRAW") && gameDataController.getTiles().size() > 0){
                    tileBtnArray.get(5).setEnabled(false);
                    shrinkTileAnimation(tileBtnArray.get(5));
                    drawTileAction(5);
                    return;
                }else if(tileBtnArray.get(5).getText().equals("DRAW") && gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_SHORT).show();
                    tileBtnArray.get(5).setVisibility(View.INVISIBLE);
                    return;
                }
                if(tileBtnArray.get(5).getText().equals(selectedTile)){
                    selectedTile = "";
                    hideTileActionsView();
                }else{
                    selectedTile = tileBtnArray.get(5).getText().toString();
                    showTileActionsView();
                }
                resetButtonColors();


            }
        });

        endGameWithPayouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDataController.performFinalPayouts();
            }
        });


        tileBtn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(0).getText().equals("") || tileBtnArray.get(0).getText().equals("DRAW")){
                    return false;
                }

                if(flippedTiles.get(0)){
                    //is's flipped, unflip it
                    flippedTiles.set(0, false);
                    tileBtnArray.get(0).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(0).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(0).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(0, true);
                    tileBtnArray.get(0).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(0).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });
        tileBtn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(1).getText().equals("") || tileBtnArray.get(1).getText().equals("DRAW")){
                    return false;
                }
                if(flippedTiles.get(1)){
                    //is's flipped, unflip it
                    flippedTiles.set(1, false);
                    tileBtnArray.get(1).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(1).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(1).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(1, true);
                    tileBtnArray.get(1).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(1).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });
        tileBtn3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(2).getText().equals("") || tileBtnArray.get(2).getText().equals("Draw")){
                    return false;
                }
                if(flippedTiles.get(2)){
                    //is's flipped, unflip it
                    flippedTiles.set(2, false);
                    tileBtnArray.get(2).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(2).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(2).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(2, true);
                    tileBtnArray.get(2).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(2).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });
        tileBtn4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(3).getText().equals("") || tileBtnArray.get(3).getText().equals("Draw")){
                    return false;
                }
                if(flippedTiles.get(3)){
                    //is's flipped, unflip it
                    flippedTiles.set(3, false);
                    tileBtnArray.get(3).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(3).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(3).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(3, true);
                    tileBtnArray.get(3).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(3).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });
        tileBtn5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(4).getText().equals("") || tileBtnArray.get(4).getText().equals("Draw")){
                    return false;
                }
                if(flippedTiles.get(4)){


                    //is's flipped, unflip it
                    flippedTiles.set(4, false);
                    tileBtnArray.get(4).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(4).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(4).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(4, true);
                    tileBtnArray.get(4).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(4).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });
        tileBtn6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tileBtnArray.get(5).getText().equals("") || tileBtnArray.get(5).getText().equals("Draw")){
                    return false;
                }

                if(flippedTiles.get(5)){
                    //is's flipped, unflip it
                    flippedTiles.set(5, false);
                    tileBtnArray.get(5).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                    tileBtnArray.get(5).setTextColor(getColor(R.color.textColor));

                }else{
                    //is's unflipped, flip it
                    if(tileBtnArray.get(5).getText().equals(selectedTile)){
                        selectedTile = "";
                        hideTileActionsView();
                    }
                    flippedTiles.set(5, true);
                    tileBtnArray.get(5).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                    tileBtnArray.get(5).setTextColor(getColor(R.color.darkGray));
                }

                return true;
            }
        });


        tileBtnArray.add(tileBtn1);
        tileBtnArray.add(tileBtn2);
        tileBtnArray.add(tileBtn3);
        tileBtnArray.add(tileBtn4);
        tileBtnArray.add(tileBtn5);
        tileBtnArray.add(tileBtn6);

        flippedTiles.add(false);
        flippedTiles.add(false);
        flippedTiles.add(false);
        flippedTiles.add(false);
        flippedTiles.add(false);
        flippedTiles.add(false);

        startGameBtn = findViewById(R.id.start_game_btn);
        gameNameTV = findViewById(R.id.game_name);

        costPerEt = findViewById(R.id.buysell_cost);
        buyButton = findViewById(R.id.buy_btn);

        increaseBuyBtn = findViewById(R.id.buy_inc_btn);
        increaseSellBtn = findViewById(R.id.sell_inc_btn);

        decreaseSellBtn = findViewById(R.id.sell_dec_btn);
        decreaseBuyBtn = findViewById(R.id.buy_dec_btn);

        decreaseTradeBtn = findViewById(R.id.trade_dec_btn);
        increaseTradeBtn = findViewById(R.id.trade_inc_btn);


        decreaseTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = tradeEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount > 1){
                    amount = amount - 2 ;
                    String s = "" + amount;
                    tradeEt.setText(s);
                }
            }
        });
        increaseTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = tradeEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount < 23){
                    amount = amount + 2;
                    String s = "" + amount;
                    tradeEt.setText(s);
                }else {
                    Toast.makeText(BasicGameActivity.this, "Listen here, buddy", Toast.LENGTH_SHORT).show();
                }
            }
        });

        increaseSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = sellEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount <= 24){
                    amount++;
                    String s = "" + amount;
                    sellEt.setText(s);
                }
            }
        });

        increaseBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = buyEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount < 3){
                    amount++;
                    String s = "" + amount;
                    buyEt.setText(s);
                }
            }
        });


        decreaseBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = buyEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount > 0){
                    amount--;
                    String s = "" + amount;
                    buyEt.setText(s);
                }
            }
        });


        decreaseSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = sellEt.getText().toString();
                buyAmount = buyAmount.trim();
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount > 0){
                    amount--;
                    String s = "" + amount;
                    sellEt.setText(s);
                }
            }
        });


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = buyEt.getText().toString();
                buyAmount = buyAmount.trim();
                if(buyAmount.equals("0")){
                    Toast.makeText(BasicGameActivity.this, "Enter a valid purchase amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount > 3){
                    Toast.makeText(BasicGameActivity.this, "What are you, a cheater?", Toast.LENGTH_SHORT).show();
                    return;
                }

                Long cardsLeft = gameDataController.getCards().get(selectedCorporation);

                if(amount <= cardsLeft){

                    gameDataController.buy(amount, currentPlayer, selectedCorporation);

                    if(selectedCorporation.equals("Etch") && amount == 3){
                        Toast.makeText(BasicGameActivity.this, "Hersh Mgersh's Ertches n Skertches", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    String s = "Not enough" + selectedCorporation + "left in the bank, honey";
                    Toast.makeText(BasicGameActivity.this, s, Toast.LENGTH_SHORT).show();
                }

                closeTransactionPopup();
            }
        });

        sellButton = findViewById(R.id.sell_btn);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sellAmount = sellEt.getText().toString();
                sellAmount = sellAmount.trim();
                if(sellAmount.equals("0")){
                    Toast.makeText(BasicGameActivity.this, "Enter a valid sell amount", Toast.LENGTH_SHORT).show();
                    return;
                }


                Long a = Long.valueOf(sellAmount);
                int amount = a.intValue();
                if(amount <= currentPlayer.getCards().get(selectedCorporation)) {
                    gameDataController.sell(amount, currentPlayer, selectedCorporation);
                }else{
                    String s = "You don't have enough " + selectedCorporation + " to sell, my poor chum";
                    Toast.makeText(BasicGameActivity.this, s, Toast.LENGTH_SHORT).show();
                    return;
                }

                closeTransactionPopup();
            }
        });

        tradeButton = findViewById(R.id.trade_btn);
        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selected = tradeSpinner.getSelectedItem().toString();

                if(selected.equals("Select") || selected.equals(selectedCorporation)){
                    Toast.makeText(BasicGameActivity.this, "Select a proper corp to trade", Toast.LENGTH_SHORT).show();
                    return;
                }

                String tradeIntAmount = tradeEt.getText().toString();
                tradeIntAmount = tradeIntAmount.trim();
                tradeIntAmount.replace(".", "");
                tradeIntAmount.replace("/", "");
                if(tradeIntAmount.equals("")){
                    Toast.makeText(BasicGameActivity.this, "Enter a valid trade amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                Long a = Long.valueOf(tradeIntAmount);
                int amount = a.intValue();
                if(amount % 2 != 0){
                    Toast.makeText(BasicGameActivity.this, "Enter an even number", Toast.LENGTH_SHORT).show();
                    return;
                }else if (amount > currentPlayer.getCards().get(selectedCorporation)){
                    String s = "You don't have enough " + selectedCorporation + " remaining, my poor chum";
                    Toast.makeText(BasicGameActivity.this, s, Toast.LENGTH_SHORT).show();
                    return;

                }else if(amount/2 > gameDataController.getCards().get(selected)){
                    String s = "Not enough " + selected + " remaining to trade in.";
                    Toast.makeText(BasicGameActivity.this, s, Toast.LENGTH_SHORT).show();
                    return;
                }

                gameDataController.tradeIn(amount, selectedCorporation, selected, currentPlayer);


                closeTransactionPopup();
            }
        });


        closePopup = findViewById(R.id.close_popup);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeTransactionPopup();
            }
        });



        tradeEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    return;
                }

                Long a = Long.valueOf(s.toString());
                int amount = a.intValue();
                if(amount % 2 != 0){
                    return;
                }

                int half = amount/2;

                String str = " For " + half;
                tradeForLbl.setText(str);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        buyLbl = findViewById(R.id.buy_tv);
        sellLbl = findViewById(R.id.sell_tv);
        tradeLbl = findViewById(R.id.trade_tv);

        buyselltradeView = findViewById(R.id.buysell_view);



        //if there is a saved instance state set data using it, otherwise use intent data
        if ((savedInstanceState != null) && (savedInstanceState.getSerializable("gameData") != null)) {

            currentUid = (String) savedInstanceState.getSerializable("userId");
            gameDataController = DataManager.deserializeGameData((String) savedInstanceState.getSerializable("gameData"));
            currentPlayer = gameDataController.players.get(currentUid);
            userName = (String) savedInstanceState.getSerializable("userName");
            gameName = (String) savedInstanceState.getSerializable("gameName");


            if (gameDataController.getCreator().equals(currentUid)) {

                String title = gameName + "'s Game";
                gameNameTV.setText(title);
                endGame.setVisibility(View.VISIBLE);

                if(!gameDataController.isGameStarted()){
                    endGame.setVisibility(View.VISIBLE);
                    startGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startGameCheck();
                        }
                    });
                }else{
                    startGameBtn.setVisibility(GONE);
                    if (currentUid.equals(gameDataController.getCreator())) {
                        endGame.setVisibility(View.VISIBLE);
                    }
                }


            }else{

                String title = gameName + "'s Game";
                gameNameTV.setText(title);

                if (gameDataController.isGameStarted()) {
                    startGameBtn.setVisibility(GONE);
                    if (currentUid.equals(gameDataController.getCreator())) {
                        endGame.setVisibility(View.VISIBLE);
                    }
                } else {
                    String s = "Leave Game";
                    startGameBtn.setText(s);
                    startGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            gameDataController.removePlayer(currentUid, currentPlayer.startTile);
                            onBackPressed();
                        }
                    });
                }
            }

            setGameListener();

        }else {

            Intent i = getIntent();
            boolean sCreator = i.getBooleanExtra("isCreator", false);

            if (sCreator) {

                gameDataController = new GameObject();
                currentUid = i.getStringExtra("userId");
                userName = i.getStringExtra("userName");
                gameName = i.getStringExtra("gameName");
                String title = gameName + "'s Game";
                gameNameTV.setText(title);
                endGame.setVisibility(View.VISIBLE);
                setNewGameData();

            } else {

                String sGame = i.getStringExtra("gameObject");
                gameDataController = DataManager.deserializeGameData(sGame);
                currentUid = i.getStringExtra("userId");
                userName = i.getStringExtra("userName");
                gameName = gameDataController.gameName;

                gameInSession = gameDataController.isGameStarted();
                String title = gameName + "'s Game";
                gameNameTV.setText(title);

                if (gameDataController.isGameStarted()) {
                    startGameBtn.setVisibility(GONE);
                    if (currentUid.equals(gameDataController.getCreator())) {
                        endGame.setVisibility(View.VISIBLE);
                    }
                } else if (!currentUid.equals(gameDataController.getCreator())) {
                    String s = "Leave Game";
                    startGameBtn.setText(s);
                    startGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            gameDataController.removePlayer(currentUid, currentPlayer.startTile);
                            onBackPressed();
                        }
                    });

                } else {
                    endGame.setVisibility(View.VISIBLE);
                    startGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startGameCheck();
                        }
                    });
                }

                if (gameDataController.players.get(currentUid) != null) {
                    setGameListener();
                } else {
                    addPlayer();
                }
            }
        }

        

        int VERTICAL_ITEM_SPACE = 5;
        recyclerView.addItemDecoration(new SpacingItemDecoration(VERTICAL_ITEM_SPACE, VERTICAL_ITEM_SPACE));

        adapter = new PlayersItemAdapter(this, playerList, gameDataController,this);
        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);


        tradeSpinner = findViewById(R.id.trade_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.corporations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradeSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        togglePlayersBtn = findViewById(R.id.toggle_players_button);
        togglePlayersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayersView();
            }
        });

        setupBoardTVs();
    }





    private void toggleTilesView(){

        if(gridIsOpen){

            gridIsOpen = false;

            ObjectAnimator animation = ObjectAnimator.ofFloat(tileLayout, "translationX", -tileLayout.getWidth());
            animation.setDuration(300);
            animation.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(toggleTileGrid, "translationX", -tileLayout.getWidth());
            animationB.setDuration(300);
            animationB.start();

            resetButtonColors();
            selectedTile = "";
            tileButtonsLayout.setVisibility(View.INVISIBLE);
            toggleTileGrid.setImageDrawable(getDrawable(R.drawable.right_arrow));

        }else {

            gridIsOpen = true;
            ObjectAnimator animationV = ObjectAnimator.ofFloat(tileLayout, "translationX", 0f);
            animationV.setDuration(300);
            animationV.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(toggleTileGrid, "translationX", 0f);
            animationB.setDuration(300);
            animationB.start();

            toggleTileGrid.setImageDrawable(getDrawable(R.drawable.left_arrow));
        }
    }





    private void shrinkTileAnimation(Button b){
        b.animate().scaleX(0.5f).scaleY(0.5f).setDuration(300);
    }




    private void toggleCardsView(){

        if(drawerIsOpen){

            drawerIsOpen = false;

            ObjectAnimator animation = ObjectAnimator.ofFloat(cardsLayout, "translationX", cardsLayout.getWidth());
            animation.setDuration(300);
            animation.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(toggleCardsButton, "translationX", cardsLayout.getWidth());
            animationB.setDuration(300);
            animationB.start();

            toggleCardsButton.setImageDrawable(getDrawable(R.drawable.left_arrow));

        }else {
            drawerIsOpen = true;
            ObjectAnimator animationV = ObjectAnimator.ofFloat(cardsLayout, "translationX", 0f);
            animationV.setDuration(300);
            animationV.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(toggleCardsButton, "translationX", 0f);
            animationB.setDuration(300);
            animationB.start();

            toggleCardsButton.setImageDrawable(getDrawable(R.drawable.right_arrow));
        }
    }






    private void togglePlayersView (){

        if(playerListIsOpen){

            playerListIsOpen = false;

            ObjectAnimator animation = ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth());
            animation.setDuration(300);
            animation.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(togglePlayersBtn, "translationX", -recyclerView.getWidth());
            animationB.setDuration(300);
            animationB.start();

            togglePlayersBtn.setImageDrawable(getDrawable(R.drawable.right_arrow));

        }else {
            playerListIsOpen = true;
            ObjectAnimator animationV = ObjectAnimator.ofFloat(recyclerView, "translationX", 0f);
            animationV.setDuration(300);
            animationV.start();

            ObjectAnimator animationB = ObjectAnimator.ofFloat(togglePlayersBtn, "translationX", 0f);
            animationB.setDuration(300);
            animationB.start();

            togglePlayersBtn.setImageDrawable(getDrawable(R.drawable.left_arrow));
        }
    }

    private void startGameCheck(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                BasicGameActivity.this);
        // set title
        alertDialogBuilder.setTitle("Start game?");

        // set dialog message
        alertDialogBuilder
                .setMessage("players can't be added after this is done.")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        gameDataController.startGame();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }





    private void closeTransactionPopup(){

        buyselltradeView.setVisibility(View.INVISIBLE);

        tradeSpinner.setSelection(0);
        buyEt.setText("0");
        sellEt.setText("0");
        tradeEt.setText("0");
        costPerEt.setText("");
        String s = " For ";
        tradeForLbl.setText(s);



        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(tradeForLbl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tradeEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(sellEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(buyEt.getWindowToken(), 0);



    }

    private void setAndShowPopupData(String corporation){

        String costPer = "Buy/Sell  $" + gameDataController.getCostPerUnit(corporation);
        costPerEt.setText(costPer);


        switch (corporation){

            case "Spark":

                buyLbl.setTextColor(getColor(R.color.sparkColor));
                sellLbl.setTextColor(getColor(R.color.sparkColor));
                tradeLbl.setTextColor(getColor(R.color.sparkColor));
                break;

            case "Nestor":

                buyLbl.setTextColor(getColor(R.color.nestorColor));
                sellLbl.setTextColor(getColor(R.color.nestorColor));
                tradeLbl.setTextColor(getColor(R.color.nestorColor));
                break;

            case "Rove":

                buyLbl.setTextColor(getColor(R.color.roveColor));
                sellLbl.setTextColor(getColor(R.color.roveColor));
                tradeLbl.setTextColor(getColor(R.color.roveColor));
                break;

            case "Fleet":

                buyLbl.setTextColor(getColor(R.color.fleetColor));
                sellLbl.setTextColor(getColor(R.color.fleetColor));
                tradeLbl.setTextColor(getColor(R.color.fleetColor));
                break;
            case "Etch":

                buyLbl.setTextColor(getColor(R.color.etchColor));
                sellLbl.setTextColor(getColor(R.color.etchColor));
                tradeLbl.setTextColor(getColor(R.color.etchColor));
                break;

            case "Bolt":

                buyLbl.setTextColor(getColor(R.color.boltColor));
                sellLbl.setTextColor(getColor(R.color.boltColor));
                tradeLbl.setTextColor(getColor(R.color.boltColor));
                break;

            case "Echo":

                buyLbl.setTextColor(getColor(R.color.echoColor));
                sellLbl.setTextColor(getColor(R.color.echoColor));
                tradeLbl.setTextColor(getColor(R.color.echoColor));
                break;

        }

        selectedCorporation = corporation;

        String str = corporation + " (" + currentPlayer.cards.get(corporation) + ")";
        buyLbl.setText(str);
        sellLbl.setText(str);
        tradeLbl.setText(str);
        String s = " For 0 ";
        tradeForLbl.setText(s);

        buyselltradeView.setVisibility(View.VISIBLE);



    }



    private void showTileActionsView(){

        tileButtonsLayout.setVisibility(View.VISIBLE);
    }
    private void hideTileActionsView(){

        tileButtonsLayout.setVisibility(View.INVISIBLE);
    }




    private void initializePlayerUI(){

        String open = "Draw";
        tileBtnArray.get(0).setText(open);
        tileBtnArray.get(0).setTextColor(getColor(R.color.inGameColor));
        tileBtnArray.get(1).setText(open);
        tileBtnArray.get(1).setTextColor(getColor(R.color.inGameColor));
        tileBtnArray.get(2).setText(open);
        tileBtnArray.get(2).setTextColor(getColor(R.color.inGameColor));
        tileBtnArray.get(3).setText(open);
        tileBtnArray.get(3).setTextColor(getColor(R.color.inGameColor));
        tileBtnArray.get(4).setText(open);
        tileBtnArray.get(4).setTextColor(getColor(R.color.inGameColor));
        tileBtnArray.get(5).setText(open);
        tileBtnArray.get(5).setTextColor(getColor(R.color.inGameColor));
    }





    private void setNewGameData(){

        final Map<String, Object> newGameData = new HashMap<>();

        DocumentReference ref = DataManager.activeGamesPath().document();
        final String key = ref.getId();
        gameDataController.gameId = key;



        Map<String, Player> players = new HashMap<>();
        newGameData.put("tiles", gameDataController.tiles);
        newGameData.put("players", players);
        newGameData.put("cards", gameDataController.cards);
        newGameData.put("creator", currentUid);
        newGameData.put("gameName",gameName);
        newGameData.put("searchable", true);
        newGameData.put("gameComplete", false);
        newGameData.put("gameStarted", false);
        newGameData.put("gameId",key);
        newGameData.put("lastTilePlayed", "");
        newGameData.put("turn", "");
        newGameData.put("finalPayoutsComplete", false);
        newGameData.put("corpSizeValues", gameDataController.getCorpSizeValues());
        newGameData.put("moveDescription", "Welcome");
        newGameData.put("mergeRound",false);
        newGameData.put("mergeData",new HashMap<>());
        initializePlayerUI();

        //first set the ActiveGames, then Game updates
        ref.set(newGameData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    currentPlayer = gameDataController.addPlayer(currentUid, userName);

                    Intent i = getIntent();
                    i.putExtra("isCreator", false);
                    i.putExtra("gameObject", DataManager.serializeGameData(gameDataController));


                    setGameListener();

                    startGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startGameCheck();
                        }
                    });


                }
            }
        });
    }



    private void addPlayer(){

        //here create player object, add to player order object
        //Write to DB
        gameDataController.addPlayer(currentUid, userName);
        initializePlayerUI();
        setGameListener();

    }



    private boolean canDraw(){
        boolean canDraw = false;
        for (String s : currentPlayer.tiles){

            if(s.equals("")){
                canDraw = true;
                break;
            }
        }

        if(gameDataController.getTiles().size() < 1){
            canDraw = false;
        }

        return canDraw;


    }








    private void drawTileAction(int pos) {


        if(!canDraw()){
            return;
        }
        selectedTile = "";
        resetButtonColors();

        gameDataController.drawTile(currentPlayer,  pos);

    }



    private void playTileAction(String tile) {

        if(tile.equals("DRAW") || tile.equals("")){
            return;
        }

        shrinkTileAnimation(tileBtnArray.get(selectedButtonIndex));
        tileButtonsLayout.setVisibility(View.INVISIBLE);
        lastPlayedTileLbl.setText(tile);
        gameDataController.playTile(tile, currentPlayer);
    }




    private void unplayableTileAction() {

        if(selectedTile.equals("Draw")){
            return;
        }

        int pos = -1;
        for(Button button : tileBtnArray){
            if(button.getText() == selectedTile){
                pos = tileBtnArray.indexOf(button);
                break;
            }
        }

        shrinkTileAnimation(tileBtnArray.get(selectedButtonIndex));
        tileButtonsLayout.setVisibility(View.INVISIBLE);

        gameDataController.discardTile(selectedTile, currentPlayer, pos);
    }



    private void setGameListener(){

        if(gameListener != null){
            gameListener.remove();
            gameListener = null;
        }

        gameListener = DataManager.activeGamesPath().document(gameDataController.gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(e != null || !documentSnapshot.exists()) {
//                    Toast.makeText(BasicGameActivity.this, "Failed to update game data", Toast.LENGTH_SHORT).show();
                    return;
                }

                GameObject game = new GameObject();

                game.setPlayerOrder((List<String>) documentSnapshot.get("playerOrder"));
                game.setCards((Map<String, Long>) documentSnapshot.get("cards"));
                game.setGameName((String) documentSnapshot.get("gameName"));

                Map<String, List<String>> liveCorps = (Map<String, List<String>>) documentSnapshot.get("liveCorporations");

                if(liveCorps == null){
                    game.setLiveCorporations(new HashMap<String, List<String>>());
                }else{
                    game.setLiveCorporations(liveCorps);
                }


                Map<String, Long>  newTileBag = (Map<String, Long>) documentSnapshot.get("tiles");
                if(newTileBag == null){
                    game.setTiles(new HashMap<String, Long>());
                }else{
                    game.setTiles(newTileBag);
                }

                Map<String, Long> discard = (Map<String, Long>) documentSnapshot.get("discarded");
                if(discard != null){
                    game.setDiscarded(discard);

                }else{
                    game.setDiscarded(new HashMap<String, Long>());
                }

                game.setGameId((String) documentSnapshot.get("gameId"));
                game.setSearchable((boolean) documentSnapshot.get("searchable"));
                game.setGameStarted((boolean) documentSnapshot.get("gameStarted"));
                game.setGameComplete((boolean) documentSnapshot.get("gameComplete"));

                game.setMergeRound((boolean) documentSnapshot.get("mergeRound"));
                game.setMergerData((Map<String, Object>) documentSnapshot.get("mergerData"));

                game.setCreator((String) documentSnapshot.get("creator"));
                game.setLastTilePlayed((String) documentSnapshot.get("lastTilePlayed"));
                game.setMoveDescription((String) documentSnapshot.get("moveDescription"));
                game.setTurn((String) documentSnapshot.get("turn"));


                if(documentSnapshot.get("finalPayoutsComplete") != null){
                    game.setFinalPayoutsComplete((boolean) documentSnapshot.get("finalPayoutsComplete"));
                }

                Map<String, Long> sizes = (Map<String, Long>) documentSnapshot.get("corpSizeValues");
                if(sizes != null){
                    game.setCorpSizeValues(sizes);

                }else{
                    game.setCorpSizeValues(new HashMap<String, Long>());
                }

                game.setCorpSizeValues((Map<String, Long>) documentSnapshot.get("corpSizeValues"));

                Map<String, Map<String, Object>> rawPlayers = (Map<String, Map<String, Object>>) documentSnapshot.get("players");
                Map<String, Player> ps = new HashMap<>();

                long oldMoney = 0;
                if(currentPlayer != null && gameDataController.getPlayers().get(currentUid) != null){
                    oldMoney = gameDataController.getPlayers().get(currentUid).money;
                }
                String oldTurn = "";
                if(currentPlayer != null){
                    oldTurn = gameDataController.getTurn();
                }

                for(Map<String, Object> p : rawPlayers.values()){

                    String key =  (String) p.get("userId");
                    ps.put(key, new Player(p));

                    if(key.equals(currentUid)){
                        currentPlayer = new Player(p);
                    }
                }


                game.players = ps;

                if(!gameInSession){

                    if(gameDataController.gameStarted){

                        //If we made it here the game just started
                        gameInSession = true;

                        startGameBtn.setVisibility(View.GONE);
                        endGame.setVisibility(View.GONE);
                        endGameWithPayouts.setVisibility(View.GONE);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("inGame", true);
                        updates.put("gameId", gameDataController.gameId);
                        FirebaseFirestore.getInstance().collection("Users").document(currentUid).update(updates);
                    }
                }

                String oldMessage = gameDataController.getMoveDescription();
                String oldTile = gameDataController.getLastTilePlayed();

                gameDataController = game;
                adapter.setGameObject(game);
                if(gameDataController.mListener == null){
                    gameDataController.setOnTileListner(BasicGameActivity.this);
                }

                refreshUI(oldMessage, oldTile, oldMoney, oldTurn);

            }
        });

    }




    private void refreshUI(String oldMessage, String oldTile, long oldMoney, String oldTurn){


        if(gameDataController.isFinalPayoutsComplete()){
            disableActionsInput();
            //SHOW FINAL PAYOUT BREAKDOWN
        }

        if(gameDataController.getTurn().equals(currentPlayer.userId)){
            if(!oldTurn.equals(currentUid)){
                showTurnDialog();
            }
            enableAllActions();
            endTurnButton.setVisibility(View.VISIBLE);

        }else{
            disableActionsInput();
            endTurnButton.setVisibility(View.GONE);
        }

        if(gameDataController.finalPayoutsComplete){
            if(gameListener != null){
                gameListener.remove();
                gameListener = null;
            }
            Toast.makeText(this, "Game Ended", Toast.LENGTH_SHORT).show();
            showPaymentDialog(currentPlayer.money, "Game Over, your final score is", true);
        }else {
            endGameWithPayouts.setVisibility(GONE);
        }

        if(gameDataController.isGameComplete() && !gameDataController.isSearchable()){
            if(gameListener != null){
                gameListener.remove();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            });
            return;
        }



        if(gameDataController.canEndGame() && !gameDataController.finalPayoutsComplete){
            endGameWithPayouts.setVisibility(View.VISIBLE);
        }else{

            endGameWithPayouts.setVisibility(View.GONE);
        }

        Map<String, Long> cardsRemaining = gameDataController.cards;
        Map<String, Player> players = gameDataController.players;

        currentPlayer = players.get(currentUid);
        playerList.clear();

        for (Player player : players.values()) {
            playerList.add(player);
        }

        adapter.notifyDataSetChanged();

        String rSpark = "bank: " + cardsRemaining.get(Corporation.SPARK.label) + " ";
        String rNestor = "bank: " + cardsRemaining.get(Corporation.NESTOR.label) + " ";
        String rFleet= "bank: " + cardsRemaining.get(Corporation.FLEET.label) + " ";
        String rRove = "bank: " + cardsRemaining.get(Corporation.ROVE.label) + " ";
        String rEtch = "bank: " + cardsRemaining.get(Corporation.ETCH.label) + " ";
        String rBolt = "bank: " + cardsRemaining.get(Corporation.BOLT.label) + " ";
        String rEcho = "bank: " + cardsRemaining.get(Corporation.ECHO.label) + " ";

        sparkRemaining.setText(rSpark);
        nestorRemaining.setText(rNestor);
        roveRemaining.setText(rRove);
        fleetRemaining.setText(rFleet);
        etchRemaining.setText(rEtch);
        boltRemaining.setText(rBolt);
        echoRemaining.setText(rEcho);

        List<String> sparkArray = gameDataController.getLiveCorporations().get(Corporation.SPARK.label);

        String sizeSpark = "size: ";
        if(sparkArray != null){
            sizeSpark = "size: " + sparkArray.size();
        }else{
            sizeSpark = "--";
        }
        List<String> nestorArray = gameDataController.getLiveCorporations().get(Corporation.NESTOR.label);
        String sizeNestor = "size: ";
        if(nestorArray != null){
            sizeNestor = "size: " + nestorArray.size();
        }else{
            sizeNestor = "--";
        }
        List<String> fleetArray = gameDataController.getLiveCorporations().get(Corporation.FLEET.label);
        String sizeFleet = "";
        if(fleetArray != null){
            sizeFleet = "size: " + fleetArray.size();
        }else{
            sizeFleet = "--";
        }

        List<String> roveArray = gameDataController.getLiveCorporations().get(Corporation.ROVE.label);
        String sizeRove = "size: ";
        if(roveArray != null){
            sizeRove = "size: " + roveArray.size();
        }else{
            sizeRove = "--";
        }

        List<String> etchArray = gameDataController.getLiveCorporations().get(Corporation.ETCH.label);
        String sizeEtch = "";
        if(etchArray != null){
            sizeEtch = "size: " + etchArray.size();
        }else{
            sizeEtch = "--";
        }

        List<String> boltArray = gameDataController.getLiveCorporations().get(Corporation.BOLT.label);
        String sizeBolt = "";
        if(boltArray != null){
            sizeBolt = "size: " + boltArray.size();
        }else{
            sizeBolt = "--";
        }
        List<String> echoArray = gameDataController.getLiveCorporations().get(Corporation.ECHO.label);
        String sizeEcho = "";
        if(echoArray != null){
            sizeEcho = "size: " + echoArray.size();
        }else{
            sizeEcho = "--";
        }

        sparkCount.setText(sizeSpark);
        nestorCount.setText(sizeNestor);
        roveCount.setText(sizeRove);
        fleetCount.setText(sizeFleet);
        etchCount.setText(sizeEtch);
        boltCount.setText(sizeBolt);
        echoCount.setText(sizeEcho);




        if(currentPlayer != null){


            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            String money = myFormat.format(currentPlayer.getMoney());
            playerMoneyTV.setText(money);



            String cSpark = "" + currentPlayer.cards.get(Corporation.SPARK.label);
            String cNestor = "" + currentPlayer.cards.get(Corporation.NESTOR.label);
            String cFleet= "" + currentPlayer.cards.get(Corporation.FLEET.label);
            String cRove = "" + currentPlayer.cards.get(Corporation.ROVE.label);
            String cEtch = "" + currentPlayer.cards.get(Corporation.ETCH.label);
            String cBolt = "" + currentPlayer.cards.get(Corporation.BOLT.label);
            String cEcho = "" + currentPlayer.cards.get(Corporation.ECHO.label);

            sparkButton.setText(cSpark);
            nestorButton.setText(cNestor);
            roveButton.setText(cRove);
            fleetButton.setText(cFleet);
            etchButton.setText(cEtch);
            echoButton.setText(cEcho);
            boltButton.setText(cBolt);
        }


        if(gameDataController.isGameStarted()){


            endGame.setVisibility(View.GONE);
            startGameBtn.setVisibility(View.INVISIBLE);

            if(!gameDataController.getMoveDescription().equals(oldMessage)){
                Toast.makeText(this, gameDataController.moveDescription, Toast.LENGTH_SHORT).show();
            }
            lastPlayedTileLbl.setText(gameDataController.getLastTilePlayed());

            if(oldMoney != 0 && gameDataController.isGameStarted()){
                if(currentPlayer.money > oldMoney){
                    onPaid(currentPlayer.money - oldMoney);
                }else if(currentPlayer.money < oldMoney){
                    onPayment(oldMoney - currentPlayer.money);
                }
            }
        }

        if(currentPlayer != null){
            setTileButtons(currentPlayer.tiles);
            setBoardTiles();
        }
    }





    private void setTileButtons(List<String> tiles){

        for (int i = 0; i < tiles.size(); i++){

            if(tiles.get(i).equals("")){
                String draw = "DRAW";
                tileBtnArray.get(i).setText(draw);
            }else{
                tileBtnArray.get(i).setText(tiles.get(i));
            }

        }

        resetButtonColors();


    }


    private void resetButtonColors(){


        for(int i = 0;i<tileBtnArray.size();i++){

            if(flippedTiles.get(i)){
                tileBtnArray.get(i).setBackground(getDrawable(R.drawable.rounded_grey_rectangle_bordered));
                tileBtnArray.get(i).setTextColor(getColor(R.color.darkGray));

            }else if(tileBtnArray.get(i).getText().equals("DRAW")){

                tileBtnArray.get(i).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                tileBtnArray.get(i).setTextColor(getColor(R.color.inGameColor));

            }else if (tileBtnArray.get(i).getText().equals(selectedTile)) {

                tileBtnArray.get(i).setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                tileBtnArray.get(i).setTextColor(getColor(R.color.textColor));
            }else{
                tileBtnArray.get(i).setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
                tileBtnArray.get(i).setTextColor(getColor(R.color.textColor));
            }
        }
    }




    @Override
    public void onPlayerClicked(Player player) {


        //do nothing for now
    }



    public void setupBoardTVs(){


        a1 = findViewById(R.id.a1); a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3); a4  = findViewById(R.id.a4);
        a5  = findViewById(R.id.a5); a6  = findViewById(R.id.a6);
        a7  = findViewById(R.id.a7); a8  = findViewById(R.id.a8);
        a9 = findViewById(R.id.a9); a10 = findViewById(R.id.a10);

        b1 = findViewById(R.id.b1); b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3); b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5); b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7); b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9); b10 = findViewById(R.id.b10);

        c1 = findViewById(R.id.c1); c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3); c4 = findViewById(R.id.c4);
        c5 = findViewById(R.id.c5); c6 = findViewById(R.id.c6);
        c7 = findViewById(R.id.c7); c8 = findViewById(R.id.c8);
        c9 = findViewById(R.id.c9); c10 = findViewById(R.id.c10);

        d1 = findViewById(R.id.d1); d2 = findViewById(R.id.d2);
        d3 = findViewById(R.id.d3); d4 = findViewById(R.id.d4);
        d5 = findViewById(R.id.d5); d6 = findViewById(R.id.d6);
        d7 = findViewById(R.id.d7); d8 = findViewById(R.id.d8);
        d9 = findViewById(R.id.d9); d10 = findViewById(R.id.d10);

        e1 = findViewById(R.id.e1); e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3); e4 = findViewById(R.id.e4);
        e5 = findViewById(R.id.e5); e6 = findViewById(R.id.e6);
        e7 = findViewById(R.id.e7); e8 = findViewById(R.id.e8);
        e9 = findViewById(R.id.e9); e10 = findViewById(R.id.e10);

        f1 = findViewById(R.id.f1); f2 = findViewById(R.id.f2);
        f3 = findViewById(R.id.f3); f4 = findViewById(R.id.f4);
        f5 = findViewById(R.id.f5); f6 = findViewById(R.id.f6);
        f7 = findViewById(R.id.f7); f8 = findViewById(R.id.f8);
        f9 = findViewById(R.id.f9); f10 = findViewById(R.id.f10);

        g1 = findViewById(R.id.g1); g2 = findViewById(R.id.g2);
        g3 = findViewById(R.id.g3); g4 = findViewById(R.id.g4);
        g5 = findViewById(R.id.g5); g6 = findViewById(R.id.g6);
        g7 = findViewById(R.id.g7); g8 = findViewById(R.id.g8);
        g9 = findViewById(R.id.g9); g10 = findViewById(R.id.g10);

        h1 = findViewById(R.id.h1); h2 = findViewById(R.id.h2);
        h3 = findViewById(R.id.h3); h4 = findViewById(R.id.h4);
        h5 = findViewById(R.id.h5); h6 = findViewById(R.id.h6);
        h7 = findViewById(R.id.h7); h8 = findViewById(R.id.h8);
        h9 = findViewById(R.id.h9); h10 = findViewById(R.id.h10);

        i1 = findViewById(R.id.i1); i2 = findViewById(R.id.i2);
        i3 = findViewById(R.id.i3); i4 = findViewById(R.id.i4);
        i5 = findViewById(R.id.i5); i6 = findViewById(R.id.i6);
        i7 = findViewById(R.id.i7); i8 = findViewById(R.id.i8);
        i9 = findViewById(R.id.i9); i10 = findViewById(R.id.i10);

        j1 = findViewById(R.id.j1); j2 = findViewById(R.id.j2);
        j3 = findViewById(R.id.j3); j4 = findViewById(R.id.j4);
        j5 = findViewById(R.id.j5); j6 = findViewById(R.id.j6);
        j7 = findViewById(R.id.j7); j8 = findViewById(R.id.j8);
        j9 = findViewById(R.id.j9); j10 = findViewById(R.id.j10);


        boardPlaceList = Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10,
                d1, d2,d3, d4, d5, d6, d7, d8, d9, d10, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10,
                g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10,
                j1, j2, j3, j4, j5, j6, j7, j8, j9, j10);
    }




    public void setBoardTiles(){

        Map<String, Long> tiles = new HashMap<>(gameDataController.aggregateUnplayedTiles());

        Map<String, List<String>> corps = gameDataController.liveCorporations;
        List<String> spark = corps.get("Spark");
        List<String> nestor = corps.get("Nestor");
        List<String> rove = corps.get("Rove");
        List<String> fleet = corps.get("Fleet");
        List<String> etch = corps.get("Etch");
        List<String> bolt = corps.get("Bolt");
        List<String> echo = corps.get("Echo");

        for(TextView tv : boardPlaceList){

            String title = (String) tv.getText();

            if (tiles.get(title) != null) {

                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.barely_rounded_text_background_rectangle));

            }else if(spark != null && spark.contains(title)){

                //spark colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.spark_tile));

            }else if(nestor != null && nestor.contains(title)){

                //nestor colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.nestor_tile));

            }else if(rove != null && rove.contains(title)){

                //rove colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.rove_tile));

            }else if(fleet != null && fleet.contains(title)){

                //fleet colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.fleet_tile));

            }
            else if(etch != null && etch.contains(title)){

                //etch colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.etch_tile));

            } else if(echo != null && echo.contains(title)){

                //echo colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.echo_tile));

            }else if(bolt != null && bolt.contains(title)){

                //bolt colors
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.bolt_tile));

            } else{
                tv.setTextColor(getColor(R.color.textBackgroundColor));
                tv.setBackground(getDrawable(R.drawable.barely_rounded_text_rectangle));

            }

        }
    }








    public void determinePlayTileAction(){
        String tile = selectedTile;
        if(tile.equals("")){
            hideTileActionsView();
            return;
        }
        //Here we need to evaluate the tile being laid and extract further actions
        List<String> results = gameDataController.evaluateAdjacentTiles(tile);

        if(results.size() < 1){
            return;
        }

        String message = results.get(0);

        Map<String, Long> uplayedTiles = gameDataController.aggregateUnplayedTiles();

        boolean didPlay = false;

        switch (message){

            case "playable":
                //playable, nothing special here
                playTileAction(tile);
                break;

            case "unplayable":
                //tile is unplayable, show user the prompt and do not play tiles
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

                break;

            case "starter":

                //here we need to se the new corp tile
                results.remove(0);
                List<String> available = new ArrayList<>();

                List<String> finalAStartingCorpTileArray = new ArrayList<>();


                for(String t : results){
                    if(uplayedTiles.get(t) == null){
                        finalAStartingCorpTileArray.add(t);
                    }
                }
                finalAStartingCorpTileArray.add(tile);


                for(String s : gameDataController.corporationNames){
                    if(gameDataController.getLiveCorporations().get(s) == null){
                        available.add(s);
                    }
                }

                if(available.size() == 0){
                    Toast.makeText(this, "No Corporations Remaining to start", Toast.LENGTH_SHORT).show();
                }else{
                    showStarterRadioButtonDialog(available, finalAStartingCorpTileArray, tile);
                }

                break;

            case "appending":

                //here we're appending tiles to an existing corporation

                results.remove(0);
                String corp = results.get(0);
                results.remove(0);

                List<String> finalAddingCorpTileArray = new ArrayList<>();
                List<String> appendingCorpList = gameDataController.getLiveCorporations().get(corp);

                for(String t : results){
                    if(uplayedTiles.get(t) == null && appendingCorpList != null && !appendingCorpList.contains(t)){
                        finalAddingCorpTileArray.add(t);
                    }
                }
                finalAddingCorpTileArray.add(tile);
                shrinkTileAnimation(tileBtnArray.get(selectedButtonIndex));
                //results should now just be the tiles to add to the existing corporation
                gameDataController.addTilesToCorpAndPlay(corp,finalAddingCorpTileArray, currentPlayer, tile);

                break;

            default:

                //default is a merger
                //if equal sizes, show user prompt, if not, auto put one under/into the other
                Map<String, Long> sizesMap = new HashMap<>();

                for(String s : results){
                    sizesMap.put(s, (long) gameDataController.getLiveCorporations().get(s).size());
                }

                //check to make sure the tile is playable
                int safeCount = 0;
                for(long l : sizesMap.values()){
                    if(l > 9){
                        if(++safeCount > 1){
                            String s = tile + " is Unplayable";
                            Toast.makeText(BasicGameActivity.this, s, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                String biggestName = "";
                long largest = 0;
                List<String> equal = new ArrayList<>();

                //here we need to either get the largest corporation or equals

                for(String s : sizesMap.keySet()){

                    long count = sizesMap.get(s);

                    if(count > largest){
                        biggestName = s;
                        largest = count;

                    }else if(count == largest){
                        if (!equal.contains(biggestName)){
                            equal.add(biggestName);
                        }
                        equal.add(s);
                    }
                }



                List<String> stragglerTileArray = new ArrayList<>();


                //this is to collect all loner tiles to add to final corp including the tile played
                List<String> allCorpTiles = gameDataController.aggregrateCorpTiles();
                for(String s : gameDataController.getAdjacentTileArray(tile)){
                    if(!uplayedTiles.containsKey(s) && !allCorpTiles.contains(s)){
                        stragglerTileArray.add(s);
                    }
                }
                stragglerTileArray.add(tile);


                if(equal.size() > 1 && equal.contains(biggestName)){
                    //show the prompt to choose the under corp
                    showMergerRadioButtonDialog(equal, results, stragglerTileArray, tile);

                }else{
                    //perform auto merge
                    gameDataController.mergeCorporations(biggestName, results, stragglerTileArray, tile, currentPlayer);
                    shrinkTileAnimation(tileBtnArray.get(selectedButtonIndex));
                }
                break;
        }
    }

    private void showStarterRadioButtonDialog(final List<String> corps, final List<String> startTiles, final String playingTile) {

        final String[] corp = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(BasicGameActivity.this);
        builder.setTitle("Choose a start corporation");

        String[] corpArr = new String[corps.size()];
        corpArr = corps.toArray(corpArr);


        builder.setSingleChoiceItems(corpArr, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                corp[0] = corps.get(item);
                Toast.makeText(getApplicationContext(), corps.get(item), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Start",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        String starting = corp[0];
                        if(starting.equals("")){
                            Toast.makeText(BasicGameActivity.this, "Choose a starting corporation", Toast.LENGTH_SHORT).show();
                        }else{

                            shrinkTileAnimation(tileBtnArray.get(selectedButtonIndex));
                            gameDataController.startCorporation(starting, startTiles, currentPlayer);
                            playTileAction(playingTile);

                        }

                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(BasicGameActivity.this, "Cancelled Merger", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }






    private void showMergerRadioButtonDialog(final List<String> equals, final List<String> involved ,final List<String> newTiles, final String startingTile) {

        final String[] corp = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(BasicGameActivity.this);
        builder.setTitle("Select WINNING Corporation");

        String[] corpArr = new String[equals.size()];
        corpArr = equals.toArray(corpArr);

        builder.setSingleChoiceItems(corpArr, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                corp[0] = equals.get(item);
                Toast.makeText(getApplicationContext(), equals.get(item), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setPositiveButton("Merge",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String winner = corp[0];

                        if(corp.equals("")){
                            Toast.makeText(BasicGameActivity.this, "Choose a corporation", Toast.LENGTH_SHORT).show();

                        }else{
                            //perform merge here
                            gameDataController.mergeCorporations(winner, involved, newTiles, startingTile, currentPlayer);

                        }

                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Toast.makeText(BasicGameActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }




    @Override
    public void onTileDrawn(String tile, int pos) {
        //re-eneable buttons
        tileBtnArray.get(pos).animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
        tileBtnArray.get(pos).setEnabled(true);
    }

    @Override
    public void onTileDiscarded(String tile, int pos) {

        //re-enable buttons
        tileBtnArray.get(pos).setEnabled(true);

    }


    public void onPaid(long amount) {

        if(amount > 1000){
            showPaymentDialog(amount, "Payday baby!", true);
        }else{
            showPaymentDialog(amount, "Eh, better than nothing!", true);
        }
    }


    public void onPayment(long amount) {

        if(amount > 2000){
            showPaymentDialog(amount, "That's a pretty penny", false);
        }else{
            showPaymentDialog(amount, "You Paid", false);
        }
    }

    boolean dialogShowing = false;
    private void showPaymentDialog(long amount, String title, boolean positive){

        AlertDialog.Builder builder;
        if(positive){
            builder = new AlertDialog.Builder(this, R.style.AlertDialogPaid);
        }else{
            builder = new AlertDialog.Builder(this, R.style.AlertDialogPayment);
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogShowing = false;
            }
        });


    // You can even inflate a layout to the AlertDialog.Builder, if looking to create a custom one.
    // Add and fill all required builder methods, as per your need.
    builder.setTitle(title);
    String message = "" + amount;
    builder.setMessage(message);



    // Now create object of AlertDialog from the Builder.
        final AlertDialog dialog = builder.create();


    // Let's start with animation work. We just need to create a style and use it here as follows.
        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;



        if(!dialogShowing){
            dialogShowing = true;
            dialog.show();
        }
    }


    private boolean turnDialogShowing = false;
    private void showTurnDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogPaid);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                turnDialogShowing = false;
            }
        });

        builder.setMessage("It's your turn");
        builder.setTitle("Hey");

        // Now create object of AlertDialog from the Builder.
        final AlertDialog dialog = builder.create();


        // Let's start with animation work. We just need to create a style and use it here as follows.
        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;


        if (!turnDialogShowing) {
            turnDialogShowing = true;
            dialog.show();
        }
    }


    private void disableActionsInput(){

        buyButton.setEnabled(false);
        sellButton.setEnabled(false);
        tradeButton.setEnabled(false);

        if(tileBtn1.getText().equals("") || tileBtn1.getText().equals("DRAW")){
            tileBtn1.setEnabled(false);
        }else{
            tileBtn1.setEnabled(true);
        }

        if(tileBtn2.getText().equals("") || tileBtn2.getText().equals("DRAW")){
            tileBtn2.setEnabled(false);
        }else{
            tileBtn2.setEnabled(true);
        }
        if(tileBtn3.getText().equals("") || tileBtn3.getText().equals("DRAW")){
            tileBtn3.setEnabled(false);
        }else{
            tileBtn3.setEnabled(true);
        }
        if(tileBtn4.getText().equals("") || tileBtn4.getText().equals("DRAW")){
            tileBtn4.setEnabled(false);
        }else{
            tileBtn4.setEnabled(true);
        }
        if(tileBtn5.getText().equals("") || tileBtn5.getText().equals("DRAW")){
            tileBtn5.setEnabled(false);
        }else{
            tileBtn5.setEnabled(true);
        }
        if(tileBtn6.getText().equals("") || tileBtn6.getText().equals("DRAW")){
            tileBtn6.setEnabled(false);
        }else{
            tileBtn6.setEnabled(true);
        }


        playTile.setEnabled(false);
        discardTile.setEnabled(false);
        endGameWithPayouts.setEnabled(false);
    }

    private void enableAllActions(){


        buyButton.setEnabled(true);
        sellButton.setEnabled(true);
        tradeButton.setEnabled(true);



        tileBtn1.setEnabled(true);
        tileBtn2.setEnabled(true);
        tileBtn3.setEnabled(true);
        tileBtn4.setEnabled(true);
        tileBtn5.setEnabled(true);
        tileBtn6.setEnabled(true);

        playTile.setEnabled(true);
        discardTile.setEnabled(true);
        endGameWithPayouts.setEnabled(true);
    }






}
