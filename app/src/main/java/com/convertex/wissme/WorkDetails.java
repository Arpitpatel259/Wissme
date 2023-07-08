package com.convertex.wissme;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.model.OrganizationResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkDetails extends AppCompatActivity {

    ImageView imageView;
    TextView textClass, textTitle, text_due, textWork, textFac;
    EditText filename;
    Button submit_work, upload_file;
    private final int REQ_PDF = 21;
    private String encodedPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_workdetails );

        upload_file = findViewById ( R.id.upload_file );
        submit_work = findViewById ( R.id.submit_work );
        filename = findViewById ( R.id.filename );
        imageView = findViewById ( R.id.image );
        imageView.setOnClickListener ( v -> finish ( ) );

        textClass = findViewById ( R.id.Class_name );
        text_due = findViewById ( R.id.Time_work );
        textFac = findViewById ( R.id.faculty_name );
        textTitle = findViewById ( R.id.Title_name );
        textWork = findViewById ( R.id.Work_name );

        textWork.setText ( getIntent ( ).getStringExtra ( "work_name" ) );
        textTitle.setText ( getIntent ( ).getStringExtra ( "work_title" ) );
        textFac.setText ( getIntent ( ).getStringExtra ( "faculty" ) );
        text_due.setText ( getIntent ( ).getStringExtra ( "end_time" ) );
        textClass.setText ( getIntent ( ).getStringExtra ( "classname" ) );

        submit_work.setOnClickListener ( view -> upload ( ) );

        upload_file.setOnClickListener ( view -> {
            Intent chooseFile = new Intent ( Intent.ACTION_GET_CONTENT );
            chooseFile.setType ( "application/pdf" );
            chooseFile = Intent.createChooser ( chooseFile, "Choose a file" );
            startActivityForResult ( chooseFile, REQ_PDF );
        } );
    }

    private void upload() {
        SharedPreferences preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<OrganizationResponseModel> up = networkService.upload ( encodedPDF
                , filename.getText ( ).toString ( )
                , preferences.getString ( Constants.Email, "N/A" ),
                preferences.getString ( Constants.KEY_ORG, "N/A" ),
                new SimpleDateFormat ( "dd-MM-yyyy", Locale.getDefault ( ) ).format ( new Date ( ) ) + " "
                        + new SimpleDateFormat ( "HH:mm:ss", Locale.getDefault ( ) ).format ( new Date ( ) )
                , getIntent ( ).getStringExtra ( "id" )
                , getIntent ( ).getStringExtra ( "work_title" )
                , getIntent ( ).getStringExtra ( "classname" )
        );
        up.enqueue ( new Callback<OrganizationResponseModel> ( ) {
            @Override
            public void onResponse(Call<OrganizationResponseModel> call, Response<OrganizationResponseModel> response) {
                Toast.makeText ( WorkDetails.this, response.body ( ).getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                startActivity ( new Intent ( getApplicationContext ( ), Com_work.class ) );
                Toast.makeText ( getApplicationContext ( ), "Please Wait For File Uploading", Toast.LENGTH_SHORT ).show ( );
                finish ( );
            }

            @Override
            public void onFailure(Call<OrganizationResponseModel> call, Throwable t) {
                Toast.makeText ( WorkDetails.this, "Network Failed" + t.toString ( ), Toast.LENGTH_SHORT ).show ( );
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == REQ_PDF && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData ( );

            try {
                InputStream inputStream = WorkDetails.this.getContentResolver ( ).openInputStream ( path );
                byte[] bytes = new byte[inputStream.available ( )];
                inputStream.read ( bytes );
                encodedPDF = Base64.encodeToString ( bytes, Base64.DEFAULT );
                Toast.makeText ( this, "Document Selected.", Toast.LENGTH_SHORT ).show ( );
            } catch (IOException e) {
                e.printStackTrace ( );
            }
        }
    }
}