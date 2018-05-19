package com.example.jhyun_000.fcmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jhyun_000 on 2018-02-23.
 */

public class TimerActionActivity extends AppCompatActivity {
    Button button_timer_gps_start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_gps);
        init();
    }

    void init() {
        button_timer_gps_start = (Button) findViewById(R.id.button_timer_start_gps);

        button_timer_gps_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActionActivity.this, GPS_Service.class);
                startService(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(this, GPS_Service.class);
        stopService(i);
    }
}
