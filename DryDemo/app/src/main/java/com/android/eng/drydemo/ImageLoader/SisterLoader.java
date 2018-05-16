package com.android.eng.drydemo.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.eng.drydemo.ImageLoader.helper.DiskCacheHelper;
import com.android.eng.drydemo.ImageLoader.helper.MemoryCacheHelper;
import com.android.eng.drydemo.ImageLoader.helper.NetworkHelper;
import com.android.eng.drydemo.R;
import com.android.eng.drydemo.Utils.NetworkUtils;
import com.android.eng.drydemo.Utils.SizeUtils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Eng on 2018/5/16.
 * Load image
 */

public class SisterLoader {
    private static final String TAG = "SisterLoader";

    private static SisterLoader sInstance;

    private static final int MESSAGE_POST_RESULT = 1;
    private static final int TAG_KEY_URI = R.id.sister_loader_uri;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors(); // cup num
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1; // core thread num
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1; // max thread pool num
    private static final long KEEP_ALIVE = 10L; // threa idle time

    private Context mContext;
    private MemoryCacheHelper mMemHelper;
    private DiskCacheHelper mDiskHelper;

    private SisterLoader(Context context) {
        this.mContext = context.getApplicationContext();
        mMemHelper = new MemoryCacheHelper(mContext);
        mDiskHelper = new DiskCacheHelper(mContext);
    }

    public static SisterLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SisterLoader(context);
        }
        return sInstance;
    }

    /**
     * create thread by thread factory
     */
    private static final ThreadFactory mFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "SisterLoader# " + mCount.getAndIncrement());
        }
    };

    /**
     * thread pool control
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            mFactory);

    /**
     * load image sync, can only run on main thread
     */
    private Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        final String key = NetworkHelper.hasKeyForURL(url);
        // search from mem cache
        Bitmap bitmap = mMemHelper.getBitmapFromMemCache(key);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = mDiskHelper.loadBitmapFromDiskCache(key, reqHeight, reqHeight);
            // get bitmap from disk, add to mem cache
            if (bitmap != null) {
                mMemHelper.addBitmapToMemCache(key, bitmap);
                return bitmap;
            }
            // not exits on disk, connect to network
            if (NetworkUtils.isAvailable(mContext)) {
                bitmap = mDiskHelper.saveImgByte(key, reqWidth, reqHeight,
                        NetworkHelper.downloadUrlToStream(url));
                Log.d(TAG, "load bitmap from network");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && mDiskHelper.getIsDiskCacheCreate()) {
            Log.d(TAG, "disk cache not created");
            bitmap = NetworkHelper.downloadBitmapFromUrl(url);
        }
        return bitmap;
    }

    public void bindBitmap(final String url, final ImageView imageView,
                           final int reqWidth, final int reqHeight) {
        final String key = NetworkHelper.hasKeyForURL(url);
        imageView.setTag(TAG_KEY_URI, url);
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, url,
                            bitmap, reqWidth, reqHeight);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }


    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView resImage = result.img;
            ViewGroup.LayoutParams params = resImage.getLayoutParams();
            params.width = SizeUtils.dp2px(mContext.getApplicationContext(), result.reqWidth);
            params.height = SizeUtils.dp2px(mContext.getApplicationContext(), result.reqHeight);
            resImage.setLayoutParams(params);
            resImage.setImageBitmap(result.bitmap);
            String uri = (String) resImage.getTag(TAG_KEY_URI);
            if (uri.equals(result.uri)) {
                resImage.setImageBitmap(result.bitmap);
            } else {
                Log.d(TAG, "url changed, not set image");
            }
        }
    };
}
