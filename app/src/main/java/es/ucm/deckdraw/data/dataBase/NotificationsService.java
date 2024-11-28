package es.ucm.deckdraw.data.dataBase;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationsService  extends FirebaseMessagingService {

        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "friend_request_channel")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        UsersAdmin db = new UsersAdmin();
        final String[] uid = new String[1];
        db.getCurrentUser(new Callback<TUsers>() {
            @Override
            public void onSuccess(TUsers data) {
                uid[0] = data.getIdusers();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(uid[0]).child("notifitacionToken").setValue(token);
    }
}
