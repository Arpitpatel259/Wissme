package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class CompleteWorkModel {

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("enroll")
    private String enrollment;

    @SerializedName("filename")
    private String filename;

    @SerializedName("submit_date")
    private String submit_date;

    @SerializedName("work_id")
    private String work_id;

    @SerializedName("work_title")
    private String work_title;

    @SerializedName("work_sub")
    private String work_sub;

    public String getName() {
        return name;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public String getWork_id() {
        return work_id;
    }

    public String getWork_title() {
        return work_title;
    }

    public String getWork_sub() {
        return work_sub;
    }

}
