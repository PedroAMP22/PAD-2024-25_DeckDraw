package es.ucm.deckdraw.data.Objects.users;
public class FriendReq {


    private String from;
    private long timestamp;

    public FriendReq() {}

    public FriendReq(String from, long timestamp) {
        this.from = from;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
