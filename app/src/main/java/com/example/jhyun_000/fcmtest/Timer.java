package com.example.jhyun_000.fcmtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.jhyun_000.fcmtest.Constants.server_url_timer;
import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-02-23.
 */

public class Timer extends Service {
    public static CountDownTimer countDownTimer;
    NotificationManager mNotificationManager;

//    minute 분
    int expire_time;
    int interval_time;

    //startService :  onCreate()->onStartCommand()-> onDestroy()
    //bindService : onCreate()->onBind()-> onUnbind()-> onDestroy()
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Timer", "OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        expire_time = (int) bundle.get("expire_time");
        interval_time = (int) bundle.get("interval_time");

        registerTimer(interval_time, expire_time);
        countDownTimer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void registerTimer(int interval, int expire) {
//        countDownTimer = new CountDownTimer(1000000, 4000) {
//        milis second = 1/1000 sec

        countDownTimer = new CountDownTimer(expire*1000*60, interval*1000*60) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("Timer", String.valueOf(millisUntilFinished));
                sendNotification("Timer tick");
            }

            @Override
            public void onFinish() {
//                sendMsgToActivity(longitude, latitude);\

//              서버로 만료 전송
//                server_url_timer

                JSONObject Jobject = new JSONObject();
                try {
                    Jobject.put("email", user_email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = Jobject.toString();
                CallRequestHttp callRequestHttp = new CallRequestHttp();
                callRequestHttp.execute(server_url_timer,json);

                MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                dbHandler.deleteAll();
            }
        };

        Log.d("Timer", "Timer started");
    }

    private void sendNotification(String messageBody) {
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = getString(R.string.timer_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(getString(R.string.timer_notification_channel_id), name, importance);
// Configure the notification channel.
            mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(Timer.this, TimerActionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(Timer.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Timer")
                .setContentText("Timer")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setChannelId(getString(R.string.timer_notification_channel_id))
                .build();

        //sdk Min 26이상부터 setChannelId 함수가능
        //원래 MinSdk 14였는데 26으로 고침

        mNotificationManager.notify(Integer.parseInt(getString(R.string.timer_notification_channel_id)), notification);
        Log.d("Timer", "SendNotification End");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        countDownTimer.cancel();
        Log.d("Timer", "timer canceled");
    }
}

 class CallRequestHttp extends AsyncTask<String, String, String> {
    RequestHttp requestHttp;

    @Override
    protected void onPreExecute() {
        requestHttp = new RequestHttp();
    }

    @Override
    protected String doInBackground(String... url) {
        String res = null;
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
        Log.i("Response-postexecute", s);

//        AlertDialog.Builder builder = new AlertDialog.Builder();
//        builder.setMessage("주소 검증이 완료되었습니다")
//                .setTitle("주소 검증 완료")
//                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //다이얼로그를 취소한다
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

}
