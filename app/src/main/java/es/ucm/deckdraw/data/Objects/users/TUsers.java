package es.ucm.deckdraw.data.Objects.users;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties

public class TUsers {

    private String idusers;
    private String username;

    private String notifitacionToken;
    private List<String> friends = new ArrayList<>();

    private List<String> friendsRequest = new ArrayList<>();
    private List<String> friendsSend = new ArrayList<>();

    @Exclude
    private String password; //Not plain text
    @Exclude
    private String email;



    //Create a user when reading it from DB, the password is already encrypted
    public TUsers(String idusers, String username, String hashesPassword, String email){
        this.idusers = idusers;
        this.username = username;
        this.password =hashesPassword;
        this.email = email;

    }


    public TUsers() {}


    public String getIdusers() {
        return idusers;
    }

    public void setIdusers(String idusers) {
        this.idusers = idusers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setNotifitacionToken(String notifitacionToken) {
        this.notifitacionToken = notifitacionToken;
    }

    public String getNotifitacionToken() {
        return notifitacionToken;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFriendsRequest() {
        return friendsRequest;
    }

    public void setFriendsRequest(List<String> friendsRequest) {
        this.friendsRequest = friendsRequest;
    }

    public List<String> getFriendsSend() {
        return friendsSend;
    }

    public void setFriendsSend(List<String> friendsSend) {
        this.friendsSend = friendsSend;
    }
}
