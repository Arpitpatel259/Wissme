package com.convertex.wissme.defaultActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.R;
import com.convertex.wissme.model.GetImageModel;
import com.convertex.wissme.model.ImageModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends AppCompatActivity {

    Button app_info;
    ImageView imageView1, image_profile;
    TextView name, email, contact, org_setting, enroll_setting, edit_photo;
    SharedPreferences preferences;
    Bitmap bitmap;
    private final int IMG_REQUEST = 21;

    @SuppressLint({"SetTextI18n", "IntentReset"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setting );
        GetImage ( );

        imageView1 = findViewById ( R.id.image_menu2 );
        image_profile = findViewById ( R.id.image_profile );
        app_info = findViewById ( R.id.app_info );
        name = findViewById ( R.id.name );
        email = findViewById ( R.id.email_id );
        contact = findViewById ( R.id.contact );
        org_setting = findViewById ( R.id.orgs_name );
        enroll_setting = findViewById ( R.id.enrollss );
        edit_photo = findViewById ( R.id.edit_photo );

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        email.setText ( "Email : " + preferences.getString ( Constants.Email, "N/A" ) );
        name.setText ( preferences.getString ( Constants.KEY_USERNAME, "N/A" ) );
        contact.setText ( preferences.getString ( Constants.KEY_CONTACT, "N/A" ) );
        enroll_setting.setText ( preferences.getString ( Constants.KEY_ENROLL, "N/A" ) );
        org_setting.setText ( preferences.getString ( Constants.KEY_ORG, "N/A" ) );

        imageView1.setOnClickListener ( v -> finish ( ) );

        app_info.setOnClickListener ( v -> showAppInfo ( ) );

        edit_photo.setOnClickListener ( view -> {
            @SuppressLint("IntentReset") Intent intent = new Intent ( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            intent.setType ( "image/*" );
            intent.setAction ( Intent.ACTION_GET_CONTENT );
            startActivityForResult ( intent, IMG_REQUEST );
        } );
    }

    private void uploadImage() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ( );
        bitmap.compress ( Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream );
        byte[] imageInByte = byteArrayOutputStream.toByteArray ( );

        String encodedImage = Base64.encodeToString ( imageInByte, Base64.DEFAULT );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<ImageModel> call = networkService.uploadImage ( encodedImage, preferences.getString ( Constants.Email, "N/A" ),
                preferences.getString ( Constants.KEY_U_NAME, "N/A" ) );
        call.enqueue ( new Callback<ImageModel> ( ) {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                Toast.makeText ( Setting.this, response.body ( ).getRemarks ( ) + "\n" + " Please Restart Application",
                        Toast.LENGTH_SHORT ).show ( );
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Toast.makeText ( Setting.this, "Something Went Wrong", Toast.LENGTH_SHORT ).show ( );
            }
        } );
    }

    private void GetImage() {
        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<GetImageModel> call = networkService.getImage ( preferences.getString ( Constants.Email, "N/A" ) );
        call.enqueue ( new Callback<GetImageModel> ( ) {
            @Override
            public void onResponse(Call<GetImageModel> call, Response<GetImageModel> response) {
                String imageUri = Constants.BASE_URL + response.body ( ).getUrl ( );
                image_profile = findViewById ( R.id.image_profile );
                Picasso.with ( getApplicationContext ( ) ).load ( imageUri ).into ( image_profile );
            }

            @Override
            public void onFailure(Call<GetImageModel> call, Throwable t) {
            }
        } );
    }

    private void showAppInfo() {
        try {
            Intent intent = new Intent ( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
            intent.setData ( Uri.parse ( "package:" + getApplicationContext ( ).getPackageName ( ) ) );
            startActivity ( intent );
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent ( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
            startActivity ( intent );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        Uri path = null;
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            path = data.getData ( );
            try {
                bitmap = MediaStore.Images.Media.getBitmap ( getContentResolver ( ), path );
                image_profile.setImageBitmap ( bitmap );
            } catch (IOException e) {
                e.printStackTrace ( );
            }
        }
        if (path != null) {
            uploadImage ( );
        }
    }
}