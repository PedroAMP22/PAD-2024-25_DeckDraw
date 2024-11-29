package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.util.Callback;

public class NotificationsAdmin {

    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    private static final String TAG = "NOTIFICATIONS_ADMIN";

    public NotificationsAdmin() {
        this.db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();
    }

    // Nueva solicitud de amistad
    public void newFriendReq(String fromUid, String toUid, Callback<Boolean> callback) {
        // Agregar la solicitud en el usuario que envía la solicitud
        dbRef.child("users").child(fromUid).child("sentRequests").child(toUid).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Agregar la solicitud en el usuario que recibe la solicitud
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

    // Mostrar las notificaciones pendientes
    public void showPendingNotifications(String uid, Callback<List<String>> callback) {
        dbRef.child("users").child(uid).child("receivedRequests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        List<String> receivedRequests = new ArrayList<>();
                        for (com.google.firebase.database.DataSnapshot requestSnapshot : snapshot.getChildren()) {
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

    // Aceptar una solicitud de amistad
    public void acceptFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        // Agregar al amigo en la lista de amigos del usuario actual
        dbRef.child("users").child(currentUser).child("friends").child(friend).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Agregar al usuario actual en la lista de amigos del amigo
                        dbRef.child("users").child(friend).child("friends").child(currentUser).setValue(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Eliminar la solicitud recibida del usuario actual
                                        dbRef.child("users").child(currentUser).child("receivedRequests").child(friend).removeValue()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        // Eliminar la solicitud enviada del amigo
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

    // Rechazar una solicitud de amistad
    public void rejectFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        // Eliminar la solicitud recibida del usuario actual
        dbRef.child("users").child(currentUser).child("receivedRequests").child(friend).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Eliminar la solicitud enviada del amigo
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

    // Eliminar a un amigo
    public void deleteFriend(String currentUser, String friend, Callback<Boolean> callback) {
        // Eliminar al amigo de la lista de amigos del usuario actual
        dbRef.child("users").child(currentUser).child("friends").child(friend).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Eliminar al usuario actual de la lista de amigos del amigo
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
}
