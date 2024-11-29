package es.ucm.deckdraw.data.dataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;

public class CurrentUserManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_USERNAME = "userUsername";
    private static final String KEY_USER_FRIENDS = "friends";
    private static final String KEY_USER_RECEIVED_REQUESTS = "receivedRequests";
    private static final String KEY_USER_SENT_REQUESTS = "sentRequests";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public CurrentUserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        UsersAdmin userA = new UsersAdmin();
        userA.getCurrentUser(new Callback<TUsers>() {
            @Override
            public void onSuccess(TUsers data) {
                saveUserSession(data);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    public void saveUserSession(TUsers user) {
        editor.putString(KEY_USER_ID, user.getIdusers());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_USERNAME, user.getUsername());

        saveStringList(KEY_USER_FRIENDS, user.getFriends());
        saveStringList(KEY_USER_RECEIVED_REQUESTS, user.getFriendsRequest());
        saveStringList(KEY_USER_SENT_REQUESTS, user.getFriendsSend());

        editor.apply();
    }

    public TUsers getCurrentUser() {
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        String userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
        String userUsername = sharedPreferences.getString(KEY_USER_USERNAME, null);

        if (userId != null && userEmail != null) {
            List<String> friends = getStringList(KEY_USER_FRIENDS);
            List<String> receivedRequests = getStringList(KEY_USER_RECEIVED_REQUESTS);
            List<String> sentRequests = getStringList(KEY_USER_SENT_REQUESTS);

            TUsers user = new TUsers(userId, userUsername,  "",  userEmail);
            user.setFriendsSend(sentRequests);
            user.setFriends(friends);
            user.setFriendsRequest(receivedRequests);
            return user;

        }
        return null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    private void saveStringList(String key, List<String> list) {
        String json = gson.toJson(list);
        editor.putString(key, json);
    }

    private List<String> getStringList(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
