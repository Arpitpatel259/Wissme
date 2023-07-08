package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkResponseModel {
    @SerializedName("works")
    private WorkRModel works;

    public WorkRModel getWorks() {
        return works;
    }
}

