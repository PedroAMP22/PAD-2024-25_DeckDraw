package es.ucm.deckdraw.data.dataBase;

import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;


public class UsersAdmin{

    private static final String TAG = "UserService";
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase db;

    public UsersAdmin() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance();
    }

    public void getCurrentUser(Callback<TUsers> callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DatabaseReference userRef = db.getReference("users").child(uid);

            userRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    TUsers user = task.getResult().getValue(TUsers.class);
                    user.setIdusers(uid);
                    user.setEmail(firebaseUser.getEmail());
                    List<String> friends = new ArrayList<>();
                    userRef.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot friendsSnapshot : snapshot.getChildren()) {
                                String friendsUids = friendsSnapshot.getKey();
                                friends.add(friendsUids);
                            }
                            user.setFriends(friends);

                            List<String> req = new ArrayList<>();
                            userRef.child("receivedRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot friendsSnapshot : snapshot.getChildren()) {
                                        String reqUids = friendsSnapshot.getKey();
                                        req.add(reqUids);
                                    }
                                    user.setFriendsRequest(req);

                                    List<String> send = new ArrayList<>();

                                    userRef.child("sentRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot friendsSnapshot : snapshot.getChildren()) {
                                                String sendUids = friendsSnapshot.getKey();
                                                send.add(sendUids);
                                            }
                                            user.setFriendsSend(send);
                                            callback.onSuccess(user);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            user.setFriendsSend(send);
                                            callback.onSuccess(user);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    user.setFriendsRequest(req);

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            user.setFriends(friends);
                        }
                    });

                }
                else{
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    callback.onFailure(task.getException());
                }

            });
        } else {
            Log.w(TAG, "No current user logged in");
            callback.onFailure(new Exception("No current user logged in"));
        }
    }

    public void createAccount(TUsers newUser, Callback<TUsers> callback) {
        String email = newUser.getEmail();
        String password = newUser.getPassword();
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        token[0] = task.getResult();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            newUser.setNotifitacionToken(token[0]);
                                            saveUsernameOnBD(user.getUid(), newUser, new Callback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    newUser.setIdusers(user.getUid());
                                                    callback.onSuccess(newUser);
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    callback.onFailure(e);
                                                }
                                            });
                                        }
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                        callback.onFailure(task1.getException());
                                    }
                                });
                    }
                });


    }

    public void signIn(TUsers user, Callback<TUsers> callback) {
        String email = user.getEmail();
        String password = user.getPassword();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            user.setIdusers(currentUser.getUid());
                            callback.onSuccess(user);
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }

    private void saveUsernameOnBD(String uid, TUsers user, Callback<Boolean> callback) {
        DatabaseReference userRef = db.getReference("users").child(uid);
        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Username Saved");
                    callback.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving username", e);
                    callback.onFailure(e);
                });
    }

    public void updateUser(TUsers user, Callback<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        if (user.getUsername() != null) {
            updates.put("username", user.getUsername());
        }

        db.getReference("users")
                .child(user.getIdusers())
                .updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }


    public void getUserByUid(String uid, Callback<TUsers> callback){

        DatabaseReference userRef = db.getReference("users").child(uid);

        userRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                TUsers user = task.getResult().getValue(TUsers.class);
                callback.onSuccess(user);
            }
            else{
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                callback.onFailure(task.getException());
            }

        });

    }

    public void signOut(){
        mAuth.signOut();
    }
}