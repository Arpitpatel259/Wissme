package com.convertex.wissme.stuTech;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.DashboardActivity;
import com.convertex.wissme.R;
import com.convertex.wissme.model.AssignWorkResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignWork extends AppCompatActivity {

    EditText title_name, title_work, faculty, title_name_class, edit_text;
    Button assign;
    ImageView imageView;
    Calendar myCalendar;
    SharedPreferences preferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.fabitem );

        imageView = findViewById ( R.id.image_menu6 );
        title_name_class = findViewById ( R.id.title_name_class );
        title_name = findViewById ( R.id.title_name );
        title_work = findViewById ( R.id.title_work );
        edit_text = findViewById ( R.id.due_date );
        faculty = findViewById ( R.id.faculty );
        assign = findViewById ( R.id.assign_btn );

        myCalendar = Calendar.getInstance ( );

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        title_name_class.setText ( preferences.getString ( Constants.KEY_CLASS, "N/A" ) );
        faculty.setText ( preferences.getString ( Constants.KEY_USERNAME, "N/A" ) );

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set ( Calendar.YEAR, year );
            myCalendar.set ( Calendar.MONTH, monthOfYear );
            myCalendar.set ( Calendar.DAY_OF_MONTH, dayOfMonth );
            updateLabel ( );
        };

        edit_text.setOnClickListener ( v -> new DatePickerDialog ( AssignWork.this, date, myCalendar
                .get ( Calendar.YEAR ), myCalendar.get ( Calendar.MONTH ),
                myCalendar.get ( Calendar.DAY_OF_MONTH ) ).show ( ) );

        SharedPreferences preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        assign.setOnClickListener ( v -> {
            if (title_name_class.length ( ) == 0) {
                title_name_class.setError ( "Please Enter Class Name" );
            } else if (title_name.length ( ) == 0) {
                title_name.setError ( "Please Enter Title Of the Work" );
            } else if (title_work.length ( ) == 0) {
                title_work.setError ( "Please Enter Work Name" );
            } else if (faculty.length ( ) == 0) {
                faculty.setError ( "Please Enter Assigner Name" );
            } else {
                HashMap<String, String> params = new HashMap<> ( );
                params.put ( "classname", title_name_class.getText ( ).toString ( ) );
                params.put ( "work_title", title_name.getText ( ).toString ( ) );
                params.put ( "work_name", title_work.getText ( ).toString ( ) );
                params.put ( "end_time", edit_text.getText ( ).toString ( ) );
                params.put ( "faculty", faculty.getText ( ).toString ( ) );
                params.put ( "organization", preferences.getString ( Constants.KEY_ORG, "N/A" ) );
                Assign ( params );
            }
        } );

        imageView.setOnClickListener ( v -> {
            startActivity ( new Intent ( getApplicationContext ( ), DashboardActivity.class ) );
            finish ( );
        } );
    }

    @Override
    public void onBackPressed() {
        startActivity ( new Intent ( getApplicationContext ( ), DashboardActivity.class ) );
        super.onBackPressed ( );
    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat ( myFormat, Locale.US );

        edit_text.setText ( sdf.format ( myCalendar.getTime ( ) ) );
    }

    private void Assign(HashMap<String, String> params) {
        ProgressDialog progressDialog = new ProgressDialog ( AssignWork.this );
        progressDialog.setTitle ( "Please wait..." );
        progressDialog.setMessage ( "Work Assigning..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<AssignWorkResponseModel> assign_work = networkService.assignwork ( params );
        assign_work.enqueue ( new Callback<AssignWorkResponseModel> ( ) {
            @Override
            public void onResponse(@NonNull Call<AssignWorkResponseModel> call, @NonNull Response<AssignWorkResponseModel> response) {
                AssignWorkResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {
                        Toast.makeText ( AssignWork.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        startActivity ( new Intent ( getApplicationContext ( ), DashboardActivity.class ) );
                        finish ( );
                    } else {
                        Toast.makeText ( AssignWork.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                }
                progressDialog.dismiss ( );
            }

            @Override
            public void onFailure(@NonNull Call<AssignWorkResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss ( );
            }
        } );
    }
}
