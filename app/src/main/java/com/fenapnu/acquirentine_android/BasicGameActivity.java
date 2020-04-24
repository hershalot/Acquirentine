package com.fenapnu.acquirentine_android;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BasicGameActivity extends AppCompatActivity implements OnPlayerClickListener{


    TextView a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10,
            d1, d2,d3, d4, d5, d6, d7, d8, d9, d10, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10,
            g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10,
            j1, j2, j3, j4, j5, j6, j7, j8, j9, j10;


    List<TextView> boardPlaceList = new ArrayList<>();


    //CARD OPTIONs
    Button buyButton;
    Button sellButton;
    Button tradeButton;
    Button closePopup;
    Button endGame;
    ImageButton toggleTileGrid;
    GridLayout tileLayout;

    ImageButton togglePlayersBtn;


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
    Button drawTileBtn;
    Button startGameBtn;
    ImageButton toggleCardsButton;

    LinearLayout tileButtonsLayout;
    LinearLayout cardsLayout;

    TextView gameNameTV;
    RecyclerView recyclerView;
    PlayersItemAdapter adapter;


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
    String selectedCorporation = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_game_layout);

        recyclerView = findViewById(R.id.players_rv);
        cardsLayout = findViewById(R.id.cards_layout);

        int VERTICAL_ITEM_SPACE = 5;
        recyclerView.addItemDecoration(new SpacingItemDecoration(VERTICAL_ITEM_SPACE, VERTICAL_ITEM_SPACE));

        adapter = new PlayersItemAdapter(this, playerList, gameDataController,this);
        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);

        tileBtn1 = findViewById(R.id.tile1_btn);
        tileBtn2 = findViewById(R.id.tile2_btn);
        tileBtn3 = findViewById(R.id.tile3_btn);
        tileBtn4 = findViewById(R.id.tile4_btn);
        tileBtn5 = findViewById(R.id.tile5_btn);
        tileBtn6 = findViewById(R.id.tile6_btn);

        lastPlayedTileLbl = findViewById(R.id.last_tile_played);
        closeTiles = findViewById(R.id.close_tiles_btn);
        closeTiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
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


        playTile = findViewById(R.id.play_tile_btn);
        playTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTileAction();
                resetButtonBackgrounds();
                selectedTile = "";
            }
        });

        discardTile = findViewById(R.id.discard_tile_btn);
        discardTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unplayableTileAction();
                resetButtonBackgrounds();
                selectedTile = "";
            }
        });
        tileButtonsLayout = findViewById(R.id.tile_button_layout);

        drawTileBtn = findViewById(R.id.draw_tile_btn);
