package me.divij.instafam.utils;

public class InputVerifier {
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches(".{1,}@.+\\..+");
    }
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches(".{6,}");
    }

}
