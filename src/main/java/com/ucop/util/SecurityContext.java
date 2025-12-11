package com.ucop.util;

import com.ucop.entity.User;

public class SecurityContext {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String getCurrentUser() {
        return currentUser != null ? currentUser.getUsername() : "SYSTEM";
    }

    public static Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId().longValue() : null;
    }
}
