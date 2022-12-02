package com.example.welink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class LoginActivity extends AppCompatActivity {

    EditText emailET,passwordET;
    Button signup_btn, login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    TextView forgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotpass = findViewById(R.id.tv_forgot_pass);
        emailET = findViewById(R.id.login_email_ET);
        passwordET = findViewById(R.id.login_password_ET);
        login_btn = findViewById(R.id.login_BTN);
        signup_btn = findViewById(R.id.login_to_signup_BTN);
        checkBox = findViewById(R.id.login_checkbox);
        progressBar = findViewById(R.id.progressbar_login);
        mAuth = FirebaseAuth.getInstance();


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String pass = passwordET.getText().toString();

                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) )
                {
                    Toast.makeText(LoginActivity.this, "helooooooo", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "GGEZ", Toast.LENGTH_SHORT).show();
                                sendtoMain();
                            }
                            else
                            {
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error :"+error, Toast.LENGTH_SHORT);
                            }
                        }
                    });
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailET.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Reset Password")
                        .setMessage("Are you sure to reset password")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(LoginActivity.this, "Reset Link sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                                    }
                                });




                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
                builder.show();

            }
        });


    }

    private void sendtoMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if( user != null )
        {
            Intent intent = new Intent(LoginActivity.this, Splashscreen.class);
            startActivity(intent);
            finish();
        }
    }
}