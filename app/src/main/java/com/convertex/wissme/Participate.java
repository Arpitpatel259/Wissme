package com.convertex.wissme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.convertex.wissme.model.LoginResponseModel;
import com.convertex.wissme.model.UserModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Participate extends AppCompatActivity {

    RecyclerView part;
    Boolean conn,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participate);

        part = findViewById(R.id.part);
        part.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        part.setHasFixedSize(true);
        findViewById(R.id.image_menu_back).setOnClickListener(view -> finish());

        if (checkConnections()) {
            GetParticipates();
        } else {
            Toast.makeText(Participate.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        GetParticipates();
    }

    private void GetParticipates() {
        ProgressDialog progressDialog = new ProgressDialog(Participate.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Getting Members..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<LoginResponseModel> getMember;
        if (preferences.getString(Constants.KEY_TYPE, "N/A").equals("Departments")) {
            getMember = networkService.getParticipate(preferences.getString(Constants.KEY_ORG, "N/A"), "Student");
        } else {
            getMember = networkService.getParticipate(preferences.getString(Constants.KEY_ORG, "N/A"), "Departments");
        }

        getMember.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {

                assert response.body() != null;
                WorkAdapter workAdapter = new WorkAdapter(response.body().getUserDetailObject().getUserDetail());
                part.setAdapter(workAdapter);
                progressDialog.cancel();
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                progressDialog.cancel();
            }
        });
    }

    private static class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

        List<UserModel> work;

        WorkAdapter(List<UserModel> works) {
            this.work = works;
        }

        @Override
        public int getItemCount() {
            return work.size();
        }

        @NonNull
        @Override
        public WorkAdapter.WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WorkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_item_container, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull WorkAdapter.WorkViewHolder holder, int position) {

            //name
            if (work.get(position).getFirstname() != null && !work.get(position).getFirstname().equals("")) {
                holder.name.setText(work.get(position).getFirstname() + " " + work.get(position).getLastname());
            } else {
                holder.name.setVisibility(View.GONE);
            }
            //id
            if (work.get(position).getEnrollment() != null && !work.get(position).getEnrollment().equals("")) {
                holder.id.setText(work.get(position).getEnrollment());
            } else {
                holder.id.setVisibility(View.GONE);
            }
            //classname
            if (work.get(position).getClassname() != null && !work.get(position).getClassname().equals("")) {
                holder.classname.setText(work.get(position).getClassname());
            } else {
                holder.classname.setVisibility(View.GONE);
            }

            //mobile
            if (work.get(position).getMobile() != null && !work.get(position).getMobile().equals("")) {
                holder.mobile.setText(work.get(position).getMobile());
            } else {
                holder.mobile.setVisibility(View.GONE);
            }
        }

        static class WorkViewHolder extends RecyclerView.ViewHolder {
            CardView cardWork;
            TextView name, id, classname, mobile;

            WorkViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.member_name);
                id = view.findViewById(R.id.member_id);
                classname = view.findViewById(R.id.member_class);
                mobile = view.findViewById(R.id.member_mobile);
                cardWork = view.findViewById(R.id.view_members);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public boolean checkConnections() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (ConnectivityManager.TYPE_WIFI == activeNetwork.getType()) {
                conn = true;
            }
            if (ConnectivityManager.TYPE_MOBILE == activeNetwork.getType()) {
                conn = true;
            }
        } else {
            TextView network = findViewById(R.id.network);
            network.setVisibility(View.VISIBLE);
            network.setText("No Internet Connection" + "\n" + "Please Connect To The Internet");
            conn = false;
        }
        return conn;
    }
}