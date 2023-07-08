package com.convertex.wissme.network;

import com.convertex.wissme.model.AssignWorkResponseModel;
import com.convertex.wissme.model.CompleteWorkResponseModel;
import com.convertex.wissme.model.GetImageModel;
import com.convertex.wissme.model.ImageModel;
import com.convertex.wissme.model.LoginResponseModel;
import com.convertex.wissme.model.OrganizationResponseModel;
import com.convertex.wissme.model.PasswordResponseModel;
import com.convertex.wissme.model.RegistrationResponseModel;
import com.convertex.wissme.model.WorkResponseModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetworkService {

    @FormUrlEncoded
    @POST("Registration.php")
    Call<RegistrationResponseModel> register(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("Login.php")
    Call<LoginResponseModel> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("ResetPassword.php")
    Call<PasswordResponseModel> resetPass(@Field("email") String email, @Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("AssignWork.php")
    Call<AssignWorkResponseModel> assignwork(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("CreateOrganization.php")
    Call<OrganizationResponseModel> CreateOrganization(@Field("organization") String organization);

    @FormUrlEncoded
    @POST("getWorksFromDB.php")
    Call<WorkResponseModel> getworksByEmail(@Field("email") String email, @Field("organization") String organization);

    @FormUrlEncoded
    @POST("EditWork.php")
    Call<AssignWorkResponseModel> editworkbyid(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("getWorkById.php")
    Call<WorkResponseModel> getworksById(@Field("email") String email, @Field("organization") String organization, @Field("id") String id);

    @FormUrlEncoded
    @POST("com_getwork.php")
    Call<CompleteWorkResponseModel> getComWorkByEmail(@Field("email") String email, @Field("organization") String organization);

    @FormUrlEncoded
    @POST("DeleteWork.php")
    Call<AssignWorkResponseModel> deleteWork(@Field("organization") String organization, @Field("id") String id);

    @FormUrlEncoded
    @POST("getComWorkForDepart.php")
    Call<CompleteWorkResponseModel> getComWorkForDepart(@Field("email") String email, @Field("organization") String organization, @Field("id") String id);

    @FormUrlEncoded
    @POST("upload_document.php")
    Call<OrganizationResponseModel> upload(@Field("PDF") String encodedPDF, @Field("myPDF") String filename, @Field("email") String email, @Field("organization") String organization, @Field("date") String date, @Field("wid") String wid, @Field("wtitle") String wtitle, @Field("wsub") String wsub);

    @FormUrlEncoded
    @POST("Image_Upload.php")
    Call<ImageModel> uploadImage(@Field("EN_IMAGE") String encodedImage, @Field("email") String email, @Field("username") String username);

    @FormUrlEncoded
    @POST("Image_get.php")
    Call<GetImageModel> getImage(@Field("email") String email);

    @FormUrlEncoded
    @POST("get_Student.php")
    Call<LoginResponseModel> getParticipate(@Field("organization") String organization,@Field("type") String type);
}

