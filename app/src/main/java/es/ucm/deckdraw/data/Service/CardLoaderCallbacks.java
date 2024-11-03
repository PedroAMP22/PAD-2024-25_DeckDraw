package es.ucm.deckdraw.data.Service;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.Fragment.CardSearchFragment;

public class CardLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<TCard>> {

    public static final String ARG_NAME = "name";
    public static final String ARG_FORMAT = "format";
    public static final String ARG_COLORS = "colors";
    public static final String ARG_TYPE = "type";

    private final Context context;
    private final Callback callback;


    public interface Callback {
        void onCardsLoaded(List<TCard> data);
    }

    public CardLoaderCallbacks(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Loader<List<TCard>> onCreateLoader(int id, @Nullable Bundle args) {
        String name = args != null ? args.getString(ARG_NAME, "") : "";
        String format = args != null ? args.getString(ARG_FORMAT, "") : "";
        List<String> colors = args != null ? args.getStringArrayList(ARG_COLORS) : null;
        String type = args != null ? args.getString(ARG_TYPE, "") : "";

        return new CardLoader(context, name, format, colors, type);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<TCard>> loader, List<TCard> data) {
        // Llamar al método onCardsLoaded en la clase que implementa Callback
        if (callback != null) {
            callback.onCardsLoaded(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<TCard>> loader) {
        // Implementar si es necesario
    }
}
