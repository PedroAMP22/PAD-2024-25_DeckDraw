package es.ucm.deckdraw.data.Objects.Cards;

import android.graphics.Bitmap;

public class ImageUrlObject
{

    private Bitmap bitmap;
    private String url;

    public ImageUrlObject(Bitmap bitmap, String url){
        this.url = url;
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
