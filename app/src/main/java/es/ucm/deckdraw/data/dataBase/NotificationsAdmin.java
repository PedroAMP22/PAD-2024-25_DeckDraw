package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import es.ucm.deckdraw.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdmin {

    FirebaseDatabase db;
    DatabaseReference dbRef;

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

                        RemoteMessage notificationMessage = new RemoteMessage.Builder(receiverToken)
                                .addData("title", "New Friend Request")
                                .addData("body", senderName + " wants to be your friend")
                                .build();

                        FirebaseMessaging.getInstance().send(notificationMessage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Notification", "Error obtaining the receiver name", error.toException());
                    }
                });
    }
}
