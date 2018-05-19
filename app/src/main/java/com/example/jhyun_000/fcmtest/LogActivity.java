package com.example.jhyun_000.fcmtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-04-12.
 */


public class LogActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    GridView log_gridView;

    Response response;
    String resp_string;
    Callback callback;
    boolean response_get = false;

    int length_jarray;
    String imagePaths[];
    String timestamp[];
    String urls[];
    String result[];

    HttpThread okhttpThread;

    Handler handler;

    //순서 : okhttp log요청 -> callback 통해서 parseresponse -> setGrid
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        log_gridView = (GridView) findViewById(R.id.log_gridView);

        callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                resp_string = response.body().string();
                Log.i("resp_string", resp_string);
//                response_get = true;
                try {
                    parseResponse(resp_string);
                    Log.i("afterParse", imagePaths[0]);

//                    Message message = null;
//                    message.what= 1;
//                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        int return_parseResponse = 0;
        try {
            return_parseResponse = parseResponse(sendLogHttp(user_email));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (return_parseResponse > 0) {
                setGrid(imagePaths, timestamp, result);
            }
        }

//        try {
//            okhttpThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Log.d("beforeRequest", uuid[0]);
//        setGrid(uuid, timestamp, image_location);

//        MyAsyncTask task = new MyAsyncTask();
//        task.execute("", "", "");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    String sendLogHttp(final String email) throws IOException, ExecutionException, InterruptedException {
//        okhttpThread = new HttpThread(callback, user_email);
//        okhttpThread.start();
        String response;
        CallRequestHttp callRequestHttp = new CallRequestHttp();
//        callRequestHttp.run();        //runnable

        response = callRequestHttp.execute().get();
//        response = callRequestHttp.getResponse();

        Log.i("Response", "sendloghttp : " + response);
        return response;
    }

    public class CallRequestHttp extends AsyncTask<String, String, String> {
        //        class CallRequestHttp implements Runnable{
//RequestHttp requestHttp = new RequestHttp();
        RequestHttp requestHttp;

        //        duration mili second = 1/1000 초
//        3600초 = 1시간
//        3600* 24 = 1day
        int days = 7;

        //        String json = "{\"email\": \"" + user_email + "\", \"duration\": " + 0 + "}";
        String json = "{\"email\": \"" + user_email + "\", \"duration\": " + 3600 * 24 * days * 1000 + "}";
        String response;
        int ok = 0;
//        @Override
//        public void run() {
//            try {
//                response = requestHttp.post("https://grad-project-app.herokuapp.com/user/logs", json);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public String getResponse(){
//            return response;
//        }

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            requestHttp = new RequestHttp();
        }

        @Override
        protected String doInBackground(String... url) {
            String res = null;
            try {
                res = requestHttp.post("https://grad-project-app.herokuapp.com/user/logs", json);
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
            ok = 1;
        }


        public String getResponse() {
            if (ok == 1) {
                Log.i("Response", "response not null");
                return response;
            } else {
                Log.i("Response", "response null/ ok == 0");
                return null;
            }

        }
    }


    int parseResponse(String responsestring) throws JSONException, IOException {
        Log.i("parseResponse", "parseResponse started");
        Log.i("parseResponse-responsesstring", responsestring);
//        if (response == null) return;

//        response.body().string()을 한번만 호출할수 있음
//        Log.i("Response-string", response.body().string());
//        Log.i("Response-tostring", response.body().toString());
//        String jsonData = response.body().string();
        String jsonData = responsestring;

        Log.i("jsonData", jsonData);
        JSONObject Jobject = new JSONObject(jsonData);
//        JSONObject Jobject = new JSONObject(response.body().string());
//        JsonObject Jobject = new JsonParser().parse(jsonData).getAsJsonObject();

//        JSONArray Jarray = Jobject.getAsJsonArray("result");
        JSONArray Jarray = Jobject.getJSONArray("result");
//        JSONArray Jarray = Jobject.getJSONArray( )

        Log.i("Jarraylength", String.valueOf(Jarray.length()));
        length_jarray = Jarray.length();

        imagePaths = new String[length_jarray];
        timestamp = new String[length_jarray];
        result = new String[length_jarray];


        String urls[] = new String[Jarray.length()];

        for (int i = 0; i < length_jarray; i++) {
            JSONObject object = Jarray.getJSONObject(i);

            imagePaths[i] = object.getString("key");
//            timestamp[i] = object.getString("timestamp");
            timestamp[i] = String.valueOf(object.getLong("timestamp"));
            result[i] = object.getString("result");
//            if(object.getString("result").equals("unknown")){
//                image_location[i] = 1;
//            }
//            else{
//                image_location[i] = 0;
//            }
            Log.i("imagePaths", imagePaths[i]);
            Log.i("timestamp", timestamp[i]);
//            Log.i("image_location", String.valueOf(image_location[i]));

            urls[i] = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" + imagePaths[i] + ".jpg";
            Log.i("urls", urls[i]);
        }
        return length_jarray;
    }


    public void setGrid(String[] imagePaths, String[] timestamp, String[] result) {
        Log.i("setGrid", "setGrid started");

        log_gridView = (GridView) findViewById(R.id.log_gridView);
//        gridView.setAdapter(new ImageAdapter(this, urls));

        Log.i("setGrid_imageUuid", imagePaths[0]);
        Log.i("setGrid_timestamp", timestamp[0]);
        Log.i("setGrid_location", result[0]);

        log_gridView.setAdapter(new ImageAdapter2(this, imagePaths, timestamp, result));
//        @NonNull Context context, @NonNull String[] imageUuids, @Nullable String[] timestamp, @NonNull int[] uuid_location
    }

//    public class MyAsyncTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            okhttpThread = new HttpThread(callback, user_email);
//            okhttpThread.start();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.i("onStartUuid", uuid[0]);
//
////            handler.removeMessages(1);
//            setGrid(uuid, timestamp, image_location);
//        }
//
//    }
}