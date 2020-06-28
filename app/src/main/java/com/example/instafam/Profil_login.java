package com.example.instafam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Optional;

public class Profil_login extends AppCompatActivity {

    private Button button;
    private TextView post;
    private TextView email, username;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_login);
        button = findViewById(R.id.searchbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profil_login.this,search_users.class));
            }
        });
        post = findViewById(R.id.postTextView);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profil_login.this, Homescreen_UI.class));
            }
        });
        email = findViewById(R.id.Email_profile);
        username = findViewById(R.id.Username_profile);

        FirebaseAuth auth =FirebaseAuth.getInstance();
        FirebaseUser currUser = auth.getCurrentUser();
        String emailID = currUser.getEmail();
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(listener -> {
            Optional<DocumentSnapshot> snapOp = listener.getResult().getDocuments().stream().filter(a -> String.valueOf(a.get("email")).equalsIgnoreCase(emailID)).findFirst();
            if (!snapOp.isPresent()) {
                Toast.makeText(this, "Error loading your data!", Toast.LENGTH_SHORT).show();
                return;
            }
            DocumentSnapshot snap = snapOp.get();
            String uName = String.valueOf(snap.get("username"));
            username.setText(uName);
            email.setText(emailID);
        });
    }
}
