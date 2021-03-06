package com.skripsi.chatapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.skripsi.chatapp.FirebaseChatMainApp;
import com.skripsi.chatapp.R;
import com.skripsi.chatapp.events.PushNotificationEvent;
import com.skripsi.chatapp.ui.activities.ChatActivity;
import com.skripsi.chatapp.ui.activities.UserListingActivity;
import com.skripsi.chatapp.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when messageFrom is received.
     *
     * @param remoteMessage Object representing the messageFrom received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if messageFrom contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("username");
            String uid = remoteMessage.getData().get("uid");
            String rsaPublicKeyTo = remoteMessage.getData().get("receiverRsaPublicKey");
            String rsaPrivateKeyTo = remoteMessage.getData().get("receverRsaPrivateKey");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            String nama = remoteMessage.getData().get("nama");
            String firebaseToken = remoteMessage.getData().get("firebaseToken");


            // Don't show notification if chat activity is open.
            if (!FirebaseChatMainApp.isChatActivityOpen()) {
                sendNotification(nama,
                        message,
                        username,
                        uid,
                        rsaPublicKeyTo,
                        rsaPrivateKeyTo,
                        fcmToken,
                        nama,
                        firebaseToken);
            } else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        username,
                        uid,
                        fcmToken,
                        firebaseToken));
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM messageFrom.
     */
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String rsaPublicKeyTo,
                                  String rsaPrivateKeyTo,
                                  String fcmToken,
                                  String name,
                                  String firebaseToken) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_RECEIVER_RSAPUBLICKEY, rsaPublicKeyTo);
        intent.putExtra(Constants.ARG_RECEIVER_RSAPRIVATEKEY, rsaPrivateKeyTo);
        intent.putExtra(Constants.ARG_FCM_TOKEN, fcmToken);
        intent.putExtra(Constants.ARG_NAME, name);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.chat_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}