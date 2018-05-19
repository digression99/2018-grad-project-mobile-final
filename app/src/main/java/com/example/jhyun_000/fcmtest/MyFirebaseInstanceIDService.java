package com.example.jhyun_000.fcmtest; /**
 * Created by jhyun_000 on 2018-02-09.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String TOKEN;

    @Override
    public void onTokenRefresh() {  //앱 새로 설치하거나 새로운 토큰 생길때만 호출됨
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        TOKEN = refreshedToken;
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        new Thread() {

            public void run() {
                OkHttpClient client = new OkHttpClient();
//                     RequestBody body = new FormBody.Builder()
//                             .add("Token", FirebaseInstanceId.getInstance().getToken())
//                             .build();

                RequestBody body = RequestBody.create(JSON, "{\"installed_token\": \"" + TOKEN + "\"}");
                Log.d(TAG, "Body : " + body);

                Request request = new Request.Builder()
                        .url("https://pure-depths-50816.herokuapp.com/user/emergency")
                        .post(body)
                        .build();

                try {
//                         Log.d(TAG, "before execute");
                    client.newCall(request).execute();
//                         Log.d(TAG, "after execute");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}


