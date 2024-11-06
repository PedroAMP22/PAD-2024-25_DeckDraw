package es.ucm.deckdraw.data.dataBase;

import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void createAccount(TUsers newUser, Callback<TUsers> callback) {
        String email = newUser.getEmail();
        String password = newUser.getPassword();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUsernameOnBD(user.getUid(), newUser.getUsername(), new Callback<Boolean>() {
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
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        callback.onFailure(task.getException());
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

    private void saveUsernameOnBD(String uid, String username, Callback<Boolean> callback) {
        DatabaseReference userRef = db.getReference("users").child(uid);
        userRef.child("username").setValue(username)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Username Saved");
                    callback.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving username", e);
                    callback.onFailure(e);
                });
    }
}