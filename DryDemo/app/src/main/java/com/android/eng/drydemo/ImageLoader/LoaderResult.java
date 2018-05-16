package com.android.eng.drydemo.ImageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Eng on 2018/5/16.
 * Load result
 */

public class LoaderResult {
    public ImageView img;
    public String uri;
    public Bitmap bitmap;
    public int reqWidth;
    public int reqHeight;

    public LoaderResult(ImageView img, String uri,
                        Bitmap bitmap, int reqWidth, int reqHeight) {
        this.img = img;
        this.uri = uri;
        this.bitmap = bitmap;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }
}
