package com.example.jhyun_000.fcmtest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by jhyun_000 on 2018-02-09.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //foreground process 위해 추가
//            sendNotification이 호출되지 않음에도 백그라운드에서 알림이 표시되는 이유는?
//            그건 "알림 메시지"을 처리하는 주체가 안드로이드 운영체제이기 때문이다.
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getData().get("message"));
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


//            if (remoteMessage.getNotification().getTag().toString().equals("SHOW_LIST")) {
//                String click_action = remoteMessage.getNotification().getClickAction();
//                Intent intent = new Intent("OPEN");
////            Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra("num1", remoteMessage.getData().toString());
//                startActivity(intent);
//            }
            if (remoteMessage.getNotification().getTag().toString().equals("SHOW_VISITOR")) {
                String click_action = remoteMessage.getNotification().getClickAction();
//                Intent intent = new Intent(click_action);
                Intent intent = new Intent(this, ViewVisitor.class);
                String uuid = remoteMessage.getData().get("uuid");
                String result = remoteMessage.getData().get("result");

                intent.putExtra("uuids", uuid);
                intent.putExtra("result", result);
                startActivity(intent);
            } else if (remoteMessage.getNotification().getTag().toString().equals("SHOW_USER")) {       //사용자
                Intent intent = new Intent(this, ViewUser.class);
                startActivity(intent);
//                YesNoDialog(getApplicationContext(), "사용자", "귀가 알림을 해제 하시겠습니까?");

            } else if (remoteMessage.getNotification().getTag().toString().equals("SHOW_EMERGENCY")) {       //도움요청 옴
                String latitude = remoteMessage.getData().get("latitude");
                String longitude = remoteMessage.getData().get("longitude");

                Intent intent = new Intent(this, DeliveredHelp.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //sendNorification 호출 추가 - foreground
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
