package com.example.instafam;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class post_photo extends AppCompatActivity {
    Uri myImageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;


    ImageView close, Image_added;
    TextView post;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);
        close = findViewById(R.id.close);
        Image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        post.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(post_photo.this);


    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if(myImageUri!=null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(myImageUri));
            uploadTask = fileReference.putFile(myImageUri);
            uploadTask.addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(post_photo.this, "Failed!", Toast.LENGTH_SHORT).show();
                    if (task.getException() != null)task.getException().printStackTrace();
                } else {
                    fileReference.getDownloadUrl().addOnCompleteListener(listener -> {
                        myUrl = listener.getResult().toString();

                        CollectionReference photos = FirebaseFirestore.getInstance().collection("photos");

                        UUID postID = UUID.randomUUID();

                        photos.get().addOnCompleteListener(listener1 -> {
                            if (listener.isSuccessful()) {
                                QuerySnapshot resSnap = listener1.getResult();
                                Optional<DocumentSnapshot> doc = resSnap.getDocuments().stream()
                                        .filter(a -> a.get("username").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())).findFirst();

                                if (doc.isPresent()) {
                                    Map<String, Map<String, String>> photoMap = (Map<String, Map<String, String>>) doc.get().get("posts");
                                    Map<String, String> postData = new HashMap<>();
                                    postData.put("photo_url", myUrl);
                                    postData.put("description", description.getText().toString());
                                    photoMap.put(postID.toString(), postData);
                                    doc.get().getId();
                                    photos.document(doc.get().getId()).update("posts", photoMap);
                                } else {
                                    Map<String, Object> dataObj = new HashMap<>();
                                    dataObj.put("username", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    Map<String, Map<String, String>> photoMap = new HashMap<>();
                                    Map<String, String> postData = new HashMap<>();
                                    postData.put("photo_url", myUrl);
                                    postData.put("description", description.getText().toString());
                                    photoMap.put(postID.toString(), postData);
                                    dataObj.put("posts", postData);
                                    photos.add(dataObj);
                                }
                            }
                        });
                    });

                    progressDialog.dismiss();

                    startActivity(new Intent(post_photo.this, Homescreen_UI.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(post_photo.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            myImageUri = result.getUri();

            Image_added.setImageURI(myImageUri);

        }else{
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
