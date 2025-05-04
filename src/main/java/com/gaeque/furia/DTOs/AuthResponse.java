package com.gaeque.furia.DTOs;

public class AuthResponse {
    private boolean isAuth;
    private Long id;
    private String email;
    private String token;
    private String userName;

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AuthResponse() {
    }

    public AuthResponse(boolean isAuth, Long id, String email, String token, String userName) {
        this.isAuth = isAuth;
        this.id = id;
        this.email = email;
        this.token = token;
        this.userName = userName;
    }

    public boolean isAuth() {
        return this.isAuth;
    }

    public void setAuth(boolean auth) {
        this.isAuth = auth;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
