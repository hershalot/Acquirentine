package com.fenapnu.acquirentine_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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


    @Override
    protected void onResume() {
        super.onResume();
        getActiveGames();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

                if(nameET.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter a Name", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(MainActivity.this, BasicGameActivity.class);
                i.putExtra("isCreator", true);
                i.putExtra("userId", currentUser.getUserId());
                i.putExtra("userName", currentUser.getName());
                i.putExtra("gameName", currentUser.getName());
                startActivity(i);

            }
        });



        nameET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if(currentUser != null){
                    currentUser.name = s.toString();
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("name", currentUser.name);
                    FirebaseFirestore.getInstance().collection("Users").document(currentUser.userId).update(newData);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        refreshLayout.setRefreshing(true);
        getData();

    }




    public void getActiveGames(){
        FirebaseFirestore.getInstance().collection("ActiveGames").whereEqualTo("searchable", true).whereEqualTo("gameComplete", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
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







    public void getData(){

        getActiveGames();


        String userId = DataManager.getUserId(this);
        if(!userId.equals("")){


            FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.isSuccessful() || task.getResult() == null){
                        Toast.makeText(MainActivity.this, "Failed to get User Data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User u = task.getResult().toObject(User.class);
                    currentUser = u;

                    nameET.setText(u.getName());
                }
            });

        }else{

            //create user entry with name
            DocumentReference ref =  FirebaseFirestore.getInstance().collection("Users").document();
            String key = ref.getId();

            User u = new User();
            u.userId = key;
            u.gameId = "";
            u.inGame = false;
            u.name = nameET.getText().toString();
            currentUser = u;

            DataManager.setUserId(key, this);
            ref.set(u);
        }
    }






    @Override
    public void onGameClicked(GameObject game) {

        if(nameET.getText().toString().equals("")){
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(game.isGameStarted()){
            //if the game is already started we only want players already in the game to play
            if(game.getPlayerOrder().contains(currentUser.getUserId())){

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
        i.putExtra("userId", currentUser.getUserId());
        i.putExtra("userName", currentUser.getName());
        startActivity(i);


    }

    @Override
    public void onRefresh() {
        getActiveGames();
    }
}
