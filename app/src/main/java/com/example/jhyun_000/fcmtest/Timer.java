package com.example.jhyun_000.fcmtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jhyun_000 on 2018-02-23.
 */

public class Timer extends Service {
    public static CountDownTimer countDownTimer;
    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {

        super.onCreate();

        Log.d("Timer", "OnCreate");
        registerTimer();
        countDownTimer.start();

    }


    private void registerTimer() {
        countDownTimer = new CountDownTimer(1000000, 4000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("Timer", String.valueOf(millisUntilFinished));
                sendNotification("Timer tick");
            }

            @Override
            public void onFinish() {
//                sendMsgToActivity(longitude, latitude);\
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
