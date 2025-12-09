package com.ucop.util;

public class SecurityContext {

    private static String currentUser = "SYSTEM";

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}
