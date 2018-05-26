package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.Constants.server_url_check_address;
import static com.example.jhyun_000.fcmtest.Constants.server_url_device_register;
import static com.example.jhyun_000.fcmtest.Constants.server_url_profile;
import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-05-10.
 */

public class Profile extends AppCompatActivity {
    EditText edit_password;
    EditText edit_protector_phone;
    EditText edit_protector_name;
    EditText edit_address;

    Button button_profile;
    int length_jarray;
    String password;
    String protector_name;
    String protector_number;
    String deviceId;
    String address;

    EditText edit_deviceNumber;
    Button button_device;
    Button button_address;
    Button button_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        int return_parseResponse = 0;
        try {
            return_parseResponse = parseResponse(requestProfile(user_email));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void init() {
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_protector_phone = (EditText) findViewById(R.id.edit_protector_phone);
        edit_protector_name = (EditText) findViewById(R.id.edit_protector_name);
        button_profile = (Button) findViewById(R.id.button_profile);

        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = null;
                String protector_phone = null;
                String protector_name = null;
                password = edit_password.getText().toString();
                protector_phone = edit_protector_phone.getText().toString();
                protector_name = edit_protector_name.getText().toString();

                try {
                    updateProfile(user_email, password, protector_phone, protector_name);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //PUt /user/profile


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

        edit_address = (EditText)findViewById(R.id.edit_address);
        button_address = (Button)findViewById(R.id.button_address);
        button_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edit_address.getText().toString();

                checkAddress(address);
            }
        });

