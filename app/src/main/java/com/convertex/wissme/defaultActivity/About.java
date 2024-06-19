package com.convertex.wissme.defaultActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.convertex.wissme.R;

public class About extends AppCompatActivity {

    ImageView imageView;
    TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //for back press
        imageView = findViewById(R.id.image_menu1);
        imageView.setOnClickListener(v -> finish());

        tv1 = findViewById(R.id.insta1);
        tv1.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/_.kanudo_ii/"));
            startActivity(browserIntent);
        });

        tv2 = findViewById(R.id.github);
        tv2.setOnClickListener(view -> {
            Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Arpitpatel259"));
            startActivity(browserIntent1);
        });
    }
}