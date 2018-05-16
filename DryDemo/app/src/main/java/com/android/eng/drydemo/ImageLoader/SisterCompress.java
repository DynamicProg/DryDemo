package com.android.eng.drydemo.ImageLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by Eng on 2018/5/16.
 * For image compress
 */

public class SisterCompress {
    private static final String TAG = "SisterCompress";

    /**
     * compress resource image
     */
    public Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // calc scale ratio
        options.inSampleSize = calcSimpleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * compress file image
     */
    public Bitmap decodeBitmapFromFileDescriptor(FileDescriptor descriptor, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor, null, options);
        options.inSampleSize = calcSimpleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(descriptor, null, options);
    }

    private int calcSimpleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, " origin image size: "+ width + "x" + height);
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "in sample size: " + inSampleSize);
        return inSampleSize;
    }
}
