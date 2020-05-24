package com.example.mobiledevlabs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class User {
    @SerializedName("Username")
    @Expose
    private String Username;

    @SerializedName("Password")
    @Expose
    private String Password;

    User (String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
