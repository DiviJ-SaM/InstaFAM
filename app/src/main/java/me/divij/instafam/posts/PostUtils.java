package me.divij.instafam.posts;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public enum PostUtils {
    INSTANCE;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getPostsByUser(Context ctx, String email, Consumer<Set<PostData>> resultOperations) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference photosRef = firestore.collection("photos");
        photosRef.get().addOnCompleteListener(listener -> {
           List<DocumentSnapshot> result =listener.getResult().getDocuments();

           Optional<DocumentSnapshot> snapOp = result.stream().filter(a -> (String.valueOf(a.get("email"))).equalsIgnoreCase(email)).findFirst();
           if (!snapOp.isPresent()) {
               Toast.makeText(ctx, "Unable to fetch posts by user!", Toast.LENGTH_SHORT).show();
               return;
           }
           Set<PostData> postDataSet = new HashSet<>();
           DocumentSnapshot snap = snapOp.get();
           Gson parser = new Gson();
           Type token = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
           Map<String, Map<String, String>> postMap = parser.fromJson(parser.toJson(snap.get("posts")), token);
           postMap.forEach( (key, post) -> {
               String url = post.get("photo_url");
               String desc = post.get("description");
               PostData curData = new PostData(email, key, desc, url);
               postDataSet.add(curData);
           });
           resultOperations.accept(postDataSet);
        });
    }

}
