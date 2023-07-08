package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponseModel {

    @SerializedName("success")
    private String success;

    @SerializedName("user_details")
    private UserDetailModel userDetailObject;

    @SerializedName("message")
    private String message;

    public UserDetailModel getUserDetailObject() {
        return userDetailObject;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }


}

