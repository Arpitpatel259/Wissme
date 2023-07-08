package com.convertex.wissme.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkRModel {
    @SerializedName("works")
    private List<WorkModel> works;

    public List<WorkModel> getWorks() {
        return works;
    }
}

