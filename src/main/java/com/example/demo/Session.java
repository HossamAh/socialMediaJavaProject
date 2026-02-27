package com.example.demo;

import com.example.demo.model.User;


public class Session {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
//    public static String getEncryptedPassword(String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt(12));
//    }
//    public static boolean verifyPassword(String inputPassword, String storedHash) {
//        return BCrypt.checkpw(inputPassword, storedHash);
//    }

}
