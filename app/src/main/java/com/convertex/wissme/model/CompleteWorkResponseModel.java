package com.convertex.wissme.model;


import com.google.gson.annotations.SerializedName;

public class CompleteWorkResponseModel {
    @SerializedName("success")
    private String success;

    @SerializedName("works")
    private CompleteWorkRModel works;

    public CompleteWorkRModel getComWork() {
        return works;
    }

    public String getSuccess() {
        return success;
    }
}