        button_delete = (Button)findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("email", user_email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = jobject.toString();
                DeleteHttp deleteHttp = new DeleteHttp();
                try {
//                    String response = deleteHttp.execute(server_url_profile, json).get();
                    Response response = deleteHttp.execute(server_url_profile, json).get();
                    Log.i("Response", "delete: " + response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class DeleteHttp extends AsyncTask<String, Response, Response> {
        RequestHttp requestHttp;
        String response;

        @Override
        protected void onPreExecute() {
            requestHttp = new RequestHttp();
        }

        @Override
        protected Response doInBackground(String... url) {
            String res = null;
            Response response = null;
            try {
//                res = requestHttp.deleteResponse(url[0], url[1]);
                response = requestHttp.deleteResponse(url[0], url[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(Response... values) {
//            super.onProgressUpdate(values);
            //progressdialog보다 progressbar 추천
            ProgressDialog dialog = new ProgressDialog(Profile.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("전송 중");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Response s) {
//            super.onPostExecute(s);
            try {
                response = s.body().string();
                Log.i("Update-postexecute-s", response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            if(s.code() / 100 == 2) {
                builder.setMessage("회원 정보가 삭제되었습니다.")
                        .setTitle("전송 완료")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다이얼로그를 취소한다
                                dialog.cancel();

                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                                Intent intent = new Intent(Profile.this, EmailPasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }else{
                builder.setMessage("회원 정보가 삭제 중 오류발생하였습니다")
                        .setTitle("삭제 도중 오류")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
            }

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public class NoDialogHttp extends AsyncTask<String, String, String> {
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
            Log.i("Response-postexecute-response", s);
        }
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
            Log.i("Response-postexecute-response", s);

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            builder.setMessage("전송 완료")
                    .setTitle("수정 완료")
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

    String requestProfile(final String email) throws IOException, ExecutionException, InterruptedException {
        String response;

        NoDialogHttp noDialogHttp = new NoDialogHttp();
        String json = "{\"email\": \"" + user_email + "\", \"duration\": " + 0 + "}";
        response = noDialogHttp.execute(server_url_profile, json).get();

        Log.i("Response", response);
        return response;
    }

    public class UpdateHttp extends AsyncTask<String, String, String> {
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
                res = requestHttp.put(url[0], url[1]);
                Log.i("Update", res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
            //progressdialog보다 progressbar 추천
            ProgressDialog dialog = new ProgressDialog(Profile.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("전송 중");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            response = s;
            Log.i("Update-postexecute-s", s);

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            builder.setMessage("전송 완료")
                    .setTitle("수정 완료")
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

    String updateProfile(final String email, String password, String protector_phone, String protector_name) throws IOException, ExecutionException, InterruptedException, JSONException {
        String response;

        UpdateHttp updateHttp = new UpdateHttp();
//        String json = "{\"email\": \"" + email + "\", \"data\": {";
//
//                json +="}}";
//        JSONArray list = new JSONArray();
        JSONObject object = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject protector = null;

        if (password != null && !password.isEmpty()) {
//        if(password.equals(null)){
            data.put("password", password);
        }
        if ((protector_name != null && !protector_name.isEmpty()) || (protector_phone != null && !protector_phone.isEmpty())) {
//            if(!protector_name.equals(null) || !protector_phone.equals(null)){
            protector = new JSONObject();
        }
        if (protector_name != null) {
//            if(!protector_name.equals(null)){
            protector.put("name", protector_name);
        }

        if (protector_phone != null) {
//            if(!protector_phone.equals(null)){
            protector.put("phoneNumber", protector_phone);
            protector.put("countryCode", "+82");
        }


        if (protector != null) {
            data.put("protector", protector);
        }
        object.put("email", email);

        object.put("data", data);

        String json = object.toString();
        Log.i("Json", json);

        response = updateHttp.execute(server_url_profile, json).get();

        Log.i("Response", response);
        return response;
//        return json;

    }

    int parseResponse(String responsestring) throws JSONException, IOException {
        String jsonData = responsestring;

        Log.i("jsonData", jsonData);
        JSONObject Jobject = new JSONObject(jsonData);
//        JSONArray Jarray = Jobject.getJSONArray("result");
//        JSONObject data = Jobject.getJSONObject("data");

//        if (Jobject.has("email") && !Jobject.isNull("email")) {
        if (!Jobject.isNull("email")) {
            String email = Jobject.getString("email");
        }

//        if (Jobject.has("password") && !Jobject.isNull("password")) {
        if (!Jobject.isNull("password")) {
            password = Jobject.getString("password");
        }

        Log.i("Response", "password1 : " + password);

        JSONObject protector = Jobject.getJSONObject("protector");

//         https://stackoverflow.com/questions/12585492/how-to-test-if-a-jsonobject-is-null-or-doesnt-exist
//           if (record.has("my_object_name") && !record.isNull("my_object_name")) {
//      // Do something with object.
//    }
//        "has" checks if the JSONObject contains a specific key.
//       isNull" checks if the value associated with the key is null or if there is no value

        Log.i("Protector", String.valueOf(protector));
        if (protector.has("name") && !protector.isNull("name")) {
            protector_name = protector.getString("name");
        }

        if (protector.has("phoneNumber") && !protector.isNull("phoneNumber")) {
            protector_number = protector.getString("phoneNumber");
        }

        JSONObject mobile = Jobject.getJSONObject("mobile");
        if (mobile.has("phoneNumber") && !mobile.isNull("phoneNumber")) {
            String phoneNumber = mobile.getString("phoneNumber");
        }

        if(Jobject.has("deviceId") && !Jobject.isNull("deviceId")){
            String deviceId = Jobject.getString("deviceId");
            edit_deviceNumber.setHint(deviceId);
        }
        if(Jobject.has("address") && !Jobject.isNull("address")){
            String address = Jobject.getString("address");
            edit_address.setHint(address);
        }

        edit_password.setHint(password);
        edit_protector_phone.setHint(protector_number);
        edit_protector_name.setHint(protector_name);

        return length_jarray;
    }

    int checkAddress(String address){

//        /user/address-check

        JSONObject jobject = new JSONObject();
        try {
            jobject.put("email", user_email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jobject.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = jobject.toString();

        CheckAddressHttp checkAddressHttp = new CheckAddressHttp();
        try {
            String response = checkAddressHttp.execute(server_url_check_address, json).get();
//            Response response = checkAddressHttp.execute(server_url_check_address, json).get();
            Log.i("AddressResponse", response);
//            Log.i("AddressResponse", response.body().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return 0;

    }

    public class CheckAddressHttp extends AsyncTask<String, String, String> {
        RequestHttp requestHttp;
        String response;

        @Override
        protected void onPreExecute() {
            requestHttp = new RequestHttp();
        }

        @Override
        protected String doInBackground(String... url) {
            String res = null;
            try {
                res = requestHttp.post(url[0], url[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
            //progressdialog보다 progressbar 추천
            ProgressDialog dialog = new ProgressDialog(Profile.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("확인 중");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            String res = null;
            res = s;
                Log.i("AddressPost", s);


            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            try {
                JSONObject jobject = new JSONObject(s);
                    if(jobject.has("error")){
                        builder.setMessage("유효한 주소가 아닙니다. 주소를 다시 입력해주세요")
                            .setTitle("주소 검증 결과")
                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //다이얼로그를 취소한다
                                    dialog.cancel();
                                }
                            });
                }else {
                        builder.setMessage("주소 검증이 완료되었습니다")
                                .setTitle("주소 검증 완료")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });
                    }
                }
             catch (JSONException e) {
                e.printStackTrace();
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}



