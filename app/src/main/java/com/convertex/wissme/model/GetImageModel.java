package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class GetImageModel {

    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    @SerializedName("url")
    private String url;

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
