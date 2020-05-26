package com.fenapnu.acquirentine_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnGameClickListener, SwipeRefreshLayout.OnRefreshListener {



    public Button createGame;
    public RecyclerView recyclerView;
    public EditText nameET;
    private TextView noGamesTV;

    private GamesItemAdapter adapter;

    private List<GameObject> allGames = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private User currentUser;
    private boolean databaseSetting = false;


    private ListenerRegistration userListener;
    private ListenerRegistration gameListener;
    private FirebaseAuth mAuth;

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    private String TAG = "MAIN ACTIVITY";





    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {

            // User is not signed in
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

        }else{
            createGame.setEnabled(true);
            getActiveGames();
            if(userListener == null){
                setUserDataListener();
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            setContentView(R.layout.activity_main_landscape);
        }
        else {
            // Portrait
            setContentView(R.layout.activity_main);
        }

        contextOfApplication = getApplicationContext();
        createGame = findViewById(R.id.create_game);
        createGame.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null) {

                    //user is logged in
                    setUserDataListener();
                    createGame.setEnabled(true);
                }else{

                    if(userListener != null){
                        userListener.remove();
                    }

                }
            }
        });



        createGame = findViewById(R.id.create_game);
        recyclerView = findViewById(R.id.recycler_view);
        nameET = findViewById(R.id.name_et);
        noGamesTV = findViewById(R.id.no_games_lbl);

        int VERTICAL_ITEM_SPACE = 25;
        recyclerView.addItemDecoration(new SpacingItemDecoration(VERTICAL_ITEM_SPACE, VERTICAL_ITEM_SPACE));

        adapter = new GamesItemAdapter(this, allGames, this);
        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);


        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.inGameColor,
                R.color.reverseInGameColor);

        createGame.requestFocus();
        nameET.clearFocus();




        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentUser == null){

                    return;
                }

                if(nameET.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter a Name", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> name = new HashMap<>();
                name.put("name",nameET.getText().toString());
                FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).update(name);
                currentUser.setName(nameET.getText().toString());

                if(userListener != null){
                    userListener.remove();
                    userListener = null;
                }

                Intent i = new Intent(MainActivity.this, BasicGameActivity.class);
                i.putExtra("isCreator", true);
                i.putExtra("userId", currentUser.getUid());
                i.putExtra("userName", currentUser.getName());
                i.putExtra("gameName", currentUser.getName());
                startActivity(i);

            }
        });





        refreshLayout.setRefreshing(true);

    }







    public void setUserDataListener(){

        if(mAuth.getCurrentUser() == null){
            return;
        }

        final DocumentReference docRef = DataManager.getUsersPath().document(mAuth.getCurrentUser().getUid());
        userListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    User user = snapshot.toObject(User.class);

                    currentUser = user;
                    DataManager.setLocalUserObject(user);

                    nameET.setText(user.getName());

//                    nameET.addTextChangedListener(new TextWatcher() {
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
//
//                            if(currentUser != null){
//                                currentUser.name = s.toString();
//                                Map<String, Object> newData = new HashMap<>();
//                                newData.put("name", currentUser.name);
//                                FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).update(newData);
//                            }
//                        }
//
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start,
//                                                  int before, int count) {
//
//                        }
//                    });

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }








    public void getActiveGames(){

        gameListener = FirebaseFirestore.getInstance().collection("ActiveGames").whereEqualTo("searchable", true).whereEqualTo("gameComplete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null || queryDocumentSnapshots == null){
                    Toast.makeText(MainActivity.this, "Failed to get Active Games", Toast.LENGTH_SHORT).show();
                    return;
                }

                allGames.clear();

                for (DocumentSnapshot snap : queryDocumentSnapshots){

                    GameObject game = new GameObject();

                    game.setPlayerOrder((List<String>) snap.get("playerOrder"));
                    game.setCards((Map<String, Long>) snap.get("cards"));
                    game.setGameName((String) snap.get("gameName"));
                    game.setTiles((Map<String, Long>) snap.get("tiles"));
                    game.setGameId((String) snap.get("gameId"));
                    game.setSearchable(true);
                    game.setCreator( (String) snap.get("creator"));
                    game.setGameComplete((boolean) snap.get("gameComplete"));
                    game.setGameStarted((boolean) snap.get("gameStarted"));

                    Map<String, Map<String, Object>> rawPlayers = (Map<String, Map<String, Object>>) snap.get("players");
                    Map<String, Player> ps = new HashMap<>();

                    for(Map<String, Object> p : rawPlayers.values()){

                        String key =  (String) p.get("userId");
                        ps.put(key, new Player(p));
                    }

                    game.setPlayers(ps);
                    allGames.add(game);
                }
                if(allGames.size() == 0){
                    noGamesTV.setVisibility(View.VISIBLE);
                }else{
                    noGamesTV.setVisibility(View.INVISIBLE);
                }

                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });


    }





    @Override
    public void onGameClicked(GameObject game) {


        if(currentUser == null){
            return;
        }
        if(userListener != null){
            userListener.remove();
            userListener = null;
        }
        if(gameListener != null){
            gameListener.remove();
            gameListener = null;
        }
        if(nameET.getText().toString().equals("")){
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> name = new HashMap<>();
        name.put("name",nameET.getText().toString());
        FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).update(name);
        currentUser.setName(nameET.getText().toString());

        if(game.isGameStarted()){
            //if the game is already started we only want players already in the game to play
            if(game.getPlayerOrder().contains(currentUser.getUid())){

                //here we can send user to game
                gotoGameActivity(game);
            }
        }else{
            gotoGameActivity(game);
        }
    }





    public void gotoGameActivity(GameObject game){

        Intent i = new Intent(this, BasicGameActivity.class);
        i.putExtra("isCreator", false);
        i.putExtra("gameObject", DataManager.serializeGameData(game));
        i.putExtra("userId", currentUser.getUid());
        i.putExtra("userName", currentUser.getName());
        startActivity(i);


    }

    @Override
    public void onRefresh() {
        getActiveGames();
    }
}
