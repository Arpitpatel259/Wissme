package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;

public class WorkModel {


    @SerializedName("id")
    private String id;

    @SerializedName("classname")
    private String classname;

    @SerializedName("work_title")
    private String work_title;

    @SerializedName("work_name")
    private String work_name;

    @SerializedName("start_time")
    private String start_time;

    @SerializedName("end_time")
    private String end_time;

    @SerializedName("faculty")
    private String faculty;

    public String getId() {
        return id;
    }

    public String getClassname() {
        return classname;
    }

    public String getWork_title() {
        return work_title;
    }

    public String getWork_name() {
        return work_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setWork_title(String work_title) {
        this.work_title = work_title;
    }

    public void setWork_name(String work_name) {
        this.work_name = work_name;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
