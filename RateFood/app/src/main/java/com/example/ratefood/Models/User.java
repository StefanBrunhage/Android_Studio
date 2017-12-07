package com.example.ratefood.Models;

/**
 * Created by rober on 07/12/2017.
 */

public class User {
    private String profile_photo;
    private String email;
    private String user_id;

    public User(String profile_photo, String email, String user_id) {
        this.profile_photo = profile_photo;
        this.email = email;
        this.user_id = user_id;
    }

    public User() {

    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    //alt + insert snabbkommando

    @Override
    public String toString() {
        return "User{" +
                "profile_photo='" + profile_photo + '\'' +
                ", email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
