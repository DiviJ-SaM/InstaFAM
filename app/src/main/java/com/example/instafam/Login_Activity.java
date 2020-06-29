package com.example.instafam;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import java.util.Objects;

import me.divij.instafam.authenticator.FireAuthenticator;

public class Login_Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 10;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private Button btnSignOut;
    private Button ProfButton;
    private Button emailAuthbtn;
    private Button loginBtn;
    private TextView or;
    private EditText emailField;
    private EditText passField;
    private ImageView logo;

    private GoogleSignInAccount account;

    private ImageView DisplayPic;
    private FireAuthenticator authenticator = FireAuthenticator.getInstance();

    public GoogleSignInAccount getAccount() {
        return account;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        signInButton = findViewById(R.id.sign_in_button);
        btnSignOut = findViewById(R.id.sign_out_button);
        ProfButton = findViewById(R.id.Profilebutton);
        emailAuthbtn = findViewById(R.id.emailAuthBtn);
        logo = findViewById(R.id.Instafam_image);
        or = findViewById(R.id.textView5);
        DisplayPic = findViewById(R.id.DisplayPic);
        loginBtn = findViewById(R.id.loginBtn);
        emailField = findViewById(R.id.emailField);
        passField = findViewById(R.id.passField);
        mAuth = FirebaseAuth.getInstance();
        btnSignOut.setVisibility(View.INVISIBLE);
        ProfButton.setVisibility(View.INVISIBLE);
        ProfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
        emailAuthbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginWithEmailPassword();
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                emailField.setText("");
                emailField.setVisibility(View.INVISIBLE);
                passField.setText("");
                passField.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.INVISIBLE);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                Toast.makeText(Login_Activity.this, "You are Logged Out", Toast.LENGTH_SHORT).show();
                btnSignOut.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.VISIBLE);
                ProfButton.setVisibility(View.INVISIBLE);
                emailAuthbtn.setVisibility(View.VISIBLE);
                emailField.setVisibility(View.VISIBLE);
                passField.setVisibility(View.VISIBLE);
                DisplayPic.setImageURI(Uri.EMPTY);
                logo.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
            }
        });
        loginBtn.setOnClickListener(event -> {
            String email = emailField.getText().toString();
            String pass = passField.getText().toString();
            authenticator.authenticate(email, pass, result -> {
                if (result == null) {
                    Toast.makeText(this, "Check Your Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, Profil_login.class));
                    Toast.makeText(this, String.format("You have logged in as %s!", result.getUser().getEmail()), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        btnSignOut.setVisibility(View.VISIBLE);
        emailAuthbtn.setVisibility(View.INVISIBLE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...

                if (requestCode == REQUEST_CODE_LOGIN) {
                    GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    account = googleSignInResult.getSignInAccount();


                    try {
                        Intent sendData = new Intent(Login_Activity.this, Profile_info.class);
                        String name, email, dpUrl = "";
                        name = account.getDisplayName();
                        email = account.getEmail();


                        dpUrl = account.getPhotoUrl().toString();
                        sendData.putExtra("p_name", name);
                        sendData.putExtra("p_email", email);
                        sendData.putExtra("p_url", dpUrl);

                        startActivity(sendData);

                    } catch (Exception f) {
                        Toast.makeText(Login_Activity.this, f.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            profilePic();
                            btnSignOut.setVisibility(View.VISIBLE);
                            emailField.setVisibility(View.INVISIBLE);
                            passField.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser fUser) {
        btnSignOut.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        ProfButton.setVisibility(View.VISIBLE);
        or.setVisibility(View.INVISIBLE);
        emailAuthbtn.setVisibility(View.INVISIBLE);
        emailField.setVisibility(View.INVISIBLE);
        passField.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(Login_Activity.this, personName + " Successfuly Logged In", Toast.LENGTH_SHORT).show();

        }
    }

    private void openProfile() {
        Intent intent = new Intent(this, Profile_info.class);
        String name = getAccount().getDisplayName();
        String email = getAccount().getEmail();
        String dpUrl = getAccount().getPhotoUrl().toString();

        intent.putExtra("p_name", name);
        intent.putExtra("p_email", email);
        intent.putExtra("p_url", dpUrl);

        startActivity(intent);
    }


    private void profilePic() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser account = mAuth.getCurrentUser();
        if (account != null) {
            //Display User Image from Google Account
            //Objects.requireNonNull() prevents getPhotoUrl() from returning a NullPointerException
            String personImage = Objects.requireNonNull(account.getPhotoUrl()).toString();

            Glide.with(this).load(personImage).into(DisplayPic);

        }
    }

    private void LoginWithEmailPassword() {
        Intent intent = new Intent(this, login_email_password.class);
        startActivity(intent);
    }


}







