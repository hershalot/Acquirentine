package com.fenapnu.acquirentine_android;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

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

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        long badges;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();




            String title = data.get("title");
            String messageBody = data.get("body");


            if(title == null){
                title = remoteMessage.getNotification().getTitle();
            }

            if(messageBody == null){
                messageBody = remoteMessage.getNotification().getBody();
            }

            // Check if message contains a notification payload




                notificationUpdateActivity(this.getApplicationContext(), messageBody, title);
                sendNotification(messageBody, title);


        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


    // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    // [END receive_message]



    /*
      Schedule a job using FirebaseJobDispatcher.
     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
//    private void handleNow() {
//        Log.d(TAG, "Short lived task is done.");
//
//    }



    static void notificationUpdateActivity(Context context, String message, String title) {

        Intent intent = new Intent("notification");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("title", title);

        //send broadcast
        context.sendBroadcast(intent);
    }




    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received
     */
    private void sendNotification(String messageBody, String title) {

        Log.d(TAG, "Send Local Notification");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                0);
        long notifId = DataManager.nowInSeconds();


        int GROUP_ID = 1;
        String channelId = getString(R.string.message_notification_channel_id);
        String channelName = getString(R.string.message_notification_channel_name);
        String  Default_Message_Group = "com.fenapnu..acquirentine_android.default_message";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        boolean background = MyApplication.get().getBackground();
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    IMPORTANCE_HIGH);

            channel.shouldVibrate();
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);
        }



        NotificationCompat.Builder notificationBuilder;
        if(!background){

            notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setTimeoutAfter(3000)
                            .setContentIntent(pendingIntent)
                            .setShowWhen(true)
                            .setGroup(Default_Message_Group)
                            .setPriority(IMPORTANCE_HIGH);

        }else {


            notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setGroup(Default_Message_Group)
                            .setShowWhen(true)
                            .setPriority(IMPORTANCE_HIGH);
        }
        Notification notification = notificationBuilder.build();

        notificationManager.notify((int)notifId, notification );

    }

}
