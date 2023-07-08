package com.convertex.wissme;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class EditWork extends AppCompatActivity {

    Button edit_assign_btn;
    EditText edit_class, edit_name, edit_work, edit_date, edit_faculty;
    Calendar myCalendar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_edit_work );

        edit_class = findViewById ( R.id.edit_class );
        edit_name = findViewById ( R.id.edit_name );
        edit_work = findViewById ( R.id.edit_work );
        edit_date = findViewById ( R.id.edit_date );
        edit_faculty = findViewById ( R.id.edit_faculty );
        edit_assign_btn = findViewById ( R.id.edit_assign_btn );

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        edit_class.setText ( getIntent ( ).getStringExtra ( "classname" ) );
        edit_name.setText ( getIntent ( ).getStringExtra ( "work_title" ) );
        edit_work.setText ( getIntent ( ).getStringExtra ( "work_name" ) );
        edit_date.setText ( getIntent ( ).getStringExtra ( "end_time" ) );
        edit_faculty.setText ( getIntent ( ).getStringExtra ( "faculty" ) );

        findViewById ( R.id.back ).setOnClickListener ( view -> startActivity ( new Intent ( getApplicationContext ( ), DashboardActivity.class ) ) );

        myCalendar = Calendar.getInstance ( );


        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set ( Calendar.YEAR, year );
            myCalendar.set ( Calendar.MONTH, monthOfYear );
            myCalendar.set ( Calendar.DAY_OF_MONTH, dayOfMonth );
            updateLabel ( );
        };

        edit_date.setOnClickListener ( v -> new DatePickerDialog ( EditWork.this, date, myCalendar
                .get ( Calendar.YEAR ), myCalendar.get ( Calendar.MONTH ),
                myCalendar.get ( Calendar.DAY_OF_MONTH ) ).show ( ) );

        SharedPreferences preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        edit_assign_btn.setOnClickListener ( v -> {
            if (edit_name.length ( ) == 0) {
                edit_name.setError ( "Please Enter Title Of the Work" );
            } else if (edit_work.length ( ) == 0) {
                edit_work.setError ( "Please Enter Work Name" );
            } else if (edit_date.length ( ) == 0) {
                edit_date.setError ( "Please Enter Due Date" );
            } else {
                HashMap<String, String> params = new HashMap<> ( );
                params.put ( "work_title", edit_name.getText ( ).toString ( ) );
                params.put ( "work_name", edit_work.getText ( ).toString ( ) );
                params.put ( "end_time", edit_date.getText ( ).toString ( ) );
                params.put ( "organization", preferences.getString ( Constants.KEY_ORG, "N/A" ) );
                params.put ( "id", getIntent ( ).getStringExtra ( "id" ) );
                edit_Work ( params );
            }
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

        edit_date.setText ( sdf.format ( myCalendar.getTime ( ) ) );
    }

    private void edit_Work(HashMap<String, String> params) {
        ProgressDialog progressDialog = new ProgressDialog ( EditWork.this );
        progressDialog.setTitle ( "Please wait..." );
        progressDialog.setMessage ( "Work Editing..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<AssignWorkResponseModel> edit_works = networkService.editworkbyid ( params );
        edit_works.enqueue ( new Callback<AssignWorkResponseModel> ( ) {
            @Override
            public void onResponse(@NonNull Call<AssignWorkResponseModel> call, @NonNull Response<AssignWorkResponseModel> response) {
                AssignWorkResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {
                        Toast.makeText ( EditWork.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        startActivity ( new Intent ( getApplicationContext ( ), DashboardActivity.class ) );
                        finish ( );
                    } else {
                        Toast.makeText ( EditWork.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
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