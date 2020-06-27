package me.divij.instafam.authenticator;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class FireAuthenticator implements Authenticator {
    private static FireAuthenticator instance;

    public synchronized static FireAuthenticator getInstance() {
        if (instance == null) {
            instance = new FireAuthenticator();
        }
        return instance;
    }

    private FirebaseAuth auth;
    private FirebaseFirestore store;

    private FireAuthenticator() {
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void register(String email, String password, Consumer<AuthResult> onComplete) {
        isRegistered(email, isRegistered -> {
            switch (isRegistered) {
                case ERROR:
                case EXISTS:
                    return;
                default:
                    break;

            }
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(res -> {
                if (res.isSuccessful()) {
                    onComplete.accept(res.getResult());
                } else {
                    onComplete.accept(null);
                }
            });
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void isRegistered(String email, Consumer<RegistrationResult> onComplete) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener( res -> onComplete.accept((res.getResult().getSignInMethods().size() == 0) ? RegistrationResult.SUCCESSFULL:RegistrationResult.EXISTS));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean authenticate(String email, String password, Consumer<AuthResult> result) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener( res -> {
           if (res.isSuccessful()) {
               result.accept(res.getResult());
           } else {
               result.accept(null);
           }
        });
        return true;
    }
}
