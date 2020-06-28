package com.example.instafam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import me.divij.instafam.posts.PostUtils;

public class search_users extends AppCompatActivity {
    private Button searchBtn;
    private EditText searchField;
    private TextView userView;
    private TextView emailView;
    private TableLayout table;
    private TextView userdp;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        searchBtn = findViewById(R.id.searchButton);
        searchField = findViewById(R.id.searchField);
        userView = findViewById(R.id.userView);
        emailView = findViewById(R.id.emailView);
        table = findViewById(R.id.tableLayout);
        userdp = findViewById(R.id.User_photo);
        searchBtn.setOnClickListener( event -> {
            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
            String requestedUser = searchField.getText().toString();
            if (requestedUser == null || requestedUser.matches("\\s*")) {
                Toast.makeText(this, "Invalid search!", Toast.LENGTH_SHORT).show();
                return;
            }
            CollectionReference colRef = fireStore.collection("users");
            colRef.get().addOnCompleteListener(listener -> {
                List<DocumentSnapshot> userRef = listener.getResult().getDocuments();
                Optional<DocumentSnapshot> snapOp = userRef.stream().filter(a -> a.get("username").equals(requestedUser)).findFirst();
                if (!snapOp.isPresent()) {
                    Toast.makeText(this, "User not found! Try again!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentSnapshot snap = snapOp.get();

                String username = String.valueOf(snap.get("username"));
                String email = String.valueOf(snap.get("email"));

                userView.setText(username);
                emailView.setText(email);
                userdp.setText(username);

                PostUtils.INSTANCE.getPostsByUser(this, email, res -> {
                    table.removeAllViews();
                    res.forEach( post -> {
                        ImageView postView = new ImageView(this);
                        Glide.with(this)
                                .load(post.getPostURL())
                                .into(postView);
                        TableLayout.LayoutParams params = new TableLayout.LayoutParams(220 , 220);
                        postView.setLayoutParams(params);
                        table.addView(postView);
                    });
                });

            });
        });
    }


}
