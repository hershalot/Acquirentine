package com.fenapnu.acquirentine_android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {


    private String TAG = "Forgot Password";
    private Button resetLinkButton;
    private EditText emailEditText;
    private TextView confirmationText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);


        emailEditText = findViewById(R.id.email_reset_tv);
        resetLinkButton = findViewById(R.id.email_reset_btn);
        ImageButton backBtn = findViewById(R.id.close_forgot_password_btn);
        confirmationText = findViewById(R.id.confirmation_text_view);

        confirmationText.setHintTextColor(getColor(R.color.placeholderColor));
        emailEditText.setHintTextColor(getColor(R.color.placeholderColor));




        resetLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailEditText.getText().toString();
                if(isValidEmail(email)){

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        resetLinkButton.setVisibility(View.GONE);
                                        emailEditText.setVisibility(View.GONE);

                                        String text = "Password reset email sent";
                                        confirmationText.setText(text);
                                        confirmationText.setVisibility(View.VISIBLE);

                                        onBackPressed();
                                    }
                                }
                            });

                }

            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
