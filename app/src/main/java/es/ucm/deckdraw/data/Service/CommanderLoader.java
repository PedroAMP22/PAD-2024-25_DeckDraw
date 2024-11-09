package es.ucm.deckdraw.data.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.Collections;
import java.util.List;

public class CommanderLoader extends AsyncTaskLoader<List<String>> {

    String commander;
    MTGServiceAPI api;

    public CommanderLoader(@NonNull Context context, String commander) {
        super(context);
        this.commander = commander;
        api =  new MTGServiceAPI();
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {
        return api.searchCommander(commander);
    }

    @Override
    public void onStartLoading(){
        //Force load
        forceLoad();
    }
}
