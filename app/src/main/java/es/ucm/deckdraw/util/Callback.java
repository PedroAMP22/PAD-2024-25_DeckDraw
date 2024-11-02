package es.ucm.deckdraw.util;

public interface Callback<T> {

    void onSuccess(T user);
    void onFailure(Exception e);
}