package com.convertex.wissme.stuTech;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.convertex.wissme.R;
import com.convertex.wissme.model.CompleteWorkModel;
import com.convertex.wissme.model.CompleteWorkResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentList extends AppCompatActivity {

    ImageView imageView4;
    RecyclerView circular_work;
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        tx = findViewById(R.id.beach);
        circular_work = findViewById(R.id.circular_student_list);
        circular_work.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        circular_work.setHasFixedSize(true);
        getCompleteWork();


        imageView4 = findViewById(R.id.image_menu4);

        imageView4.setOnClickListener(v -> finish());
    }

    private void getCompleteWork() {
        ProgressDialog progressDialog = new ProgressDialog(StudentList.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Getting Works..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<CompleteWorkResponseModel> get_works = networkService.getComWorkForDepart(preferences.getString(Constants.Email, "N/A"), preferences.getString(Constants.KEY_ORG, "N/A"), getIntent().getStringExtra("id"));

        get_works.enqueue(new Callback<CompleteWorkResponseModel>() {
            @Override
            public void onResponse(Call<CompleteWorkResponseModel> call, Response<CompleteWorkResponseModel> response) {

                if (response.body() != null && response.body().getComWork() != null && response.body().getComWork().getComWork() != null) {
                    circular_work.setVisibility(View.VISIBLE);
                    tx.setVisibility(View.GONE);
                    WorkAdapter workAdapter = new WorkAdapter(response.body().getComWork().getComWork());
                    circular_work.setAdapter(workAdapter);
                } else {
                    circular_work.setVisibility(View.GONE);
                    tx.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<CompleteWorkResponseModel> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

        List<CompleteWorkModel> work;

        WorkAdapter(List<CompleteWorkModel> works) {
            this.work = works;
        }

        @Override
        public int getItemCount() {
            return work.size();
        }

        @NonNull
        @Override
        public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WorkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.department_com_work, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
            //classname
            if (work.get(position).getEnrollment() != null && !work.get(position).getEnrollment().equals("")) {
                holder.enroll.setText(work.get(position).getEnrollment());
            } else {
                holder.enroll.setVisibility(View.GONE);
            }
            //work title
            if (work.get(position).getName() != null && !work.get(position).getName().equals("")) {
                holder.name.setText(work.get(position).getName());
            } else {
                holder.name.setVisibility(View.GONE);
            }
            //work end time
            if (work.get(position).getSubmit_date() != null && !work.get(position).getSubmit_date().equals("")) {
                holder.end_time.setText(work.get(position).getSubmit_date());
            } else {
                holder.end_time.setVisibility(View.GONE);
            }

            holder.cardWork.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ComWorkDetail.class);
                intent.putExtra("wid", work.get(holder.getAdapterPosition()).getWork_id());
                intent.putExtra("filename", work.get(holder.getAdapterPosition()).getFilename());
                intent.putExtra("wsub", work.get(holder.getAdapterPosition()).getWork_sub());
                intent.putExtra("wtitle", work.get(holder.getAdapterPosition()).getWork_title());
                intent.putExtra("wtime", work.get(holder.getAdapterPosition()).getSubmit_date());
                intent.putExtra("url", work.get(holder.getAdapterPosition()).getUrl());

                startActivity(intent);
            });

        }

        class WorkViewHolder extends RecyclerView.ViewHolder {
            CardView cardWork;
            TextView name, enroll, end_time;

            WorkViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.text_names);
                enroll = view.findViewById(R.id.text_enrolls);
                end_time = view.findViewById(R.id.submitted_date);
                cardWork = view.findViewById(R.id.work_card_view);
            }
        }
    }
}
