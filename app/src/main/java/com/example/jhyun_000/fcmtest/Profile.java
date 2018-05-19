package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-05-10.
 */

public class Profile extends AppCompatActivity {
    EditText edit_password;
    EditText edit_protector_phone;
    EditText edit_protector_name;

    Button button_profile;
    int length_jarray;
    String password;
    String protector_name;
    String protector_number;



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
        }
    }


    String requestProfile(final String email) throws IOException, ExecutionException, InterruptedException {
        String response;

        CallRequestHttp callRequestHttp = new CallRequestHttp();
        String json = "{\"email\": \"" + user_email + "\", \"duration\": " + 0 + "}";
        response = callRequestHttp.execute("https://grad-project-app.herokuapp.com/user/profile", json).get();

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
            Log.i("Update-postexecute-response", response);

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            builder.setMessage("전송 완료")
                    .setTitle("프로필 수정")
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
        }

        if (protector != null) {
            data.put("protector", protector);
        }
        object.put("email", email);

        object.put("data", data);

        String json = object.toString();
        Log.i("Json", json);

        response = updateHttp.execute("https://grad-project-app.herokuapp.com/user/profile", json).get();

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

        Log.i("Response", "password2 : " + password);
        edit_password.setHint(password);
        edit_protector_phone.setHint(protector_number);
        edit_protector_name.setHint(protector_name);

        return length_jarray;
    }

}
