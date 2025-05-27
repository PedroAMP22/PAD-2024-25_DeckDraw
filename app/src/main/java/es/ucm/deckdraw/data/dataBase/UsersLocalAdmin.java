package es.ucm.deckdraw.data.dataBase;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;

public class UsersLocalAdmin {
    private static final String FILE_NAME = "users.json";
    private final Context context;
    private final Gson gson = new Gson();

    public UsersLocalAdmin(Context context) {
        this.context = context;
    }

    private List<TUsers> loadUsers() {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            fis.close();

            Type type = new TypeToken<List<TUsers>>() {}.getType();
            List<TUsers> users = gson.fromJson(builder.toString(), type);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<TUsers> users) {
        try {
            String json = gson.toJson(users);
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAccount(TUsers newUser, Callback<TUsers> callback) {
        List<TUsers> users = loadUsers();
        for (TUsers u : users) {
            if (u.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                callback.onFailure(new Exception("User already exists"));
                return;
            }
        }

        String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
        newUser.setPassword(hashed);
        newUser.setIdusers(UUID.randomUUID().toString());
        users.add(newUser);
        saveUsers(users);
        callback.onSuccess(newUser);
    }

    public void signIn(TUsers loginUser, Callback<TUsers> callback) {
        List<TUsers> users = loadUsers();
        for (TUsers u : users) {
            if (u.getEmail().equalsIgnoreCase(loginUser.getEmail())
                    && u.checkPassword(loginUser.getPassword())) {
                callback.onSuccess(u);
                return;
            }
        }
        callback.onFailure(new Exception("Incorrect email or password"));
    }

    public void getCurrentUser(Callback<TUsers> callback) {
        CurrentUserManager session = new CurrentUserManager(context);
        TUsers user = session.getCurrentUser();
        if (user != null) {
            callback.onSuccess(user);
        } else {
            callback.onFailure(new Exception("No user logged in"));
        }
    }

    public void getUserByUid(String uid, Callback<TUsers> callback) {
        List<TUsers> users = loadUsers();
        for (TUsers u : users) {
            if (u.getIdusers().equals(uid)) {
                callback.onSuccess(u);
                return;
            }
        }
        callback.onFailure(new Exception("User not found"));
    }

    public void updateUser(TUsers updatedUser, Callback<Boolean> callback) {
        List<TUsers> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getIdusers().equals(updatedUser.getIdusers())) {
                users.set(i, updatedUser);
                saveUsers(users);
                callback.onSuccess(true);
                return;
            }
        }
        callback.onFailure(new Exception("User not found"));
    }

    public void signOut() {
        new CurrentUserManager(context).clearSession();
    }
}
