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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.Constants.server_url_emergency;
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
        JSONObject jobject = new JSONObject();
        try {
            jobject.put("email", email);

            JSONObject current_location = new JSONObject();
//            current_location.put("longitude", longitude);
//            current_location.put("latitude", latitude);

            current_location.put("longitude", String.valueOf(longitude));
            current_location.put("latitude", String.valueOf(latitude));
            jobject.put("current_location", current_location);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray locations = new JSONArray();
        if (cursor.moveToFirst()) {
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);


            JSONObject location = new JSONObject();
            try{
//                location.put("longitude", longitude);
//                location.put("latitude", latitude);


                location.put("longitude", String.valueOf(longitude));
                location.put("latitude", String.valueOf(latitude));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            locations.put(location);
        }

        while (cursor.moveToNext()) {
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);
            Log.i("Emergency", "longitude is : " + longitude + " latitude is : " + latitude);

            JSONObject location = new JSONObject();
            try{
//                location.put("longitude", longitude);
//                location.put("latitude", latitude);

                location.put("longitude", String.valueOf(longitude));
                location.put("latitude", String.valueOf(latitude));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            locations.put(location);
            try {
                jobject.put("locations", locations);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String Jsonarray_nice = jobject.toString();
        Log.i("Emergency", "Jsonarray:" + Jsonarray_nice);

        //이걸 server로 보내야함
        final String finalJsonarray = Jsonarray_nice;
        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, finalJsonarray);
                Log.i("Emergency", "Emergency Body : " + body);

                Request request = new Request.Builder()
                        .url(server_url_emergency)
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

        Log.i("Emergency", "Jsonarray :" + Jsonarray_nice);
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
