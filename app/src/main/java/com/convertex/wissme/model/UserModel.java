package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("type")
    private String type;

    public String getEnrollment() {
        return enrollment;
    }

    @SerializedName("enrollment")
    private String enrollment;

    public String getClassname() {
        return classname;
    }

    @SerializedName("classname")
    private String classname;

    public String getOrganization() {
        return organization;
    }

    @SerializedName("organization")
    private String organization;

    public String getUsername() {
        return username;
    }

    @SerializedName("username")
    private String username;

    public String getType() {
        return type;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }
}

