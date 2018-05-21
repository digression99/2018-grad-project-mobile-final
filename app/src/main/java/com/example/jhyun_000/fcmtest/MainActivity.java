package com.example.jhyun_000.fcmtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.jhyun_000.fcmtest.Constants.server_url_emergency;
import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Token";
    String token;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    int REQUEST_FINE = 1;
    int REQUEST_COARSE = 2;
    int REQUEST_INTERNET = 3;

    Button button_timer_start;
    Button button_timer_end;
    Button button_emergecy;

    EditText timer_expire_edittext;
    EditText timer_interval_edittext;
    Button face_register_page_button;
    Button log_button;
    Button profile_button;
    Button map_button;
    Button logout_button;

    MyDBHandler myDBHandler;
    SQLiteDatabase db;


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

        PermissionCheck();

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

        button_emergecy = (Button) findViewById(R.id.button_emergency);
        button_emergecy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
                startActivity(intent);
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
