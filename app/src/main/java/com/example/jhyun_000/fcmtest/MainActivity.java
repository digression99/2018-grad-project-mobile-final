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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceId;

//2018_0519 7:06 test hyun
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Token";
    String token;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    int REQUEST_FINE = 1;
    int REQUEST_COARSE = 2;
    int REQUEST_INTERNET = 3;

    Button button_gps;
    Button button_timer_start;
    Button button_timer_end;
    Button button_emergecy;
    TextView textView;
    EditText timer_expire_edittext;
    EditText timer_interval_edittext;
    Button login_page_button;
    Button face_register_page_button;
    Button log_button;
    //    Button button_visitor;
    Button profile_button;
    Button device_button;
    Button map_button;

    double longitude;
    double latitude;

    boolean isEmergency = false;
    MyDBHandler myDBHandler;
    SQLiteDatabase db;

    public int timer_expire;
    public int timer_interval;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findviews();

        myDBHandler = new MyDBHandler(this, null, null, 1);
        db = myDBHandler.getWritableDatabase();

        mAuth = FirebaseAuth.getInstance();

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
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }

            //
            String num1 = getIntent().getExtras().get("num1").toString();
            TextView main_nt_textview = (TextView) findViewById(R.id.main_nt_textview);
            main_nt_textview.setText(num1);
        }
        // [END handle_data_extras]
    }

    void findviews() {
        button_gps = (Button) findViewById(R.id.button_gps);
        button_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GPS_Service.class);
                startService(i);
            }
        });

        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTokenHttp();
            }
        });

        timer_expire_edittext = (EditText) findViewById(R.id.timer_expire_edittext);
        timer_interval_edittext = (EditText) findViewById(R.id.timer_interval_edittext);

        timer_expire_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                start 지점에서 시작되는 count 갯수만큼의 글자들이 after 길이만큼의 글자로 대치되려고 할 때 호출된다
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                start 지점에서 시작되는 before 갯수만큼의 글자들이 count 갯수만큼의 글자들로 대치되었을 때 호출된다
            }

            @Override
            public void afterTextChanged(Editable s) {
//                EditText의 텍스트가 변경되면 호출된다
                String str = s.toString();
                if (str.length() > 0) {
                    timer_expire = Integer.valueOf(str);
                    button_timer_start.setEnabled(true);
                } else {
                    button_timer_start.setEnabled(false);
                }
            }
        });

        timer_interval_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                start 지점에서 시작되는 count 갯수만큼의 글자들이 after 길이만큼의 글자로 대치되려고 할 때 호출된다
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                start 지점에서 시작되는 before 갯수만큼의 글자들이 count 갯수만큼의 글자들로 대치되었을 때 호출된다
            }

            @Override
            public void afterTextChanged(Editable s) {
//                EditText의 텍스트가 변경되면 호출된다
                String str = s.toString();
                if (str.length() > 0) {
                    timer_interval = Integer.valueOf(str);
                    button_timer_start.setEnabled(true);
                } else {
                    button_timer_start.setEnabled(false);
                }
            }
        });

        button_timer_start = (Button) findViewById(R.id.button_timer_start);
        button_timer_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Timer.class);
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

        button_emergecy = (Button) findViewById(R.id.button_emergency);
        button_emergecy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, GPS_Service.class);
//                startService(intent);
//
//                isEmergency = true;

                Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
                startActivity(intent);

//                sendEmergency("ddd@gmail.com");
            }
        });

        textView = (TextView) findViewById(R.id.textView);
        login_page_button = (Button) findViewById(R.id.login_page_button);
        face_register_page_button = (Button) findViewById(R.id.face_register_page_button);

        login_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
            }
        });

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

//        button_visitor = (Button) findViewById(R.id.button_visitor);
//        button_visitor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ViewVisitor.class);
//                String uuid[] = {"a86d0a71-5152-4a09-a9f4-880acc661008"};
//                String result[] = {"friend"};
//                intent.putExtra("uuids", uuid);
//                intent.putExtra("result", result);
//                startActivity(intent);
//            }
//        });
        profile_button = (Button) findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        device_button = (Button) findViewById(R.id.device_button);
        device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceRegister.class);
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
    }

    void sendTokenHttp() {
        // Get token
        token = FirebaseInstanceId.getInstance().getToken();

        //Nexus 5X 26 token: eEkA4fDyEKQ:APA91bG6wK8W4hu2BjJoTMpPPWAZakySNVpSEHF4OJLHayJxKgo1pt30YO29SKH9w_hqZbbSD21K6zgTaP7rg7PJinmBz4vxIGUbmeMTYP6Kt7XqFDe9iUA0mbjdLfPi1tcV832KBUsa

        // Log and toast
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
                        .url("https://pure-depths-50816.herokuapp.com/user/emergency")
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

    void sendEmergency(final String email) {
        Toast.makeText(MainActivity.this, "Emerg longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

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
//            cursor.getString(0);
//            String longitude = cursor.getString(1);
//            String latitude = cursor.getString(2);
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);
            Log.i("cursor", "longitude is : " + longitude + " latitude is : " + latitude);
//            if (longitude != null && latitude != null)
            Jsonarray += ", {\"longitude\" : " + longitude + ", \"latitude\": " + latitude + "}";
        }
        Jsonarray += "]}";

        Log.d("Jsonarray", Jsonarray);

        //이걸 server로 보내야함
        final String finalJsonarray = Jsonarray;
        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, finalJsonarray);
                Log.d(TAG, "Emergency Body : " + body);

                Request request = new Request.Builder()
//                        .url("http://grad-project-app.herokuapp.com/user/emergency")
                        .url(getString(R.string.server_url_emergency))
                        .post(body)
                        .build();

//                try {
//                    client.newCall(request).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Log.d("Response", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        Log.d("Jsonarray", Jsonarray);
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

                    textView.append("\n" + intent.getExtras().get("longitude") + " " + intent.getExtras().get("latitude"));
                    Log.d("broadcast: longitude", String.valueOf(intent.getExtras().get("longitude")));
                    Log.d("broadcast: latitude", String.valueOf(intent.getExtras().get("latitude")));

                    longitude = (double) intent.getExtras().get("longitude");
                    latitude = (double) intent.getExtras().get("latitude");

                    Toast.makeText(MainActivity.this, "BC longitude : " + String.valueOf(longitude) + "latitude : " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();

                    if (isEmergency) {
                        isEmergency = false;
                        sendEmergency(user_email);
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
