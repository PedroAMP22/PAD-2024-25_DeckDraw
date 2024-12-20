package es.ucm.deckdraw.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;

    public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "UsersAdminActivity";

    private UsersAdmin userService;
    private EditText emailET;
    private EditText passwordET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userService = new UsersAdmin();

        emailET = findViewById(R.id.logInEmailET);
        passwordET = findViewById(R.id.logInPassET);
        Button logInButton = findViewById(R.id.logInButton);
        TextView registerPromptTV = findViewById(R.id.registerPromptTV);





        userService.getCurrentUser(new Callback<TUsers>() {
            public void onSuccess(TUsers user) {
                if(user != null){
                    CurrentUserManager sessionManager = new CurrentUserManager(LogInActivity.this);
                    sessionManager.saveUserSession(user);

                    Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
                    startActivity(i);
                    Log.d(TAG, "User logged: " + user.getIdusers());
                    Toast.makeText(LogInActivity.this, getText(R.string.already_logged), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"User not logged");
            }
        });



        logInButton.setOnClickListener(v -> {


            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            TUsers loginUser = new TUsers(null, null, password, email);

            if(!email.isEmpty() && !password.isEmpty()) {
                userService.signIn(loginUser, new Callback<TUsers>() {
                    @Override
                    public void onSuccess(TUsers user) {
                        CurrentUserManager sessionManager = new CurrentUserManager(LogInActivity.this);
                        sessionManager.saveUserSession(user);

                        Intent i = new Intent(LogInActivity.this, MainScreenActivity.class);
                        startActivity(i);
                        Log.d(TAG, "User logged: " + user.getIdusers());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(LogInActivity.this, getString(R.string.error_couldnt_login), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error logging in", e);
                    }
                });
            }
        });

        registerPromptTV.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}