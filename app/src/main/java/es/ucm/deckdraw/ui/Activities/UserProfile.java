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
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class UserProfile extends AppCompatActivity {

    SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

    TUsers cUser;

    EditText passwordET;
    EditText usernameET;
    EditText emailET;

    UsersAdmin bd;

    boolean isPasswordVisible = false;
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

        sharedViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                cUser = user;
            }
        });

        bd = new UsersAdmin();

        Toolbar tl = findViewById(R.id.toolbarUP);
        tl.setBackInvokedCallbackEnabled(true);

        Button updateB = findViewById(R.id.UpdateButton);
        Button signOut = findViewById(R.id.SignOut);

        passwordET = findViewById(R.id.EditPassword);
        usernameET = findViewById(R.id.EditUsername);
        emailET = findViewById(R.id.EditEmail);

        ImageView toggle = findViewById(R.id.togglePasswordVisibilityUP);
        toggle.setOnClickListener((v) -> {
            if (isPasswordVisible) {
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggle.setImageResource(R.drawable.ic_visibility); //Reveal password icon
            } else {
                passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggle.setImageResource(R.drawable.ic_visibility_off); //Hide password icon
            }
            passwordET.setSelection(passwordET.length());
            isPasswordVisible = !isPasswordVisible;
        });


        passwordET.setText(cUser.getPassword());
        emailET.setText(cUser.getEmail());
        usernameET.setText(cUser.getUsername());

        updateB.setOnClickListener((v) ->{
            updateProfile();
        });


        signOut.setOnClickListener((v) ->{
            bd.signOut();
            sharedViewModel.setCurrentUser(null);

            Intent i = new Intent(UserProfile.this, LogInActivity.class);
            startActivity(i);
        });

    }

    private void updateProfile(){
        String password = passwordET.getText().toString();
        String email = emailET.getText().toString();
        String username = usernameET.getText().toString();

        bd.updateUser(new TUsers(cUser.getIdusers(), username, password, email), new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Toast.makeText(UserProfile.this, "User updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });



    }
}