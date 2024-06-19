package com.convertex.wissme.defaultActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.R;
import com.convertex.wissme.model.OrganizationResponseModel;
import com.convertex.wissme.model.RegistrationResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    Button show_pass1;
    Button show_pass2;
    EditText password;
    EditText C_pass, organization, classname, first_name, last_name;
    EditText username;
    Spinner type;
    Boolean conn;
    CheckBox org;

    String[] SPINNER_DATA = {"Select Option", "Student", "Departments"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );

        org = findViewById ( R.id.org );
        show_pass1 = findViewById ( R.id.show_pass1 );
        show_pass2 = findViewById ( R.id.show_pass2 );
        TextView sign_in = findViewById ( R.id.sign_in );
        Button register = findViewById ( R.id.register );
        first_name = findViewById ( R.id.Firstname );
        last_name = findViewById ( R.id.Lastname );
        EditText email = findViewById ( R.id.email );
        organization = findViewById ( R.id.organization );
        EditText mobile = findViewById ( R.id.mobile );
        EditText enroll = findViewById ( R.id.Enrollment );
        username = findViewById ( R.id.username );
        type = findViewById ( R.id.type );
        classname = findViewById ( R.id.classname );
        password = findViewById ( R.id.password );
        C_pass = findViewById ( R.id.Repassword );

        type = findViewById ( R.id.type );
        ArrayAdapter<String> adapter = new ArrayAdapter<> ( Register.this, android.R.layout.simple_dropdown_item_1line, SPINNER_DATA );
        type.setAdapter ( adapter );

        type.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    org.setVisibility ( View.VISIBLE );
                    classname.setVisibility ( View.VISIBLE );
                } else {
                    org.setVisibility ( View.GONE );
                    classname.setVisibility ( View.GONE );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );

        register.setOnClickListener ( view -> {
            String Password = password.getText ( ).toString ( );
            String c_pass = C_pass.getText ( ).toString ( );

            if (first_name.length ( ) == 0) {
                first_name.setError ( "Please Enter Firstname" );
            } else if (last_name.length ( ) == 0) {
                last_name.setError ( "Please Enter Lastname" );
            } else if (username.length ( ) == 0) {
                username.setError ( "Please Enter Username" );
            } else if (email.length ( ) == 0) {
                email.setError ( "Please Enter Email Address" );
            } else if (!Patterns.EMAIL_ADDRESS.matcher ( email.getText ( ).toString ( ) ).matches ( )) {
                email.setError ( "Please Enter Valid Email-ID" );
            } else if (type.getSelectedItem ( ).toString ( ).equals ( "Select Option" )) {
                Toast.makeText ( getApplicationContext ( ), "Please Select Option", Toast.LENGTH_LONG ).show ( );
            } else if (organization.length ( ) == 0) {
                organization.setError ( "Please Enter organization Name" );
            } else if (mobile.getText ( ).toString ( ).length ( ) < 9) {
                mobile.setError ( "Enter Only 10 Digit Mobile Number" );
            } else if (enroll.getText ( ).toString ( ).length ( ) < 11) {
                enroll.setError ( "Enter College or Department Id" );
            } else if (Password.length ( ) == 0) {
                password.setError ( "Please Enter Password" );
            } else if (Password.length ( ) <= 7) {
                password.setError ( "Please Enter Password Minimum in 8 Char" );
            } else if (!Password.equals ( c_pass )) {
                C_pass.setError ( "Both Password are not Matched." );
            } else {
                HashMap<String, String> params = new HashMap<> ( );
                params.put ( "firstname", first_name.getText ( ).toString ( ) );
                params.put ( "lastname", last_name.getText ( ).toString ( ) );
                params.put ( "email", email.getText ( ).toString ( ) );
                params.put ( "type", type.getSelectedItem ( ).toString ( ) );
                params.put ( "organization", organization.getText ( ).toString ( ) );
                params.put ( "classname", classname.getText ( ).toString ( ) );
                params.put ( "mobile", mobile.getText ( ).toString ( ) );
                params.put ( "enrollment", enroll.getText ( ).toString ( ) );
                params.put ( "password", password.getText ( ).toString ( ) );
                params.put ( "username", username.getText ( ).toString ( ) );
                if (checkConnections ( )) {
                    register ( params );
                } else {
                    Toast.makeText ( Register.this, "No Internet Connection", Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

        sign_in.setOnClickListener ( v -> {
            Intent a = new Intent ( Register.this, Login.class );
            startActivity ( a );
            finish ( );
        } );
    }

    private void createOrganization() {
        ProgressDialog progressDialog = new ProgressDialog ( Register.this );
        progressDialog.setTitle ( "Please wait " );
        progressDialog.setMessage ( "Creating Organization..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<OrganizationResponseModel> CreateOrganization = networkService.CreateOrganization ( organization.getText ( ).toString ( ) );
        CreateOrganization.enqueue ( new Callback<OrganizationResponseModel> ( ) {
            @Override
            public void onResponse(@NonNull Call<OrganizationResponseModel> call, @NonNull Response<OrganizationResponseModel> response) {
                OrganizationResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {
                        Toast.makeText ( Register.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        finish ( );
                    } else {
                        Toast.makeText ( Register.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                } else {
                    Toast.makeText ( Register.this, "SomeThing Went Wrong", Toast.LENGTH_LONG ).show ( );
                }
                progressDialog.dismiss ( );
            }

            @Override
            public void onFailure(@NonNull Call<OrganizationResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss ( );
            }
        } );
    }

    private void register(HashMap<String, String> params) {
        if (org.isChecked ( )) {
            createOrganization ( );
        }

        ProgressDialog progressDialog = new ProgressDialog ( Register.this );
        progressDialog.setTitle ( "Please wait..." );
        progressDialog.setMessage ( "Registering..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<RegistrationResponseModel> registerCall = networkService.register ( params );
        registerCall.enqueue ( new Callback<RegistrationResponseModel> ( ) {
            @Override
            public void onResponse(@NonNull Call<RegistrationResponseModel> call, @NonNull Response<RegistrationResponseModel> response) {
                RegistrationResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {
                        Toast.makeText ( Register.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        finish ( );
                    } else {
                        Toast.makeText ( Register.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                }
                progressDialog.dismiss ( );
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss ( );
            }
        } );
    }

    public void ShowHidePass3(View view) {

        if (view.getId ( ) == R.id.show_pass1) {

            if (password.getTransformationMethod ( ).equals ( PasswordTransformationMethod.getInstance ( ) )) {
                password.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );
            } else {
                password.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );
            }
        }
    }

    public void ShowHidePass4(View view) {
        if (view.getId ( ) == R.id.show_pass2) {
            if (C_pass.getTransformationMethod ( ).equals ( PasswordTransformationMethod.getInstance ( ) )) {
                C_pass.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );
            } else {
                C_pass.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );
            }
        }
    }

    public boolean checkConnections() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext ( ).getSystemService ( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo ( );

        if (null != activeNetwork) {
            if (ConnectivityManager.TYPE_WIFI == activeNetwork.getType ( )) {
                conn = true;
            }
            if (ConnectivityManager.TYPE_MOBILE == activeNetwork.getType ( )) {
                conn = true;
            }
        } else {
            conn = false;
        }
        return conn;
    }
}