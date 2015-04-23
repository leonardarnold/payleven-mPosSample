package de.leonardarnold.mpossample.image;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;

/**
 * ImageCache is used to store a Bitmap temporary
 * It will be stored with an id in a HashMap
 * just to be sure that we get the right image
 */
public class ImageCache {
    private static final String TAG = ImageCache.class.getSimpleName();
    private HashMap<String, Bitmap> imageMap;

    public ImageCache() {
    }

    /**
     * @param id    unique id to be secure that the right image is passed
     * @param image corresponding to unique id
     */
    public void setImageById(String id, Bitmap image) {
        imageMap = new HashMap<>();
        imageMap.put(id, image);
        Log.d(TAG, "set Bitmap for id = " + id);
    }

    /**
     * @param id check if bitmap is corresponding to this id
     * @return null if id is wrong, or bitmap if id is correct
     * and delete image
     */
    public Bitmap getBitmapByIdAndClear(String id) {
        Bitmap tmp = imageMap.containsKey(id) ? imageMap.get(id) : null;
        if (tmp != null) {
            imageMap = null;
            Log.d(TAG, "get Bitmap from id = " + id);
        } else {
            Log.d(TAG, "no Bitmap found for id = " + id);
        }
        return tmp;
    }
}
