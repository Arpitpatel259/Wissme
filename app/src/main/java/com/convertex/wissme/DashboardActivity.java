package com.convertex.wissme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.convertex.wissme.model.AssignWorkResponseModel;
import com.convertex.wissme.model.GetImageModel;
import com.convertex.wissme.model.WorkModel;
import com.convertex.wissme.model.WorkResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    ImageView imageView;
    TextView text_email, network, text_name;
    CircularImageView image_profile;
    SharedPreferences preferences;
    Boolean type, conn;
    RecyclerView circular_work;
    LinearLayout contentView;
    SwipeRefreshLayout refresh;
    static final float END_SCALE = 0.8f;

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @SuppressLint({"WrongConstant", "SetTextI18n", "RtlHardcoded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        GetImage();
        circular_work = findViewById(R.id.circular_work);
        circular_work.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        circular_work.setHasFixedSize(true);
        if (checkConnections()) {
            getWork();
        } else {
            Toast.makeText(DashboardActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.image_menu);
        image_profile = findViewById(R.id.image_profile_dash);
        text_email = findViewById(R.id.text_email);
        fab = findViewById(R.id.fab);
        text_name = findViewById(R.id.text_name);
        network = findViewById(R.id.network);

        preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        text_email.setText(preferences.getString(Constants.Email, "N/A"));
        text_name.setText(preferences.getString(Constants.KEY_USERNAME, "N/A"));

        if (preferences.getString(Constants.KEY_TYPE, "N/A").equals("N/A")) {
            Toast.makeText(DashboardActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        } else if (preferences.getString(Constants.KEY_TYPE, "N/A").equals("Departments")) {
            type = true;
            fab.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_com_work).setVisibility(View.GONE);
        } else {
            type = false;
            findViewById(R.id.layout_com_work).setVisibility(View.VISIBLE);
            findViewById(R.id.view_com_work).setVisibility(View.VISIBLE);
        }

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        findViewById(R.id.layout_logout).setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Are you sure you want to logout?");
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(DashboardActivity.this, "You have been Logged out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DashboardActivity.this, Login.class);
                startActivity(intent);
                finish();
            });
            alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });

        //assign work
        fab.setOnClickListener(view -> {
            if (checkConnections()) {
                startActivity(new Intent(DashboardActivity.this, AssignWork.class));
                finish();
            } else {
                Toast.makeText(DashboardActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.layout_com_work).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, Com_work.class));
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        //back Button
        imageView.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        //refresh
        refresh = findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(() -> {
            Intent i = new Intent(DashboardActivity.this, DashboardActivity.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);
        });

        //Notification section
        findViewById(R.id.layout_notification).setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        //Setting Section
        findViewById(R.id.layout_setting).setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivity.this, Setting.class));
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        //About Section
        findViewById(R.id.layout_about).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, About.class));
            drawerLayout.closeDrawer(Gravity.LEFT);
        });
        animateNavigationDrawer();

        //Participate Section
        findViewById(R.id.layout_participate).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, Participate.class));
            drawerLayout.closeDrawer(Gravity.LEFT);
        });
    }

    private void getWork() {
        ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Getting Your Works..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<WorkResponseModel> get_work = networkService.getworksByEmail(preferences.getString(Constants.Email, "N/A"), preferences.getString(Constants.KEY_ORG, "N/A"));

        get_work.enqueue(new Callback<WorkResponseModel>() {
            @Override
            public void onResponse(Call<WorkResponseModel> call, Response<WorkResponseModel> response) {

                WorkAdapter workAdapter = new WorkAdapter(response.body().getWorks().getWorks());
                circular_work.setAdapter(workAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<WorkResponseModel> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

        List<WorkModel> work;

        WorkAdapter(List<WorkModel> works) {
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
            if (work.get(position).getClassname() != null && !work.get(position).getClassname().equals("")) {
                holder.classname.setText(work.get(position).getClassname());
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
            if (work.get(position).getEnd_time() != null && !work.get(position).getEnd_time().equals("")) {
                holder.end_time.setText(work.get(position).getEnd_time());
            } else {
                holder.end_time.setVisibility(View.GONE);
            }

            //for edit button

            if (type) {
                holder.edit.setVisibility(View.VISIBLE);
            } else {
                holder.edit.setVisibility(View.GONE);
            }

            holder.edit.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), EditWork.class);
                intent.putExtra("id", work.get(holder.getAdapterPosition()).getId());
                intent.putExtra("classname", work.get(holder.getAdapterPosition()).getClassname());
                intent.putExtra("work_title", work.get(holder.getAdapterPosition()).getWork_title());
                intent.putExtra("work_name", work.get(holder.getAdapterPosition()).getWork_name());
                intent.putExtra("end_time", work.get(holder.getAdapterPosition()).getEnd_time());
                intent.putExtra("faculty", work.get(holder.getAdapterPosition()).getFaculty());
                startActivity(intent);
                finish();
            });

            if (type) {
                holder.delete.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
            }

            holder.delete.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Work is Deleted", Toast.LENGTH_SHORT).show());

            holder.cardWork.setOnClickListener(view -> {
                Intent intent;
                if (!type) {
                    intent = new Intent(getApplicationContext(), WorkDetails.class);
                    intent.putExtra("id", work.get(holder.getAdapterPosition()).getId());
                    intent.putExtra("classname", work.get(holder.getAdapterPosition()).getClassname());
                    intent.putExtra("work_title", work.get(holder.getAdapterPosition()).getWork_title());
                    intent.putExtra("work_name", work.get(holder.getAdapterPosition()).getWork_name());
                    intent.putExtra("start_time", work.get(holder.getAdapterPosition()).getStart_time());
                    intent.putExtra("end_time", work.get(holder.getAdapterPosition()).getEnd_time());
                    intent.putExtra("faculty", work.get(holder.getAdapterPosition()).getFaculty());
                } else {
                    intent = new Intent(getApplicationContext(), StudentList.class);
                    intent.putExtra("id", work.get(holder.getAdapterPosition()).getId());
                }
                startActivity(intent);
            });

            holder.delete.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete work?");
                alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                    deleteWork(work.get(holder.getAdapterPosition()).getId());
                    Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                });
                alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            });
        }

        class WorkViewHolder extends RecyclerView.ViewHolder {
            CardView cardWork;
            TextView classname, title, end_time;
            ImageView edit, delete;

            WorkViewHolder(View view) {
                super(view);
                classname = view.findViewById(R.id.text_item_classname);
                title = view.findViewById(R.id.text_item_title);
                end_time = view.findViewById(R.id.text_item_end_time);
                cardWork = view.findViewById(R.id.item_card_view);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
            }
        }
    }

    private void deleteWork(String id) {
        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<AssignWorkResponseModel> get_works = networkService.deleteWork(preferences.getString(Constants.KEY_ORG, "N/A"), id);

        get_works.enqueue(new Callback<AssignWorkResponseModel>() {
            @Override
            public void onResponse(Call<AssignWorkResponseModel> call, Response<AssignWorkResponseModel> response) {
                AssignWorkResponseModel responseBody = response.body();
                Toast.makeText(DashboardActivity.this, responseBody.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<AssignWorkResponseModel> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Something want wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
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

    private void GetImage() {
        preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);
        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<GetImageModel> call = networkService.getImage(preferences.getString(Constants.Email, "N/A"));
        call.enqueue(new Callback<GetImageModel>() {
            @Override
            public void onResponse(Call<GetImageModel> call, Response<GetImageModel> response) {
                String imageUri = "https://wissmw.000webhostapp.com/" + response.body().getUrl();
                image_profile = findViewById(R.id.image_profile_dash);
                Picasso.with(getApplicationContext()).load(imageUri).into(image_profile);
            }

            @Override
            public void onFailure(Call<GetImageModel> call, Throwable t) {
            }
        });
    }
}