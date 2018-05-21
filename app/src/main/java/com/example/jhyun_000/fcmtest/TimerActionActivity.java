package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by jhyun_000 on 2018-02-23.
 */

public class TimerActionActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start_gps);
//        init();

        AlertDialog LDialog = new AlertDialog.Builder(this)
                .setTitle("Acces GPS")
                .setMessage("현재 위치 받아옵니다")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(!TimerActionActivity.this.isFinishing()){
                            finish();
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!TimerActionActivity.this.isFinishing()){
                            finish();
                        }
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TimerActionActivity.this, GPS_Service.class);
                        startService(intent);
                    }
                }).create();
        LDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
              if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    double longitude = (double) intent.getExtras().get("longitude");
                    double latitude = (double) intent.getExtras().get("latitude");
                    Toast.makeText(TimerActionActivity.this, "longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Intent i = new Intent(this, GPS_Service.class);
        stopService(i);
    }

}
