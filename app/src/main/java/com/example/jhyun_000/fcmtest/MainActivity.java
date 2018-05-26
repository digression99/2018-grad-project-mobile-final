package com.example.jhyun_000.fcmtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

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

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Token";
    String token;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    boolean isEmergency = false;

    int REQUEST_FINE = 1;
    int REQUEST_COARSE = 2;
    int REQUEST_INTERNET = 3;

    Button button_timer_start;
    Button button_timer_end;
    ImageView button_emergecy;

    EditText timer_expire_edittext;
    EditText timer_interval_edittext;
    Button face_register_page_button;
    Button log_button;
    Button profile_button;
    Button map_button;
    Button logout_button;

    MyDBHandler myDBHandler;
    SQLiteDatabase db;

    double longitude;
    double latitude;

    private FirebaseAuth mAuth;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findviews();

        myDBHandler = new MyDBHandler(this, null, null, 1);
        db = myDBHandler.getWritableDatabase();

        mAuth = FirebaseAuth.getInstance();

//        PermissionCheck();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            user_email = user.getEmail();
            Toast.makeText(this, user_email, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionCheck();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d("broadcast: longitude", String.valueOf(intent.getExtras().get("longitude")));
                    Log.d("broadcast: latitude", String.valueOf(intent.getExtras().get("latitude")));

                    longitude = (double) intent.getExtras().get("longitude");
                    latitude = (double) intent.getExtras().get("latitude");

                    Toast.makeText(MainActivity.this, "BC longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

                    if (isEmergency) {
                        isEmergency = false;
                        try {
                            sendEmergency(user_email);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(MainActivity.this, GPS_Service.class);
                        stopService(i);
                    }
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
    }

    void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));

        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]

        // [END handle_data_extras]
    }

    void findviews() {
        timer_expire_edittext = (EditText) findViewById(R.id.timer_expire_edittext);
        timer_interval_edittext = (EditText) findViewById(R.id.timer_interval_edittext);

        button_timer_start = (Button) findViewById(R.id.button_timer_start);
        button_timer_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int expire_time = Integer.parseInt(timer_expire_edittext.getText().toString());
                int interval_time = Integer.parseInt(timer_interval_edittext.getText().toString());

                Intent intent = new Intent(MainActivity.this, Timer.class);
                intent.putExtra("expire_time", expire_time);
                intent.putExtra("interval_time", interval_time);
                startService(intent);
            }
        });

        button_timer_end = (Button) findViewById(R.id.button_timer_end);
        button_timer_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Timer.class);
                stopService(intent);

                MyDBHandler myDBHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                myDBHandler.deleteAll();
            }
        });

        button_emergecy = (ImageView) findViewById(R.id.button_emergency);
        button_emergecy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(MainActivity.this, GPS_Service.class);
                startService(intent);
                isEmergency = true;
            }
        });

        face_register_page_button = (Button) findViewById(R.id.face_register_page_button);

        face_register_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaceRegister.class);
                startActivity(intent);
            }
        });

        log_button = (Button) findViewById(R.id.log_button);
        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });

        profile_button = (Button) findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        map_button = (Button) findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeliveredHelp.class);
                startActivity(intent);
            }
        });

        logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void sendTokenHttp() {
        token = FirebaseInstanceId.getInstance().getToken();

        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
//                     RequestBody body = new FormBody.Builder()
//                             .add("Token", FirebaseInstanceId.getInstance().getToken())
//                             .build();

                RequestBody body = RequestBody.create(JSON, "{\"token\": \"" + token + "\"}");
                Log.d(TAG, "Body : " + body);

                Request request = new Request.Builder()
                        .url(server_url_emergency)
                        .post(body)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    void sendEmergency(final String email) throws IOException {
        Toast.makeText(MainActivity.this, "Emerg longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

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
}
