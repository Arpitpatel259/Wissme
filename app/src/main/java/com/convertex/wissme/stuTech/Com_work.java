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
import android.widget.Toast;

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

public class Com_work extends AppCompatActivity {

    ImageView imageView4;
    RecyclerView circular_work;
    Boolean type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_work);

        circular_work = findViewById(R.id.circular_com_work);
        circular_work.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        circular_work.setHasFixedSize(true);

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        if (preferences.getString(Constants.KEY_TYPE, "N/A").equals("N/A")) {
            Toast.makeText(Com_work.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        } else type = preferences.getString ( Constants.KEY_TYPE, "N/A" ).equals ( "Departments" );

        getCompleteWorkFors();


        imageView4 = findViewById(R.id.image_menu4);

        imageView4.setOnClickListener( v -> finish() );
    }

    private void getCompleteWorkFors() {
        ProgressDialog progressDialog = new ProgressDialog(Com_work.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Getting Works..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<CompleteWorkResponseModel> gasworks = networkService.getComWorkByEmail( preferences.getString(Constants.Email, "N/A"), preferences.getString(Constants.KEY_ORG, "N/A") );

        gasworks.enqueue(new Callback<CompleteWorkResponseModel>() {
            @Override
            public void onResponse(Call<CompleteWorkResponseModel> call, Response<CompleteWorkResponseModel> response) {

                WorkAdapter workAdapter = new WorkAdapter(response.body().getComWork().getComWork());
                circular_work.setAdapter(workAdapter);
                progressDialog.cancel();
            }

            @Override
            public void onFailure(Call<CompleteWorkResponseModel> call, Throwable t) {
                progressDialog.cancel();
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
            return new WorkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.work_item_container, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
            //classname
            if (work.get(position).getWork_sub() != null && !work.get(position).getWork_sub().equals("")) {
                holder.classname.setText(work.get(position).getWork_sub());
            } else {
                holder.classname.setVisibility(View.GONE);
            }
            //work title
            if (work.get(position).getWork_title() != null && !work.get(position).getWork_title().equals("")) {
                holder.title.setText(work.get(position).getWork_title());
            } else {
                holder.title.setVisibility(View.GONE);
            }
            //work end time
            if (work.get(position).getSubmit_date() != null && !work.get(position).getSubmit_date().equals("")) {
                holder.end_time.setText(work.get(position).getSubmit_date());
            } else {
                holder.end_time.setVisibility(View.GONE);
            }

            holder.cardWork.setOnClickListener( v -> {
                Intent intent = new Intent(getApplicationContext(), ComWorkDetail.class);
                intent.putExtra("wid", work.get(holder.getAdapterPosition()).getWork_id());
                intent.putExtra("filename", work.get(holder.getAdapterPosition()).getFilename());
                intent.putExtra("wsub", work.get(holder.getAdapterPosition()).getWork_sub());
                intent.putExtra("wtitle", work.get(holder.getAdapterPosition()).getWork_title());
                intent.putExtra("wtime", work.get(holder.getAdapterPosition()).getSubmit_date());
                intent.putExtra("url", work.get(holder.getAdapterPosition()).getUrl());

                startActivity(intent);
            } );
        }

        class WorkViewHolder extends RecyclerView.ViewHolder {
            CardView cardWork;
            TextView classname, title, end_time;

            WorkViewHolder(View view) {
                super(view);
                classname = view.findViewById(R.id.text_item_classname);
                title = view.findViewById(R.id.text_item_title);
                end_time = view.findViewById(R.id.text_item_end_time);
                cardWork = view.findViewById(R.id.item_card_view);
            }
        }
    }
}
