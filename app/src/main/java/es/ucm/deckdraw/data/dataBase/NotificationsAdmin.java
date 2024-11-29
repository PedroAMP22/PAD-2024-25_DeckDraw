package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;

import es.ucm.deckdraw.util.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotificationsAdmin {

    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    private static final String TAG = "NOTIFICATIONS_ADMIN";

    public NotificationsAdmin() {
        this.db = FirebaseDatabase.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public void newFriendReq(String fromUid, String toUid, Callback<Boolean> callback) {
        dbRef.child("users").child(fromUid).child("sentRequests").child(toUid).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbRef.child("users").child(toUid).child("receivedRequests").child(fromUid).setValue(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        callback.onSuccess(true);
                                    } else {
                                        callback.onFailure(task1.getException());
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void showPendingNotifications(String uid, Callback<List<String>> callback) {
        dbRef.child("users").child(uid).child("receivedRequests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> receivedRequests = new ArrayList<>();
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            receivedRequests.add(requestSnapshot.getKey());
                        }
                        callback.onSuccess(receivedRequests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.toException());
                    }
                });
    }

    public void acceptFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        dbRef.child("users").child(currentUser).child("friends").child(friend).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbRef.child("users").child(friend).child("friends").child(currentUser).setValue(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        dbRef.child("users").child(currentUser).child("receivedRequests").child(friend).removeValue()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        dbRef.child("users").child(friend).child("sentRequests").child(currentUser).removeValue()
                                                                .addOnCompleteListener(task3 -> {
                                                                    if (task3.isSuccessful()) {
                                                                        callback.onSuccess(true);
                                                                    } else {
                                                                        callback.onFailure(task3.getException());
                                                                    }
                                                                });
                                                    } else {
                                                        callback.onFailure(task2.getException());
                                                    }
                                                });
                                    } else {
                                        callback.onFailure(task1.getException());
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void rejectFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        dbRef.child("users").child(currentUser).child("receivedRequests").child(friend).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbRef.child("users").child(friend).child("sentRequests").child(currentUser).removeValue()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        callback.onSuccess(true);
                                    } else {
                                        callback.onFailure(task1.getException());
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void deleteFriend(String currentUser, String friend, Callback<Boolean> callback) {
        dbRef.child("users").child(currentUser).child("friends").child(friend).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbRef.child("users").child(friend).child("friends").child(currentUser).removeValue()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        callback.onSuccess(true);
                                    } else {
                                        callback.onFailure(task1.getException());
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void sendNotification(String receiverToken, String senderId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(senderId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String senderName = snapshot.getValue(String.class);

                        JSONObject notificationData = new JSONObject();
                        try {
                            notificationData.put("to", receiverToken);
                            JSONObject notificationBody = new JSONObject();
                            notificationBody.put("title", "New Friend Request");
                            notificationBody.put("body", senderName + " wants to be your friend");

                            notificationData.put("notification", notificationBody);

                            sendNotificationUsingOkHttp(notificationData);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Notification", "Error obtaining the sender name", error.toException());
                    }
                });
    }

    private void sendNotificationUsingOkHttp(JSONObject notificationData) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();


            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    notificationData.toString()
            );


            String FCM_SEND_ENDPOINT = "/v1/projects/deckdraw-90c8d/messages:send";
            URL url = null;
            try {
                url = new URL("https://fcm.googleapis.com" + FCM_SEND_ENDPOINT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + getServiceAccountAccessToken());
                httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification sent successfully");
                } else {
                    Log.e("Notification", "Error sending notification: " + response.code());
                }
            } catch (IOException e) {
                Log.e("Notification", "Error sending notification", e);
            }
        }).start();
    }

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("service-account.json"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
