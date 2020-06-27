package me.divij.instafam;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UserManager {
    private static UserManager instance;
    public static UserManager getInstance() {
        if (instance == null) instance = new UserManager();
        return instance;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insertUserData(String email, String user, String password, Consumer<DocumentReference> onSuccess) {
        FirebaseAuth authInstance = FirebaseAuth.getInstance();
        FirebaseUser curUser = authInstance.getCurrentUser();

        if (curUser == null) return;

        CollectionReference reference = FirebaseFirestore.getInstance().collection("users");
        Map<String,String> data = new HashMap<>();
        data.put("email", email);
        data.put("username", user);
        data.put("password", password);
        reference.add(data).addOnSuccessListener( a -> {
            onSuccess.accept(a);
        });
    }




}
