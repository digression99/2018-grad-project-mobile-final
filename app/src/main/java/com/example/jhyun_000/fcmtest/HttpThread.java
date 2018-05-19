package com.example.jhyun_000.fcmtest;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by jhyun_000 on 2018-04-16.
 */

public class HttpThread extends Thread {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Callback callback;
    String email;

    public HttpThread(Callback callback, String email) {
        this.callback = callback;
        this.email = email;
    }

    @Override
    public void run() {
        super.run();
        OkHttpClient client = new OkHttpClient();
//                     RequestBody body = new FormBody.Builder()
//                             .add("Token", FirebaseInstanceId.getInstance().getToken())
//                             .build();

        RequestBody body = RequestBody.create(JSON, "{\"email\": \"" + email + "\", \"duration\": " + 0 + "}");
        Log.d("sendLogHttp", "Body : " + body);

        Request request = new Request.Builder()
                .url("https://grad-project-app.herokuapp.com/user/logs")
                .post(body)
                .build();


//                    response = client.newCall(request).execute();
        client.newCall(request).enqueue(callback);
    }
}
