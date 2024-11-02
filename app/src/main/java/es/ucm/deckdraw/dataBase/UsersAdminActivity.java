package es.ucm.deckdraw.dataBase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.ucm.deckdraw.users.TUsers;

public class UsersAdminActivity extends Activity {

    private static final String TAG = "UsersAdmin";

    private FirebaseAuth mAuth;

    private FirebaseDatabase db;

    private boolean success;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }


    private void createAccount(TUsers newUser) {

        String email = newUser.getEmail();
        String password = newUser.getPassword();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;

                        success = saveUsernameOnBD(user.getUid(), newUser.getUsername() );

                        newUser.setIdusers(user.getUid());
                        updateUI(newUser);
                    } else {


                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(UsersAdminActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

    }

    private void signIn(TUsers newUser) {

        String email = newUser.getEmail();
        String password = newUser.getPassword();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        newUser.setIdusers(user.getUid());
                        updateUI(newUser);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(UsersAdminActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                });
    }

    private boolean saveUsernameOnBD(String uid, String username){
        DatabaseReference userRef = db.getReference("users").child(uid);
        final boolean[] result = new boolean[1];
        userRef.child("username").setValue(username)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Username Saved");
                    result[0] = true;
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error while saving username", e);
                    result[0] = false;
                });

        return result[0];
    }

    //methods to update the UI
    private void reload() { }

    private void updateUI(TUsers user) {

    }


}
