package com.example.jhyun_000.fcmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-04-17.
 */

public class EmergencyActivity extends AppCompatActivity {
    boolean isEmergency = false;
    MyDBHandler myDBHandler;
    SQLiteDatabase db;

    double longitude;
    double latitude;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    int REQUEST_FINE = 1;
    int REQUEST_COARSE = 2;
    int REQUEST_INTERNET = 3;

    ImageView activity_emergency_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        myDBHandler = new MyDBHandler(this, null, null, 1);
        db = myDBHandler.getWritableDatabase();

        activity_emergency_button = (ImageView) findViewById(R.id.activity_emergency_button);
        activity_emergency_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyActivity.this, GPS_Service.class);
                startService(intent);

                isEmergency = true;
            }
        });
    }

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        PermissionCheck();

        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

//                    textView.append("\n" + intent.getExtras().get("longitude") + " " + intent.getExtras().get("latitude"));
                    Log.d("broadcast: longitude", String.valueOf(intent.getExtras().get("longitude")));
                    Log.d("broadcast: latitude", String.valueOf(intent.getExtras().get("latitude")));

                    longitude = (double) intent.getExtras().get("longitude");
                    latitude = (double) intent.getExtras().get("latitude");

                    Toast.makeText(EmergencyActivity.this, "BC longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

                    if (isEmergency) {
                        isEmergency = false;
                        try {
                            sendEmergency(user_email);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(EmergencyActivity.this, GPS_Service.class);
                        stopService(i);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }


    void sendEmergency(final String email) throws IOException {
        Toast.makeText(EmergencyActivity.this, "Emerg longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

        Cursor cursor = myDBHandler.findAll();
        String Jsonarray = "{\"email\": \"" + email + "\"," +
                "\"current_location\": {\"longitude\": " + longitude + ", \"latitude\": " + latitude + "}, ";
        if (cursor.moveToFirst()) {
//            String longitude = cursor.getString(1);
//            String latitude = cursor.getString(2);
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);

            Jsonarray += "\"locations\":[ " + "{\"longitude\": " + longitude + ", \"latitude\": " + latitude + "}";
        }

        while (cursor.moveToNext()) {
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);
            Log.i("Emergency", "longitude is : " + longitude + " latitude is : " + latitude);
//            if (longitude != null && latitude != null)
            Jsonarray += ", {\"longitude\" : " + longitude + ", \"latitude\": " + latitude + "}";
        }
        Jsonarray += "]}";

        Log.i("Emergency", "Jsonarray:" + Jsonarray);

        //이걸 server로 보내야함
        final String finalJsonarray = Jsonarray;
        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, finalJsonarray);
                Log.i("Emergency", "Emergency Body : " + body);

                Request request = new Request.Builder()
//                        .url("http://grad-project-app.herokuapp.com/user/emergency")
                        .url(getString(R.string.server_url_emergency))
                        .post(body)
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Log.i("Emergency Response", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

//        RequestHttp requestHttp = new RequestHttp();
//        String response = requestHttp.post("http://grad-project-app.herokuapp.com/user/emergency", Jsonarray);
        Log.i("Emergency", "Jsonarray :" + Jsonarray);
//        Log.i("Response", response );
    }

    private void PermissionCheck() {
        int permission_fine_location = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse_location = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission_internet = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        if (permission_fine_location == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE);
        }

        if (permission_coarse_location == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE);
        }

        if (permission_internet == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_INTERNET);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
