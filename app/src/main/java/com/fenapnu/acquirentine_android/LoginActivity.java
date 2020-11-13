package com.fenapnu.acquirentine_android;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



import java.util.Arrays;



/**
 * Created by Fenapnu on 3/1/18.
 *

 */



public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private int RC_SIGN_IN = 34;
    private User workingUser;

    private LinearLayout fb;

    private EditText emailLoginField;
    private EditText passwordLoginField;
    private TextView loginInfoTV;
    private ProgressBar firebaseLoginProgress;
    private Button firebaseLoginButton;




    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        firebaseLoginButton = findViewById(R.id.firebase_login_button);


        emailLoginField = findViewById(R.id.email_field);
        passwordLoginField = findViewById(R.id.password_field);
        loginInfoTV = findViewById(R.id.login_login_info_tv);


        loginInfoTV.setHintTextColor(getColor(R.color.placeholderColor));
        passwordLoginField.setHintTextColor(getColor(R.color.placeholderColor));
        emailLoginField.setHintTextColor(getColor(R.color.placeholderColor));

        Button forgotPasswordButton = findViewById(R.id.forgot_password_btn);
        Button registerButton = findViewById(R.id.register_here_btn);


        firebaseLoginProgress = findViewById(R.id.firebase_login_progress_bar);
        firebaseLoginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String email = "";
                String password = "";

                email = emailLoginField.getText().toString();
                password = passwordLoginField.getText().toString();

                showFirebaseLoginProgress();

                if(!email.equals("") && !password.equals("") ){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        dismissFirebaseLoginProgress();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        existingUserCheck();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                        dismissFirebaseLoginProgress();
                                        String failed = "Login Failed";
                                        loginInfoTV.setText(failed);
                                    }
                                }
                            });
                }else{

                    dismissFirebaseLoginProgress();
                    String failed = "Please enter valid credentials";
                    loginInfoTV.setText(failed);
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //register view
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //password reset view
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });

        ProgressBar progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();



        /*
         *
         * set TESTING to FALSE for PRODUCTION / TRUE for TESTING
         *
         */

//        DataManager.setTesting(true);

        /*
         *
         * END set TESTING to FALSE for PRODUCTION
         *
         */

    }



    @Override
    protected void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            onBackPressed();
        }
    }





    private void showFirebaseLoginProgress(){

        firebaseLoginProgress.setVisibility(View.VISIBLE);
        firebaseLoginButton.setEnabled(false);

    }


    private void dismissFirebaseLoginProgress(){

        firebaseLoginProgress.setVisibility(View.INVISIBLE);
        firebaseLoginButton.setEnabled(true);
    }










    //Checks Firebase for existing User Data. If it exits, Check for Subscription
    public void existingUserCheck() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String uid = FirebaseAuth.getInstance().getUid();

        if(uid == null){
            return;
        }

        final DocumentReference myRef = DataManager.getUsersPath().document(uid);

        myRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if(task.getResult() == null){
                        return;
                    }

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        User tmp;
                        try{
                            tmp = document.toObject(User.class);
                            Log.d(TAG, "Value is: " + tmp);
                        }catch(Exception e){

                            tmp = null;
                        }

                        final User user = tmp;
                        if(user != null){
                            Log.d(TAG, "User Not Null");

                        }

                        DataManager.setLocalUserObject(user);
                        onBackPressed();

                    } else {
                        Log.d(TAG, "No such document");

                        //CREATE NEW USER -- check for promotional period
                        Log.d(TAG, "Creating New User");

                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if(fUser == null){
                            return;
                        }

                        String email = fUser.getEmail();
                        String displayName = fUser.getDisplayName();


                        if (email == null) {
                            email = "";
                        }
                        if (displayName == null) {
                            displayName = "";
                        }

                        User newUser = new User();

                        newUser.setEmail(email);
                        newUser.setName(displayName);

                        newUser.setUid(fUser.getUid());



                        //save new user data locally and in cloud
                        DataManager.setLocalUserObject(newUser);
                        myRef.set(newUser);
                        onBackPressed();
                    }


                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }




    private void handleLogout(){

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



}
