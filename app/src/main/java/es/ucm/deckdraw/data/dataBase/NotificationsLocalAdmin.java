package es.ucm.deckdraw.data.dataBase;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.util.Callback;

public class NotificationsLocalAdmin {

    private final CurrentUserManager session;

    public NotificationsLocalAdmin(Context context) {
        this.session = new CurrentUserManager(context);
    }

    public void newFriendReq(String fromUid, String toUid, Callback<Boolean> callback) {
        TUsers user = session.getCurrentUser();

        if (!user.getFriendsSend().contains(toUid)) {
            user.getFriendsSend().add(toUid);
            session.saveUserSession(user);
        }

        if (!user.getFriendsRequest().contains(fromUid)) {
            user.getFriendsRequest().add(fromUid); // Simula que el otro usuario recibió la solicitud
            session.saveUserSession(user);
        }

        callback.onSuccess(true);
    }

    public void showPendingNotifications(String uid, Callback<List<String>> callback) {
        TUsers user = session.getCurrentUser();
        callback.onSuccess(new ArrayList<>(user.getFriendsRequest()));
    }

    public void acceptFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        TUsers user = session.getCurrentUser();

        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);
        }
        user.getFriendsRequest().remove(friend);
        user.getFriendsSend().remove(friend);
        session.saveUserSession(user);
        callback.onSuccess(true);
    }

    public void rejectFriendRequest(String currentUser, String friend, Callback<Boolean> callback) {
        TUsers user = session.getCurrentUser();
        user.getFriendsRequest().remove(friend);
        user.getFriendsSend().remove(friend);
        session.saveUserSession(user);
        callback.onSuccess(true);
    }

    public void deleteFriend(String currentUser, String friend, Callback<Boolean> callback) {
        TUsers user = session.getCurrentUser();
        user.getFriends().remove(friend);
        session.saveUserSession(user);
        callback.onSuccess(true);
    }
}
