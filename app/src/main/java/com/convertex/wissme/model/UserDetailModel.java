package com.convertex.wissme.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserDetailModel {

    @SerializedName("user_details")
    private List<UserModel> userDetail;

    public List<UserModel> getUserDetail() {
        return userDetail;
    }
}
