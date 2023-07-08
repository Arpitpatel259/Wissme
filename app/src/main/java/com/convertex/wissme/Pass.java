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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.model.PasswordResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pass extends AppCompatActivity {

    Button show_pass4;
    Button cp;
    Button show_pass5;
    EditText pass, re_password, fetch_email, fetch_username;
    Boolean conn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_pass );

        preferences = getSharedPreferences ( Constants.MyPREFERENCES, MODE_PRIVATE );
        show_pass4 = findViewById ( R.id.show_pass4 );
        show_pass5 = findViewById ( R.id.show_pass5 );
        pass = findViewById ( R.id.newpass );
        re_password = findViewById ( R.id.newpass1 );
        fetch_email = findViewById ( R.id.fetch_email );
        fetch_username = findViewById ( R.id.fetch_username );

        cp = findViewById ( R.id.cp );

        cp.setOnClickListener ( v -> {

            String Pass = pass.getText ( ).toString ( );
            String c_pass = re_password.getText ( ).toString ( );

            if (fetch_email.getText ( ).toString ( ).equals ( "" )) {
                fetch_email.setError ( "Please Enter Email Address" );
            } else if (!Patterns.EMAIL_ADDRESS.matcher ( fetch_email.getText ( ).toString ( ) ).matches ( )) {
                fetch_email.setError ( "Please Enter Valid Email-ID" );
            } else if (fetch_username.length ( ) == 0) {
                fetch_username.setError ( "Please Enter Username" );
            } else if (Pass.length ( ) == 0) {
                pass.setError ( "Please Enter Password" );
            } else if (Pass.length ( ) <= 7) {
                pass.setError ( "Please Enter Password Minimum in 8 Char" );
            } else if (!Pass.equals ( c_pass )) {
                re_password.setError ( "Both Password are not Matched." );
            } else {
                if (checkConnections ( )) {
                    ResetPassword ();
                } else {
                    Toast.makeText ( Pass.this, "No Internet Connection", Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );
    }

    //reset Password
    private void ResetPassword() {
        ProgressDialog progressDialog = new ProgressDialog ( Pass.this );
        progressDialog.setTitle ( "Please wait " );
        progressDialog.setMessage ( "Password Changing..." );
        progressDialog.setCancelable ( false );
        progressDialog.show ( );

        NetworkService networkService = NetworkClient.getClient ( ).create ( NetworkService.class );
        Call<PasswordResponseModel> Password = networkService.resetPass ( fetch_email.getText ( ).toString ( ), fetch_username.getText ( ).toString ( ), pass.getText ().toString () );
        Password.enqueue ( new Callback<PasswordResponseModel>( ) {
            @Override
            public void onResponse(@NonNull Call<PasswordResponseModel> call, @NonNull Response<PasswordResponseModel> response) {
                PasswordResponseModel responseBody = response.body ( );
                if (responseBody != null) {
                    if (responseBody.getSuccess ( ).equals ( "1" )) {
                        Toast.makeText ( Pass.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                        Intent intent = new Intent ( Pass.this, Login.class );
                        startActivity ( intent );
                        finish ( );
                    } else {
                        Toast.makeText ( Pass.this, responseBody.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                } else {
                    Toast.makeText ( Pass.this, "SomeThing Went Wrong" , Toast.LENGTH_LONG ).show ( );
                }
                progressDialog.dismiss ( );
            }

            @Override
            public void onFailure(@NonNull Call<PasswordResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss ( );
            }
        } );
    }

    //show Hide Password
    public void ShowHidePass1(View view) {
        if (view.getId ( ) == R.id.show_pass4) {
            if (pass.getTransformationMethod ( ).equals ( PasswordTransformationMethod.getInstance ( ) )) {
                pass.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );
            } else {
                pass.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );
            }
        }
    }

    //show Hide Password
    public void ShowHidePass2(View view) {

        if (view.getId ( ) == R.id.show_pass5) {

            if (re_password.getTransformationMethod ( ).equals ( PasswordTransformationMethod.getInstance ( ) )) {
                re_password.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );
            } else {
                re_password.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );
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

