package com.fenapnu.acquirentine_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnGameClickListener, SwipeRefreshLayout.OnRefreshListener, UserSingleton.OnCurrentUserDataChanged {



    public Button createGame;
    public RecyclerView recyclerView;
    public TextView nameTV;
    public ImageButton changeName;
    private TextView noGamesTV;

    private GamesItemAdapter adapter;

    private List<GameObject> allGames = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;

    private ListenerRegistration gameListener;

    public static Context contextOfApplication;




    public void addAuthListener(){

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    showLoginActivity();
                }else{

                    UserSingleton.INSTANCE.setListeners();
                    getActiveGames();
                    sendTokenToServer();
                }
            }
        });
    }

    public void showLoginActivity(){

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }



    private void sendTokenToServer(){

        if(FirebaseAuth.getInstance().getCurrentUser() == null || FirebaseAuth.getInstance().getUid() == null || FirebaseAuth.getInstance().getUid().equals("")) {
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        if(!token.equals(UserSingleton.INSTANCE.getCurrentUser().getToken()) && FirebaseAuth.getInstance().getUid() != null){
//                            currentUser.setToken(currentUser.getToken());
//                            DataManager.setLocalUserObject(currentUser);
                            Map<String,Object> tokenUpdate = new HashMap<>();
                            tokenUpdate.put("token",token);
                            tokenUpdate.put("deviceOS", "android");

                            DataManager.getUsersPath().document(FirebaseAuth.getInstance().getUid()).update(tokenUpdate);
                        }
                    }
                });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            setContentView(R.layout.activity_main_landscape);
        }else {
            // Portrait
            setContentView(R.layout.activity_main);
        }

        contextOfApplication = getApplicationContext();
        createGame = findViewById(R.id.create_game);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        UserSingleton.INSTANCE.setLocalUserDataChangedListener(this);
        addAuthListener();

        createGame = findViewById(R.id.create_game);
        recyclerView = findViewById(R.id.recycler_view);
        nameTV = findViewById(R.id.name_et);
        changeName = findViewById(R.id.change_name_btn);
        noGamesTV = findViewById(R.id.no_games_lbl);

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNamePrompt();
            }
        });

        int VERTICAL_ITEM_SPACE = 25;
        recyclerView.addItemDecoration(new SpacingItemDecoration(VERTICAL_ITEM_SPACE, VERTICAL_ITEM_SPACE));

        adapter = new GamesItemAdapter(this, allGames, this);

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

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FirebaseAuth.getInstance().getCurrentUser() == null || UserSingleton.INSTANCE.getCurrentUser().getUid().equals("")){
                    Toast.makeText(MainActivity.this, "User is null, try logging out and in again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(nameTV.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter a Name", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(MainActivity.this, BasicGameActivity.class);
                i.putExtra("isCreator", true);
                i.putExtra("userId", UserSingleton.INSTANCE.getCurrentUser().getUid());
                i.putExtra("userName", UserSingleton.INSTANCE.getCurrentUser().getName());
                i.putExtra("gameName", UserSingleton.INSTANCE.getCurrentUser().getName());
                startActivity(i);
            }
        });

        refreshLayout.setRefreshing(true);
        setUserData();
    }



    private void handleLogout(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();

        if(uid == null) {
            return;
        }

        try{
            mAuth.signOut();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }





    public void setUserData(){

        if(!UserSingleton.INSTANCE.getUserIsLoggedIn() || UserSingleton.INSTANCE.getCurrentUser().getUid() == null){
            return;
        }

        nameTV.setText(UserSingleton.INSTANCE.getCurrentUser().getName());

    }



    private void changeNamePrompt(){

        if(!UserSingleton.INSTANCE.getUserIsLoggedIn()){
            showLoginActivity();
            return;
        }

        final EditText taskEditText = new EditText(MyApplication.getContext());
        taskEditText.setAllCaps(false);
        taskEditText.setHint("Enter Name");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Name")
                .setView(taskEditText)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        final String s = String.valueOf(taskEditText.getText());

                        if (s.equals("")) {
                            Toast.makeText(MainActivity.this, "Enter a code", Toast.LENGTH_SHORT).show();
                        }

                        CollectionReference userRef = DataManager.getUsersPath();
                        userRef.document(UserSingleton.INSTANCE.getCurrentUser().getUid()).update("name",s);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })

                .create();
        dialog.show();
    }




    public void getActiveGames(){

        if (gameListener != null) {
            gameListener.remove();
            gameListener = null;
        }

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



        if(UserSingleton.INSTANCE.getCurrentUser().getUid().equals("")){
            return;
        }
        if(gameListener != null){
            gameListener.remove();
            gameListener = null;
        }
        if(nameTV.getText().toString().equals("")){
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> name = new HashMap<>();
        name.put("name", nameTV.getText().toString());
        FirebaseFirestore.getInstance().collection("Users").document(UserSingleton.INSTANCE.getCurrentUser().getUid()).update(name);
        UserSingleton.INSTANCE.getCurrentUser().setName(nameTV.getText().toString());

        if(game.isGameStarted()){
            //if the game is already started we only want players already in the game to play
            if(game.getPlayerOrder().contains(UserSingleton.INSTANCE.getCurrentUser().getUid())){

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
        i.putExtra("userId", UserSingleton.INSTANCE.getCurrentUser().getUid());
        i.putExtra("userName", UserSingleton.INSTANCE.getCurrentUser().getName());
        startActivity(i);


    }

    @Override
    public void onRefresh() {
        getActiveGames();
    }

    @Override
    public void onUserUpdated(@NotNull User user) {
        setUserData();
    }
}
