package es.ucm.deckdraw.data.Service;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;
import es.ucm.deckdraw.data.Objects.Cards.TCard;

public class CardLoader extends AsyncTaskLoader<List<TCard>> {

    private final String name;
    private final String format;
    private final List<String> colors;
    private final List<String> type;
    private final List<String> rarity;



    private MTGServiceAPI api;

    public CardLoader(Context context, String name, String format, List<String> colors, List<String> type, List<String> rarity) {
        super(context);
        this.name = name;
        this.format = format;
        this.colors = colors;
        this.type = type;
        this.rarity = rarity;

        api = new MTGServiceAPI();
    }

    @Override
    public List<TCard> loadInBackground() {
        return api.searchCardsFilters(name, format, colors, type,rarity);
    }

    @Override
    public void onStartLoading(){
        //Force load
        forceLoad();
    }



}
