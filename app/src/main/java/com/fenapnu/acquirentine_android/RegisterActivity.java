package com.fenapnu.acquirentine_android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class RegisterActivity extends AppCompatActivity {

    private int PICTURE_REQUEST = 27;
    private String TAG = "";
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView registerInfoTV;
    private ProgressBar registerProgress;
    private Button registerButton;
    private EditText nameField;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.register_email_tv);
        passwordEditText = findViewById(R.id.register_password_tv);
        confirmPasswordEditText = findViewById(R.id.register_confirm_password_tv);
        ImageButton backButton = findViewById(R.id.close_register_btn);
        registerButton = findViewById(R.id.register_button);
        registerInfoTV = findViewById(R.id.register_info_tv);
        registerProgress = findViewById(R.id.register_progress_bar);
        nameField = findViewById(R.id.register_name_et);


        confirmPasswordEditText.setHintTextColor(getColor(R.color.placeholderColor));
        passwordEditText.setHintTextColor(getColor(R.color.placeholderColor));
        emailEditText.setHintTextColor(getColor(R.color.placeholderColor));
        nameField.setHintTextColor(getColor(R.color.placeholderColor));


        mAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showFirebaseLoginProgress();

                final String name = nameField.getText().toString();
                if(name.equals("")){
                    dismissFirebaseLoginProgress();

                    String infoText = "Please enter a name";
                    registerInfoTV.setText(infoText);
                    return;
                }



                //Check If legit email address
                final String email = emailEditText.getText().toString();
                if(!isValidEmail(email)){

                    dismissFirebaseLoginProgress();

                    String infoText = "Please use a valid email";
                    registerInfoTV.setText(infoText);
                    return;
                }

                final String password = passwordEditText.getText().toString();
                final String confirmPassword = confirmPasswordEditText.getText().toString();


                //Check if passwords match
                if(!password.equals(confirmPassword)){

                    dismissFirebaseLoginProgress();

                    String infoText = "Passwords Don't Match";
                    registerInfoTV.setText(infoText);

                    return;
                }



                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {




                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String info = "Account Created Successfully";
                                    registerInfoTV.setText(info);

                                    // Sign in success, update UI with the signed-in user's information

                                    String name = nameField.getText().toString();
                                    String email = emailEditText.getText().toString();

                                    User newUser = new User();
                                    newUser.setName(name);
                                    newUser.setUid(user.getUid());
                                    newUser.setEmail(email);


                                    DataManager.getUsersPath().document(user.getUid()).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Account Creation failed.",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                                dismissFirebaseLoginProgress();
                                                onBackPressed();

                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    dismissFirebaseLoginProgress();
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    String info = task.getException().getLocalizedMessage();
                                    registerInfoTV.setText(info);

                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }





    private void showFirebaseLoginProgress(){

        registerProgress.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.INVISIBLE);

    }


    private void dismissFirebaseLoginProgress(){

        registerProgress.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.VISIBLE);

    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }








}
