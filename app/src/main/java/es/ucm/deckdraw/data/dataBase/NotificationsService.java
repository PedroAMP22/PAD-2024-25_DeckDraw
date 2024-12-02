package es.ucm.deckdraw.data.dataBase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.ucm.deckdraw.MainActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationsService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "friend_request_channel";
    private static final String TAG = "NotificationService";

    static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Friend request channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Friend request notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();

            Intent i = new Intent(this, MainActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent notificationPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            NOTIFICATION_ID,
                            i,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_image)
                    .setContentTitle(title)
                    .setContentIntent(notificationPendingIntent)
                    .setAutoCancel(true);
            UsersAdmin ua = new UsersAdmin();
            ua.getCurrentUser(new Callback<TUsers>() {
                @Override
                public void onSuccess(TUsers data) {
                    if(!data.getFriendsRequest().isEmpty()){

                        String message = "You have " + data.getFriendsRequest().size()+ " pending friends requests!";

                        mBuilder.setContentText(message);

                        Notification notification = mBuilder.build();

                        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        mNotifyManager.notify(NOTIFICATION_ID, notification);
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });


        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        UsersAdmin db = new UsersAdmin();
        db.getCurrentUser(new Callback<TUsers>() {
            @Override
            public void onSuccess(TUsers data) {

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("users").child(data.getIdusers()).child("notifitacionToken").setValue(token);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });




    }
}
