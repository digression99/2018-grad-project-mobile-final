package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.example.jhyun_000.fcmtest.Constants.server_url_device_register;
import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-05-18.
 */

public class DeviceRegister extends AppCompatActivity {
    EditText edit_deviceNumber;
    Button button_device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        init();
    }

    void init() {
        edit_deviceNumber = (EditText) findViewById(R.id.edit_deviceNumber);
        button_device = (Button) findViewById(R.id.button_device);

        button_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = edit_deviceNumber.getText().toString();

//                 response = callRequestHttp.execute("https://grad-project-app.herokuapp.com/user/profile", json).get();
//                디바이스 등록 주소 : 원래 주소 + /device/device-register
//                "https://grad-project-app.herokuapp.com/device/device-register"

                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("email", user_email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jobject.put("deviceId", deviceId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = jobject.toString();
                CallRequestHttp callRequestHttp = new CallRequestHttp();
                try {
                    String response = callRequestHttp.execute(server_url_device_register, json).get();
                    Log.i("Response", "deviceRegister: " + response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public class CallRequestHttp extends AsyncTask<String, String, String> {
        RequestHttp requestHttp;
        String response;

        @Override
        protected void onPreExecute() {
            requestHttp = new RequestHttp();
        }

        @Override
        protected String doInBackground(String... url) {
            String res = null;
            int count = url.length;
            try {
                res = requestHttp.post(url[0], url[1]);
                Log.i("Response", res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            response = s;
            Log.i("Response-postexecute-s", s);
            Log.i("Response-postexecute-response", response);

            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceRegister.this);

            builder.setMessage("등록 완료")
                    .setTitle("DEVICE REGISTER")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //다이얼로그를 취소한다
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
