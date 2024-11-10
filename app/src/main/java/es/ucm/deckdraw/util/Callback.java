package es.ucm.deckdraw.util;

public interface Callback<T> {

    void onSuccess(T data);
    void onFailure(Exception e);
}