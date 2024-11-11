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
import es.ucm.deckdraw.util.Callback;

public class CardLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<TCard>> {

    public static final String ARG_NAME = "name";
    public static final String ARG_FORMAT = "format";
    public static final String ARG_COLORS = "colors";
    public static final String ARG_TYPE = "type";
    public static final String ARG_RARITY = "rarity";


    private final Context context;
    private final Callback<List<TCard>> callback;

    public CardLoaderCallbacks(Context context, Callback<List<TCard>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Loader<List<TCard>> onCreateLoader(int id, @Nullable Bundle args) {
        String name = args != null ? args.getString(ARG_NAME, "") : "";
        String format = args != null ? args.getString(ARG_FORMAT, "") : "";
        List<String> colors = args != null ? args.getStringArrayList(ARG_COLORS) : null;
        List<String> types = args != null ? args.getStringArrayList(ARG_TYPE) : null;
        List<String> rarity = args != null ? args.getStringArrayList(ARG_RARITY) : null;


        return new CardLoader(context, name, format, colors, types,rarity);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<TCard>> loader, List<TCard> data) {
        if (data != null && !data.isEmpty()) {
            callback.onSuccess(data); // Devuelve los resultados a través de onSuccess
        } else {
            callback.onFailure(new Exception("No se encontraron resultados"));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<TCard>> loader) {
        // Implementar si es necesario
    }
}
