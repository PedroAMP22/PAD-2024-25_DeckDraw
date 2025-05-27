package es.ucm.deckdraw.data.dataBase;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.util.Callback;

public class DecksLocalAdmin {
    private static final String FILE_PREFIX = "decks_";
    private final Context context;
    private final Gson gson = new Gson();

    public DecksLocalAdmin(Context context) {
        this.context = context;
    }

    private String getFileName(String userId) {
        return FILE_PREFIX + userId + ".json";
    }

    public void createDeck(String uid, TDecks newDeck) {
        List<TDecks> decks = getUserDecksInternal(uid);
        newDeck.setIdDeck("deck_" + System.currentTimeMillis());
        decks.add(newDeck);
        saveDecks(uid, decks);
    }

    public void updateDeck(TDecks deck, Callback<Boolean> callback) {
        List<TDecks> decks = getUserDecksInternal(deck.getDeckOwner());
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getIdDeck().equals(deck.getIdDeck())) {
                decks.set(i, deck);
                saveDecks(deck.getDeckOwner(), decks);
                callback.onSuccess(true);
                return;
            }
        }
        callback.onSuccess(false);
    }

    public void deleteDeck(TDecks deck, Callback<Boolean> callback) {
        List<TDecks> decks = getUserDecksInternal(deck.getDeckOwner());
        boolean removed = decks.removeIf(d -> d.getIdDeck().equals(deck.getIdDeck()));
        saveDecks(deck.getDeckOwner(), decks);
        callback.onSuccess(removed);
    }

    public void getUserDecks(String userId, Callback<List<TDecks>> callback) {
        callback.onSuccess(getUserDecksInternal(userId));
    }

    private void saveDecks(String uid, List<TDecks> decks) {
        try {
            String json = gson.toJson(decks);
            FileOutputStream fos = context.openFileOutput(getFileName(uid), Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<TDecks> getUserDecksInternal(String uid) {
        try {
            FileInputStream fis = context.openFileInput(getFileName(uid));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            fis.close();

            Type type = new TypeToken<List<TDecks>>() {}.getType();
            List<TDecks> decks = gson.fromJson(builder.toString(), type);
            return decks != null ? decks : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
