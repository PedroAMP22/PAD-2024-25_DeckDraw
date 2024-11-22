package es.ucm.deckdraw.ui.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.util.Callback;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, usernameEditText;
    //private ImageView togglePasswordVisibility;
    private UsersAdmin usersAdmin;

    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SPECIAL_CHAR = Pattern.compile(".*[@#$%^&+=._].*");
    private static final int PASSWORD_MIN_LENGTH = 8;

    private TextView criteriaLength, criteriaUppercase, criteriaLowercase, criteriaDigit, criteriaSpecialChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        Button registerButton = findViewById(R.id.registerButton);

        criteriaLength = findViewById(R.id.criteriaLength);
        criteriaUppercase = findViewById(R.id.criteriaUppercase);
        criteriaLowercase = findViewById(R.id.criteriaLowercase);
        criteriaDigit = findViewById(R.id.criteriaDigit);
        criteriaSpecialChar = findViewById(R.id.criteriaSpecialChar);

        usersAdmin = new UsersAdmin();

        //togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());
        passwordEditText.addTextChangedListener(passwordWatcher);

        registerButton.setOnClickListener(v -> registerUser());
    }

    /*
    //Codigo de Alavaro RIP T-T
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_show); //Reveal password icon
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off); //Hide password icon
        }
        passwordEditText.setSelection(passwordEditText.length());
        isPasswordVisible = !isPasswordVisible;
    }
    */


    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updatePasswordCriteria(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void updatePasswordCriteria(String password) {
        criteriaLength.setTextColor(password.length() >= PASSWORD_MIN_LENGTH ? getColor(R.color.SuccessColor) : getColor(R.color.TextColorSecondary));
        criteriaUppercase.setTextColor(PASSWORD_UPPERCASE.matcher(password).matches() ? getColor(R.color.SuccessColor) : getColor(R.color.TextColorSecondary));
        criteriaLowercase.setTextColor(PASSWORD_LOWERCASE.matcher(password).matches() ? getColor(R.color.SuccessColor) : getColor(R.color.TextColorSecondary));
        criteriaDigit.setTextColor(PASSWORD_DIGIT.matcher(password).matches() ? getColor(R.color.SuccessColor) : getColor(R.color.TextColorSecondary));
        criteriaSpecialChar.setTextColor(PASSWORD_SPECIAL_CHAR.matcher(password).matches() ? getColor(R.color.SuccessColor) : getColor(R.color.TextColorSecondary));
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        TUsers newUser = new TUsers();

        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(username);

        usersAdmin.createAccount(newUser, new Callback<TUsers>() {
            @Override
            public void onSuccess(TUsers result) {
                Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RegisterActivity", "Error en el registro", e);
                Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
