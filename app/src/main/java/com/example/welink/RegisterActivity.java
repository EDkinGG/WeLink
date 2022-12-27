package com.example.welink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText emailET,passwordET,confirmET;
    Button reg_btn, login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = findViewById(R.id.register_email_ET);
        passwordET = findViewById(R.id.register_password_ET);
        confirmET = findViewById(R.id.register_confirm_password_ET);
        reg_btn = findViewById(R.id.register_BTN);
        login_btn = findViewById(R.id.register_to_login_BTN);
//        checkBox = findViewById(R.id.register_checkbox);
        progressBar = findViewById(R.id.progressbar_register);
        mAuth = FirebaseAuth.getInstance();

//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b)
//                {
//                    passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    confirmET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }
//                else
//                {
//                    passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    confirmET.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//            }
//        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String pass = passwordET.getText().toString();
                String confirm_pass = confirmET.getText().toString();

                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass))
                {
                    if( pass.equals(confirm_pass))
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    sendtoMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error :"+error, Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Password and Confirm Password not matching", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendtoMain() {
        Intent intent = new Intent(RegisterActivity.this, Splashscreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user != null )
        {
            sendtoMain();
        }
    }
}