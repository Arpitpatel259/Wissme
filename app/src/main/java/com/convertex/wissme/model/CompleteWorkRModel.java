package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompleteWorkRModel {
    @SerializedName("works")
    private List<CompleteWorkModel> works;

    public List<CompleteWorkModel> getComWork() {
        return works;
    }
}
