package com.convertex.wissme;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.model.WorkResponseModel;
import com.convertex.wissme.network.NetworkClient;
import com.convertex.wissme.network.NetworkService;
import com.convertex.wissme.utils.Constants;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComWorkDetail extends AppCompatActivity {

    TextView textSub, textTitle, textSTime, textFac, textDes, textFileName;
    ImageView back, pdf_image, pdf_download;
    LinearLayout layoutClick;
    private long refid;
    DownloadManager downloadManager;
    private Uri Download_Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_work_detail);

        textSub = findViewById(R.id.Sub_name);
        textTitle = findViewById(R.id.Title_name);
        textSTime = findViewById(R.id.submit_time);
        layoutClick = findViewById(R.id.PDF);
        textDes = findViewById(R.id.Work_name);
        textFac = findViewById(R.id.faculty_name);
        textFileName = findViewById(R.id.text_filename);
        back = findViewById(R.id.image);
        pdf_image = findViewById(R.id.pdf_image);
        pdf_download = findViewById(R.id.pdf_download);

        getWorkDetails();

        textFileName.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PDFViewer.class);
            intent.putExtra("url", getIntent().getStringExtra("url"));
            intent.putExtra("filename", getIntent().getStringExtra("filename") + ".pdf");
            startActivity(intent);

        });
        back.setOnClickListener(v -> onBackPressed());

        pdf_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "/Download/wissme/");
                if (!file.exists()) {
                    file.mkdir();
                }

                String url = Constants.BASE_URL + getIntent().getStringExtra("url");
                String fileName = url.substring(url.lastIndexOf('/') + 1);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle(fileName);
                request.setDescription("Downloading File Please Wait...");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationUri(Uri.fromFile(new File(file,fileName)));

                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        });
    }

    private void getWorkDetails() {
        ProgressDialog progressDialog = new ProgressDialog(ComWorkDetail.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Getting Works..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences preferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE);

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<WorkResponseModel> get_work = networkService.getworksById(preferences.getString(Constants.Email, "N/A"), preferences.getString(Constants.KEY_ORG, "N/A"), getIntent().getStringExtra("wid"));

        get_work.enqueue(new Callback<WorkResponseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WorkResponseModel> call, Response<WorkResponseModel> response) {
                WorkResponseModel rb = response.body();
                textTitle.setText(rb.getWorks().getWorks().get(0).getWork_title());
                textSub.setText(rb.getWorks().getWorks().get(0).getClassname());
                textDes.setText("Instruction : " +
                        "\n" + rb.getWorks().getWorks().get(0).getWork_name());
                textFac.setText("Assigned By : " + rb.getWorks().getWorks().get(0).getFaculty());
                textFileName.setText(getIntent().getStringExtra("filename") + ".pdf");
                textSTime.setText("Submitted at : " + getIntent().getStringExtra("wtime"));
                progressDialog.cancel();
            }

            @Override
            public void onFailure(Call<WorkResponseModel> call, Throwable t) {
                progressDialog.cancel();
            }
        });
    }
}