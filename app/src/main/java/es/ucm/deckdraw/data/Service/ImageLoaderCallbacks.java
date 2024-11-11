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

public class ImageLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Bitmap>> {

    // Key for the list of URLs in the Bundle
    public static final String URLS_KEY = "urls_key";

    private Context context;

    public ImageLoaderCallbacks(Context context){this.context = context;}

    @NonNull
    @Override
    public Loader<List<Bitmap>> onCreateLoader(int id, @Nullable Bundle args) {
        assert args != null;
        ArrayList<String> urlList = args.getStringArrayList(URLS_KEY);
        assert urlList != null ;
        return new ImageLoader(context, urlList);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Bitmap>> loader, List<Bitmap> data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Bitmap>> loader) {

    }
}