//        drawTileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawTileAction();
//
//            }
//        });

        tileBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }

                if(tileBtn1.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn1.setVisibility(View.INVISIBLE);
                    return;
                }
                selectedTile = tileBtn1.getText().toString();

                resetButtonBackgrounds();
                tileBtn1.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });


        tileBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }

                if(tileBtn2.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn2.setVisibility(View.INVISIBLE);
                    return;
                }
                selectedTile = tileBtn2.getText().toString();

                resetButtonBackgrounds();
                tileBtn2.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });


        tileBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }

                if(tileBtn3.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn3.setVisibility(View.INVISIBLE);
                    return;
                }
                selectedTile = tileBtn3.getText().toString();

                resetButtonBackgrounds();
                tileBtn3.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });

        tileBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }

                if(tileBtn4.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn4.setVisibility(View.INVISIBLE);
                    return;
                }
                selectedTile = tileBtn4.getText().toString();

                resetButtonBackgrounds();
                tileBtn4.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });

        tileBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }

                if(tileBtn5.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn5.setVisibility(View.INVISIBLE);
                    return;
                }
                selectedTile = tileBtn5.getText().toString();

                resetButtonBackgrounds();
                tileBtn5.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });


        tileBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gameDataController != null && !gameDataController.isGameStarted()){
                    return;
                }


                if(tileBtn6.getText().equals("Draw") && gameDataController.getTiles().size() > 0){
                    drawTileAction();
                    return;
                }else if(gameDataController.getTiles().size() < 1){
                    Toast.makeText(BasicGameActivity.this, "No Tiles Remain", Toast.LENGTH_LONG).show();
                    tileBtn6.setVisibility(View.INVISIBLE);
                    return;
                }

                selectedTile = tileBtn6.getText().toString();
                resetButtonBackgrounds();
                tileBtn6.setBackground(getDrawable(R.drawable.rounded_green_rectangle_bordered));
                showTileActionsView();
            }
        });





        tileBtn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        tileBtn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        tileBtn3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        tileBtn4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        tileBtn5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        tileBtn6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });



        tileBtnArray.add(tileBtn1);
        tileBtnArray.add(tileBtn2);
        tileBtnArray.add(tileBtn3);
        tileBtnArray.add(tileBtn4);
        tileBtnArray.add(tileBtn5);
        tileBtnArray.add(tileBtn6);

        startGameBtn = findViewById(R.id.start_game_btn);
        gameNameTV = findViewById(R.id.game_name);

        Intent i = getIntent();
        boolean sCreator = i.getBooleanExtra("isCreator", false);

        if(sCreator){


            gameDataController = new GameObject();
            currentUid = i.getStringExtra("userId");
            userName = i.getStringExtra("userName");
            gameName = i.getStringExtra("gameName");
            String title = gameName + "'s Game";
            gameNameTV.setText(title);
            endGame.setVisibility(View.VISIBLE);
            setNewGameData();

        }else{

            String sGame  = i.getStringExtra("gameObject");
            gameDataController = DataManager.deserializeGameData(sGame);
            currentUid = i.getStringExtra("userId");
            userName = i.getStringExtra("userName");
            gameName = gameDataController.gameName;

            gameInSession = gameDataController.isGameStarted();
            String title = gameName + "'s Game";
            gameNameTV.setText(title);

            if(gameDataController.isGameStarted()){
                startGameBtn.setVisibility(View.GONE);
                if(currentUid.equals(gameDataController.getCreator())){
                    endGame.setVisibility(View.VISIBLE);
                }
            }else if(!currentUid.equals(gameDataController.getCreator())){
                String s = "Leave Game";
                startGameBtn.setText(s);
                startGameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gameDataController.removePlayer(currentUid, currentPlayer.startTile);
                        onBackPressed();
                    }
                });

            }else{
                endGame.setVisibility(View.VISIBLE);
                startGameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGameCheck();
                    }
                });
            }

            if(gameDataController.players.get(currentUid) != null){
                setGameListener();
            }else {
                addPlayer();
            }
        }



        buyButton = findViewById(R.id.buy_btn);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyAmount = buyEt.getText().toString();
                buyAmount = buyAmount.trim();
                if(buyAmount.equals("")){
                    Toast.makeText(BasicGameActivity.this, "Enter a valid purchase amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                Long a = Long.valueOf(buyAmount);
                int amount = a.intValue();

                if(amount > 4){
                    Toast.makeText(BasicGameActivity.this, "What are you, a cheater?", Toast.LENGTH_SHORT).show();
                    return;
                }

                Long cardsLeft = gameDataController.getCards().get(selectedCorporation);

                if(amount <= cardsLeft){

                    gameDataController.buy(amount, currentPlayer, selectedCorporation);

                    if(selectedCorporation.equals("Etch") && amount == 4){
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
                if(sellAmount.equals("")){
                    Toast.makeText(BasicGameActivity.this, "Enter a valid sell amount", Toast.LENGTH_SHORT).show();
                    return;
                }


                Long a = Long.valueOf(sellAmount);
                int amount = a.intValue();
                if(amount <= currentPlayer.getCards().get(selectedCorporation)) {
                    gameDataController.sell(amount, currentPlayer, selectedCorporation);
                }else{
                    String s = "You don't have enough" + selectedCorporation + " to sell, my poor chum";
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

                if(selected.equals("Select Corporation") || selected.equals(selectedCorporation)){
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

        buyEt = findViewById(R.id.buy_et);
        sellEt = findViewById(R.id.sell_et);
        tradeForLbl = findViewById(R.id.trade_for_tv);
        tradeEt = findViewById(R.id.trade_et);

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


    private void tileClickAction(){




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
        buyEt.setText("");
        sellEt.setText("");
        tradeEt.setText("");
        String s = " For ";
        tradeForLbl.setText(s);



        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(tradeForLbl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tradeEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(sellEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(buyEt.getWindowToken(), 0);



    }

    private void setAndShowPopupData(String corporation){


        switch (corporation){


            case "Spark":

//                alphaView.setBackgroundColor(getColor(R.color.sparkColor));

                buyLbl.setTextColor(getColor(R.color.sparkColor));
                sellLbl.setTextColor(getColor(R.color.sparkColor));
                tradeLbl.setTextColor(getColor(R.color.sparkColor));
                break;

            case "Nestor":
//                alphaView.setBackgroundColor(getColor(R.color.nestorColor));

                buyLbl.setTextColor(getColor(R.color.nestorColor));
                sellLbl.setTextColor(getColor(R.color.nestorColor));
                tradeLbl.setTextColor(getColor(R.color.nestorColor));
                break;

            case "Rove":

//                alphaView.setBackgroundColor(getColor(R.color.roveColor));

                buyLbl.setTextColor(getColor(R.color.roveColor));
                sellLbl.setTextColor(getColor(R.color.roveColor));
                tradeLbl.setTextColor(getColor(R.color.roveColor));
                break;

            case "Fleet":
//                alphaView.setBackgroundColor(getColor(R.color.fleetColor));

                buyLbl.setTextColor(getColor(R.color.fleetColor));
                sellLbl.setTextColor(getColor(R.color.fleetColor));
                tradeLbl.setTextColor(getColor(R.color.fleetColor));
                break;
            case "Etch":
//                alphaView.setBackgroundColor(getColor(R.color.etchColor));

                buyLbl.setTextColor(getColor(R.color.etchColor));
                sellLbl.setTextColor(getColor(R.color.etchColor));
                tradeLbl.setTextColor(getColor(R.color.etchColor));
                break;

            case "Bolt":
//                alphaView.setBackgroundColor(getColor(R.color.boltColor));

                buyLbl.setTextColor(getColor(R.color.boltColor));
                sellLbl.setTextColor(getColor(R.color.boltColor));
                tradeLbl.setTextColor(getColor(R.color.boltColor));
                break;

            case "Echo":
//                alphaView.setBackgroundColor(getColor(R.color.echoColor));

                buyLbl.setTextColor(getColor(R.color.echoColor));
                sellLbl.setTextColor(getColor(R.color.echoColor));
                tradeLbl.setTextColor(getColor(R.color.echoColor));
                break;

        }

        selectedCorporation = corporation;
        buyLbl.setText(corporation);
        sellLbl.setText(corporation);
        tradeLbl.setText(corporation);
        String s = " For 0 ";
        tradeForLbl.setText(s);

        buyselltradeView.setVisibility(View.VISIBLE);

    }



    private void showTileActionsView(){

        tileButtonsLayout.setVisibility(View.VISIBLE);
    }





    private void initializePlayerUI(){

        String open = "Draw";
        tileBtn1.setText(open);
        tileBtn1.setTextColor(getColor(R.color.inGameColor));
        tileBtn2.setText(open);
        tileBtn2.setTextColor(getColor(R.color.inGameColor));
        tileBtn3.setText(open);
        tileBtn3.setTextColor(getColor(R.color.inGameColor));
        tileBtn4.setText(open);
        tileBtn4.setTextColor(getColor(R.color.inGameColor));
        tileBtn5.setText(open);
        tileBtn5.setTextColor(getColor(R.color.inGameColor));
        tileBtn6.setText(open);
        tileBtn6.setTextColor(getColor(R.color.inGameColor));
    }





    private void setNewGameData(){

        Map<String, Object> newGameData = new HashMap<>();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("ActiveGames").document();
        String key = ref.getId();
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
        newGameData.put("moveDescription", "");



        initializePlayerUI();

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
        currentPlayer = gameDataController.addPlayer(currentUid, userName);
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








    private void drawTileAction() {


        if(!canDraw()){
            drawTileBtn.setVisibility(View.INVISIBLE);
            return;
        }

        resetButtonBackgrounds();
        selectedTile = "";


        gameDataController.drawTile(currentPlayer);


    }



    private void playTileAction() {

        if(selectedTile.equals("Draw") || selectedTile.equals("")){
            return;
        }


        tileButtonsLayout.setVisibility(View.INVISIBLE);
        gameDataController.playTile(selectedTile, currentPlayer);
    }




    private void unplayableTileAction() {

        if(selectedTile.equals("Open")){
            return;
        }
        tileButtonsLayout.setVisibility(View.INVISIBLE);

        gameDataController.removeTileFromHand(selectedTile, currentPlayer);
    }




    private void setGameListener(){


        gameListener = FirebaseFirestore.getInstance().collection("ActiveGames").document(gameDataController.gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(e != null || !documentSnapshot.exists()) {
                    Toast.makeText(BasicGameActivity.this, "Failed to update game data", Toast.LENGTH_SHORT).show();
                    return;
                }

                GameObject game = new GameObject();

                game.setPlayerOrder((List<String>) documentSnapshot.get("playerOrder"));
                game.setCards((Map<String, Long>) documentSnapshot.get("cards"));
                game.setGameName((String) documentSnapshot.get("gameName"));

                Map<String, Long>  newTileBag = (Map<String, Long>) documentSnapshot.get("tiles");
                if(newTileBag == null){
                    game.setTiles(new HashMap<String, Long>());
                }else{
                    game.setTiles(newTileBag);
                }


                game.setGameId((String) documentSnapshot.get("gameId"));
                game.setSearchable((boolean) documentSnapshot.get("searchable"));
                game.setGameStarted((boolean) documentSnapshot.get("gameStarted"));
                game.setGameComplete((boolean) documentSnapshot.get("gameComplete"));
                game.setCreator((String) documentSnapshot.get("creator"));
                game.setLastTilePlayed((String) documentSnapshot.get("lastTilePlayed"));
                game.setMoveDescription((String) documentSnapshot.get("moveDescription"));

                Map<String, Map<String, Object>> rawPlayers = (Map<String, Map<String, Object>>) documentSnapshot.get("players");
                Map<String, Player> ps = new HashMap<>();

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
                refreshUI(oldMessage, oldTile);

            }
        });

    }




    private void refreshUI(String oldMessage, String oldTile){

        if(gameDataController.isGameComplete() && !gameDataController.isSearchable()){
            if(gameListener != null){
                gameListener.remove();
            }

            onBackPressed();
            return;
        }

        Map<String, Long> cardsRemaining = gameDataController.cards;
        Map<String, Player> players = gameDataController.players;

        currentPlayer = players.get(currentUid);
        playerList.clear();

        for (Player player : players.values()) {
            playerList.add(player);
        }

        adapter.notifyDataSetChanged();

        String rSpark = "R: " + cardsRemaining.get(Corporation.SPARK.label) + " ";
        String rNestor = "R: " + cardsRemaining.get(Corporation.NESTOR.label) + " ";
        String rFleet= "R: " + cardsRemaining.get(Corporation.FLEET.label) + " ";
        String rRove = "R: " + cardsRemaining.get(Corporation.ROVE.label) + " ";
        String rEtch = "R: " + cardsRemaining.get(Corporation.ETCH.label) + " ";
        String rBolt = "R: " + cardsRemaining.get(Corporation.BOLT.label) + " ";
        String rEcho = "R: " + cardsRemaining.get(Corporation.ECHO.label) + " ";


        sparkRemaining.setText(rSpark);
        nestorRemaining.setText(rNestor);
        roveRemaining.setText(rRove);
        fleetRemaining.setText(rFleet);
        etchRemaining.setText(rEtch);
        boltRemaining.setText(rBolt);
        echoRemaining.setText(rEcho);


        if(currentPlayer != null){
            String sSpark = "" + currentPlayer.cards.get(Corporation.SPARK.label) + " Spark ";
            String sNestor = "" + currentPlayer.cards.get(Corporation.NESTOR.label) + " Nestor ";
            String sFleet= "" + currentPlayer.cards.get(Corporation.FLEET.label) + " Fleet ";
            String sRove = "" + currentPlayer.cards.get(Corporation.ROVE.label) + " Rove ";
            String sEtch = "" + currentPlayer.cards.get(Corporation.ETCH.label) + " Etch ";
            String sBolt = "" + currentPlayer.cards.get(Corporation.BOLT.label) + " Bolt ";
            String sEcho = "" + currentPlayer.cards.get(Corporation.ECHO.label) + " Echo ";

            sparkCount.setText(sSpark);

            nestorCount.setText(sNestor);
            roveCount.setText(sRove);
            fleetCount.setText(sFleet);
            etchCount.setText(sEtch);
            boltCount.setText(sBolt);
            echoCount.setText(sEcho);
        }



        if(gameDataController.isGameStarted()){

            if(!gameDataController.getMoveDescription().equals(oldMessage)){
                Toast.makeText(this, gameDataController.moveDescription, Toast.LENGTH_LONG).show();
            }

            if(!gameDataController.getLastTilePlayed().equals(oldTile)){
                lastPlayedTileLbl.setText(gameDataController.getLastTilePlayed());
            }
        }


//        if(currentPlayer != null && gameDataController.tiles.size() > 1) {
//
//            drawTileBtn.setVisibility(View.VISIBLE);
//
//        }

        if(gameDataController.isGameStarted()){
            startGameBtn.setVisibility(View.INVISIBLE);
        }

        if(currentPlayer != null){
            setTileButtons(currentPlayer.tiles);
            setBoardTiles();
        }
    }





    private void setTileButtons(List<String> tiles){

        resetButtonTitles();
        resetButtonBackgrounds();

        for (int i = 0; i < tiles.size(); i++){

            if(tiles.get(i).equals("")){
                String draw = "Draw";
                tileBtnArray.get(i).setText(draw);
                tileBtnArray.get(i).setTextColor(getColor(R.color.inGameColor));
            }else{
                tileBtnArray.get(i).setTextColor(getColor(R.color.textColor));
                tileBtnArray.get(i).setText(tiles.get(i));
            }

        }

        if(!canDraw()){

            drawTileBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void resetButtonBackgrounds(){
        tileBtn1.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
        tileBtn2.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
        tileBtn3.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
        tileBtn4.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
        tileBtn5.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));
        tileBtn6.setBackground(getDrawable(R.drawable.rounded_white_rectangle_bordered));

    }

    private void resetButtonTitles(){
        String open = "Draw";
        tileBtn1.setText(open);
        tileBtn1.setTextColor(getColor(R.color.inGameColor));
        tileBtn2.setText(open);
        tileBtn2.setTextColor(getColor(R.color.inGameColor));
        tileBtn3.setText(open);
        tileBtn3.setTextColor(getColor(R.color.inGameColor));
        tileBtn4.setText(open);
        tileBtn4.setTextColor(getColor(R.color.inGameColor));
        tileBtn5.setText(open);
        tileBtn5.setTextColor(getColor(R.color.inGameColor));
        tileBtn6.setText(open);
        tileBtn6.setTextColor(getColor(R.color.inGameColor));

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

        Map<String, Long> tiles = new HashMap<>(gameDataController.getTiles());
        Map<String, Player> players = new HashMap<>(gameDataController.getPlayers());

        for(Player p : players.values()){

            for(String s : p.getTiles()){

                tiles.put(s, (long) 1);
            }
        }

        for(TextView tv : boardPlaceList){

            String title = (String) tv.getText();

            if (tiles.get(title) == null) {
                tv.setTextColor(getColor(R.color.textBackgroundColor));
                tv.setBackground(getDrawable(R.drawable.barely_rounded_text_rectangle));
            }else{
                tv.setTextColor(getColor(R.color.textColor));
                tv.setBackground(getDrawable(R.drawable.barely_rounded_text_background_rectangle));

            }
        }

    }









}
