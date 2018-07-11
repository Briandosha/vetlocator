package com.example.brian.vetlocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mVet, mFarmer, mheatmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVet = (Button) findViewById(R.id.vet);
        mFarmer = (Button) findViewById(R.id.farmer);
        mheatmaps = findViewById(R.id.heatmaps);

        startService(new Intent(MainActivity.this, onAppKilled.class));

        mVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, VetLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FarmerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        mheatmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HeatmapsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}
