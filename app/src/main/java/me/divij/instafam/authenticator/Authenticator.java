package me.divij.instafam.authenticator;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.function.Consumer;

public interface Authenticator {
    void register(String email, String password, Consumer<AuthResult> onComplete);
    void isRegistered(String email, Consumer<RegistrationResult> onComplete);
    boolean authenticate(String email, String password, Consumer<AuthResult> result);
    static enum RegistrationResult {
        EXISTS,
        SUCCESSFULL,
        ERROR
    }
}
