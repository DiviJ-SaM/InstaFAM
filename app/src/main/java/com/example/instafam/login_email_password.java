package com.example.instafam;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import me.divij.instafam.UserManager;
import me.divij.instafam.authenticator.Authenticator;
import me.divij.instafam.authenticator.FireAuthenticator;
import me.divij.instafam.utils.InputVerifier;

public class login_email_password extends AppCompatActivity {

    EditText txtEmail, txtpassword, txtconfPassword, username;
    ProgressBar progressBar;
    Button RegnButton;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;

    private FireAuthenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticator = FireAuthenticator.getInstance();

        setContentView(R.layout.activity_login_email_password);
        txtEmail = (EditText) findViewById(R.id.editText2);
        txtpassword = (EditText) findViewById(R.id.editText4);
        txtconfPassword = (EditText) findViewById(R.id.editTex5);
        username = findViewById(R.id.username);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        RegnButton = (Button) findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance();

        RegnButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString();
                final String password = txtpassword.getText().toString().trim();
                final String confpassword = txtconfPassword.getText().toString().trim();

                if (!InputVerifier.isValidEmail(email)) {
                    Toast.makeText(login_email_password.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!InputVerifier.isValidPassword(password) || !InputVerifier.isValidPassword(confpassword)) {
                    Toast.makeText(login_email_password.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                authenticator.isRegistered(email, res -> {
                    if (res == Authenticator.RegistrationResult.EXISTS) {
                        Toast.makeText(login_email_password.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                    }
                });

                progressBar.setVisibility(View.VISIBLE);
                if (password.equals(confpassword)) {
                    authenticator.register(email, password, authResult -> {
                        progressBar.setVisibility(View.GONE);

                        UserManager manager = UserManager.getInstance();
                        manager.insertUserData(email, username.getText().toString(), password, ref -> {
                            Toast.makeText(login_email_password.this, "User profile is created for " + email, Toast.LENGTH_SHORT).show();

                        });
                        try {
                            Intent transferData = new Intent(login_email_password.this,Homescreen_UI.class);
                            String pname, pemailId;
                            pname = username.getText().toString();
                            pemailId = txtEmail.getText().toString();


                            transferData.putExtra("person_name", pname);
                            transferData.putExtra("person_email", pemailId);


                            startActivity(transferData);
                            open_userprofile();


                        } catch (Exception f) {
                            Toast.makeText(login_email_password.this, f.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    });
                } else {
                    Toast.makeText(login_email_password.this, "Passwords dont match!", Toast.LENGTH_SHORT).show();

                }
            }


        });
    }
    private void open_userprofile(){
        startActivity(new Intent(login_email_password.this,Profil_login.class));
    }

}


