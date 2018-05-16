package com.android.eng.drydemo.ImageLoader.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Eng on 2018/5/16.
 * Related to mem cache
 */

public class MemoryCacheHelper {
    private static final String TAG = "MemoryCacheHelper";

    private Context mContext;
    private LruCache<String, Bitmap> mMemCache;

    public MemoryCacheHelper(Context context) {
        this.mContext = context;
        int maxMem = (int) (Runtime.getRuntime().maxMemory() / 1024); // get app max mem
        int cacheSize = maxMem / 8; // size of cache
        mMemCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * get object of mem chche
     */
    public LruCache<String, Bitmap> getMemCache() {
        return mMemCache;
    }

    /**
     * get bitmap from LruCache by key
     */
    public Bitmap getBitmapFromMemCache(String key) {
        Log.d(TAG, "get bitmap from memcache");
        return mMemCache.get(key);
    }

    /**
     * set bitmap to LruCache by key
     */
    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) != null) {
            Log.d(TAG, "add bitmap to memcache");
            mMemCache.put(key, bitmap);
        } else {
            Log.d(TAG, "bitmap already exits");
        }
    }
}
