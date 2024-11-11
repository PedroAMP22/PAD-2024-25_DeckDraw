package es.ucm.deckdraw.data.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.loader.content.AsyncTaskLoader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader extends AsyncTaskLoader<List<Bitmap>> {
    private ArrayList<String> urlList;

    public ImageLoader(Context context, ArrayList<String> urlList) {
        super(context);
        this.urlList = urlList;
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // Starts loading the data
    }

    @Override
    public List<Bitmap> loadInBackground() {

        List<Bitmap> bitmapList = new ArrayList<>();
        for(String imageUrl : urlList) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                if(bitmap != null)
                    bitmapList.add(bitmap);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return bitmapList;
    }
}
