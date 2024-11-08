package es.ucm.deckdraw.data.Service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

import es.ucm.deckdraw.data.Objects.Cards.TCard;

public class CommanderLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<String>>{
    private Context context;
    private List<String> commanders;



    public CommanderLoaderCallbacks(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        assert args != null;
        return new CommanderLoader(context, args.getString("Commander"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        Log.d("comanders",data.toString());
        this.commanders = data;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }
    public List<String> getCommanders(){
        return commanders;
    }
}
