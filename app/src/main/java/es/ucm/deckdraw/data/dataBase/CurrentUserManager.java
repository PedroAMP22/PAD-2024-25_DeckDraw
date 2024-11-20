package es.ucm.deckdraw.data.dataBase;

import android.content.Context;
import android.content.SharedPreferences;

import es.ucm.deckdraw.data.Objects.users.TUsers;

public class CurrentUserManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_USERNAME = "userUsername";


    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public CurrentUserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(TUsers user) {
        editor.putString(KEY_USER_ID, user.getIdusers());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_USERNAME, user.getUsername());
        editor.apply();
    }

    public TUsers getCurrentUser() {
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        String userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
        String userUsername = sharedPreferences.getString(KEY_USER_USERNAME, null);

        if (userId != null && userEmail != null) {
            return new TUsers(userId, userUsername, null, userEmail);
        }
        return null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
