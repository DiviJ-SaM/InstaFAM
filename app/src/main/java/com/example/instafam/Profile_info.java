package com.example.instafam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Profile_info extends AppCompatActivity {

    private ImageView dp;
    private TextView name, email, weloome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        dp = (ImageView) findViewById(R.id.dp);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.personEmail);
        weloome = findViewById(R.id.textView6);

        Intent i = getIntent();
        final String i_name, i_email, i_gender, i_url;
        i_name = i.getStringExtra("p_name");
        i_email = i.getStringExtra("p_email");

        i_url = i.getStringExtra("p_url");

        name.setText(i_name);
        email.setText(i_email);
        weloome.setText("WELCOME TO YOUR PROFILE, "+i_name);


        Glide.with(this).load(i_url).into(dp);

    }


}




