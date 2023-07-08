package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class AssignWorkResponseModel {

    @SerializedName("success")
    private  String success;

    @SerializedName("message")
    private  String message;

    public AssignWorkResponseModel(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
