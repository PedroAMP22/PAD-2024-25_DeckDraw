package es.ucm.deckdraw.data.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import es.ucm.deckdraw.MainActivity;
import es.ucm.deckdraw.data.Objects.Cards.ImageUrlObject;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.util.Callback;

public class ImageLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ImageUrlObject>> {

    // Key for the list of URLs in the Bundle
    public static final String URLS_KEY = "URLS";

    private Context context;
    private final Callback<List<ImageUrlObject>> callback;


    public ImageLoaderCallbacks(Context context, Callback<List<ImageUrlObject>> callback){
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Loader<List<ImageUrlObject>> onCreateLoader(int id, @Nullable Bundle args) {
        assert args != null;
        ArrayList<String> urlList = args.getStringArrayList(URLS_KEY);
        assert urlList != null ;
        return new ImageLoader(context, urlList);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ImageUrlObject>> loader, List<ImageUrlObject> data) {
        if (data != null && !data.isEmpty()) {
            callback.onSuccess(data); // Devuelve los resultados a través de onSuccess
        } else {
            callback.onFailure(new Exception("No se puede mostrar la url a imagen"));
        }
        //((MainActivity) context).updateImages(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ImageUrlObject>> loader) {

    }
}
