package com.example.instafam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.HashMap;

import javax.annotation.Nullable;

public class Homescreen_UI extends AppCompatActivity {

Button postbtn;
    TextView username, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen__ui);

username = findViewById(R.id.textView7);
email = findViewById(R.id.emailafterlogin);
postbtn = findViewById(R.id.post_button);
postbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        post();
    }
});

    }


    private void transferAndShowData() {
        username = (TextView) findViewById(R.id.textView7);
        email = (TextView) findViewById(R.id.emailafterlogin);

        Intent i = getIntent();
        final String i_name, i_email;
        i_name = i.getStringExtra("person_name");
        i_email = i.getStringExtra("person_email");


        username.setText("Welcome " + i_name);
        email.setText(i_email);
    }
    private void post(){
        startActivity(new Intent(Homescreen_UI.this, post_photo.class));
    }


}
