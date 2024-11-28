package es.ucm.deckdraw.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class UserProfile extends AppCompatActivity {

    TUsers cUser;

    EditText usernameET;
    EditText emailET;

    UsersAdmin bd;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CurrentUserManager sessionManager = new CurrentUserManager(this);

        cUser = sessionManager.getCurrentUser();

        toolbar = findViewById(R.id.toolbarUP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bd = new UsersAdmin();


        Button updateB = findViewById(R.id.UpdateButton);
        Button signOut = findViewById(R.id.SignOut);


        usernameET = findViewById(R.id.EditUsername);
        emailET = findViewById(R.id.EditEmail);

        emailET.setEnabled(false);


        emailET.setText(cUser.getEmail());
        usernameET.setText(cUser.getUsername());

        updateB.setOnClickListener((v) ->{
            updateProfile();
        });


        signOut.setOnClickListener((v) ->{
            bd.signOut();
            sessionManager.clearSession();
            Intent i = new Intent(UserProfile.this, LogInActivity.class);
            startActivity(i);
        });

    }

    private void updateProfile(){
        String email = emailET.getText().toString();
        String username = usernameET.getText().toString();

        bd.updateUser(new TUsers(cUser.getIdusers(), username, null, email), new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Toast.makeText(UserProfile.this, getString(R.string.user_updated), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserProfile.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();

            }
        });



    }
}