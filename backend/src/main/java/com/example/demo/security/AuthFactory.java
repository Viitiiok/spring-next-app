package com.example.demo.security;

public class AuthFactory {

    public static Authenticator getAuthenticator(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AdminAuthenticator();
        } else {
            return new UserAuthenticator();
        }
    }

    public interface Authenticator {
        boolean authenticate(String username, String password, String... extra);
    }

    // Admin authentication requires a second password
    public static class AdminAuthenticator implements Authenticator {
        @Override
        public boolean authenticate(String username, String password, String... extra) {
            if (extra.length < 1) return false;
            String secondPassword = extra[0];
            // Replace with real admin authentication logic
            return "admin".equals(username) && "adminPass".equals(password) && "secondPass".equals(secondPassword);
        }
    }

    // Regular user authentication
    public static class UserAuthenticator implements Authenticator {
        @Override
        public boolean authenticate(String username, String password, String... extra) {
            // Replace with real user authentication logic
            return "user".equals(username) && "userPass".equals(password);
        }
    }
}