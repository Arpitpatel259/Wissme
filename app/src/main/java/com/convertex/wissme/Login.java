package com.convertex.wissme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.model.LoginResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    TextView textCreateAccount, pass;
    EditText inputEmail;
    EditText inputPassword;
    Button buttonLogin;
    Button show_pass;
    Boolean conn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        checkConnections ( );

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        String isLoggedIn = preferences.getString ( Constants.Email, " " );
        if (!isLoggedIn.equals ( " " )) {
            Intent intent = new Intent ( Login.this, DashboardActivity.class );
            startActivity ( intent );
            finish ( );
        }

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, Context.MODE_PRIVATE );
        show_pass = findViewById ( R.id.show_pass );
        pass = findViewById ( R.id.for_pass );
        textCreateAccount = findViewById ( R.id.sign_up );
        buttonLogin = findViewById ( R.id.login_btn );
        inputEmail = findViewById ( R.id.emailId );
        inputPassword = findViewById ( R.id.password1 );

        textCreateAccount.setOnClickListener ( v -> {
            if (checkConnections ( )) {
                startActivity ( new Intent ( getApplicationContext ( ), Register.class ) );
            } else {
                Toast.makeText ( Login.this, "No Internet Connection", Toast.LENGTH_SHORT ).show ( );
            }
        } );

        buttonLogin.setOnClickListener ( view -> {
            if (inputEmail.getText ( ).toString ( ).equals ( "" )) {
                inputEmail.setError ( "Please Enter Email Address" );
            } else if (!Patterns.EMAIL_ADDRESS.matcher ( inputEmail.getText ( ).toString ( ) ).matches ( )) {
                inputEmail.setError ( "Please Enter Valid Email-ID" );
            } else if (inputPassword.getText ( ).toString ( ).equals ( "" )) {
                inputPassword.setError ( "Please Enter Password" );
            } else {
                if (checkConnections ( )) {
                    login ( );
                } else {
                    Toast.makeText ( Login.this, "No Internet Connection", Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

        pass.setOnClickListener ( view -> {
            if (checkConnections ( )) {
                startActivity ( new Intent ( getApplicationContext ( ), Pass.class ) );
            } else {
                Toast.makeText ( Login.this, "No Internet Connection", Toast.LENGTH_SHORT ).show ( );
            }
        } );
    }

    private void login() {
        ProgressDialog progressDialog = new ProgressDialog ( Login.this );
        progressDialog.setTitle ( "Please wait " );
        progressDialog.setMessage ( "Logging..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<LoginResponseModel> login = networkService.login ( inputEmail.getText ( ).toString ( ), inputPassword.getText ( ).toString ( ) );
        login.enqueue ( new Callback<LoginResponseModel> ( ) {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull Response<LoginResponseModel> response) {
                LoginResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {

                        String email = inputEmail.getText ( ).toString ( );
                        SharedPreferences.Editor editor = preferences.edit ( );
                        editor.putString ( Constants.Email, email );
                        editor.putString ( Constants.KEY_USERNAME, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getFirstname ( ) + " " + responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getLastname ( ) );
                        editor.putString ( Constants.KEY_U_NAME, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getUsername ( ) );
                        editor.putString ( Constants.KEY_CONTACT, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getMobile ( ) );
                        editor.putString ( Constants.KEY_TYPE, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getType ( ) );
                        editor.putString ( Constants.KEY_ORG, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getOrganization ( ) );
                        editor.putString ( Constants.KEY_CLASS, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getClassname ( ) );
                        editor.putString ( Constants.KEY_ENROLL, responseBody.getUserDetailObject ( ).getUserDetail ( ).get ( 0 ).getEnrollment ( ) );

                        editor.apply ( );

                        Toast.makeText ( Login.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        Intent intent = new Intent ( Login.this, DashboardActivity.class );
                        startActivity ( intent );
                        finish ( );
                    } else {
                        Toast.makeText ( Login.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                } else {
                    Toast.makeText ( Login.this, "SomeThing Went Wrong", Toast.LENGTH_LONG ).show ( );
                }
                progressDialog.dismiss ( );
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss ( );
            }
        } );
    }

    public void ShowHidePass5(View view) {

        if (view.getId ( ) == R.id.show_pass) {
            if (inputPassword.getTransformationMethod ( ).equals ( PasswordTransformationMethod.getInstance ( ) )) {
                inputPassword.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );
            } else {
                inputPassword.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );
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