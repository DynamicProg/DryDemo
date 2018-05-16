package com.android.eng.drydemo.ImageLoader.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.android.eng.drydemo.ImageLoader.SisterCompress;
import com.android.eng.drydemo.ImageLoader.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Eng on 2018/5/16.
 * Related to disk cache
 */

public class DiskCacheHelper {
    private static final String TAG = "DiskCacheHelper";

    private static final long DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private static final int DISK_CACHE_INDEX = 0;

    private Context mContext;
    private DiskLruCache mDiskCache;
    private SisterCompress mCompress;
    private boolean mIsDiskLruCacheCreated = false; // disk cache create flag


    public DiskCacheHelper(Context context) {
        this.mContext = context;
        mCompress = new SisterCompress();
        File diskCacheDir = getDiskCacheDir(mContext, "diskCache");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir();
        }
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DiskLruCache getDiskCache() {
        return mDiskCache;
    }

    public void setIsDiskCacheCreate(boolean status) {
        this.mIsDiskLruCacheCreated = status;
    }

    public boolean getIsDiskCacheCreate() {
        return mIsDiskLruCacheCreated;
    }

    /**
     * get menu of disk cache
     */
    private File getDiskCacheDir(Context Context, String dirName) {
        boolean externalStorageAvailable = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = Context.getExternalCacheDir().getPath();
        } else {
            cachePath = Context.getCacheDir().getPath();
        }
        Log.d(TAG, "get disk cache dir: " + cachePath);
        return new File(cachePath + File.separator + dirName);
    }

    /**
     * query available space
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    public Bitmap loadBitmapFromDiskCache(String key, int reqWidth, int reqHeight) throws IOException {
        Log.d(TAG, "load bitmap from disk cache");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("cannot load image on UI thread");
        }
        if (mDiskCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mCompress.decodeBitmapFromFileDescriptor(fileDescriptor,
                    reqWidth, reqHeight);
        }
        return bitmap;
    }

    public Bitmap saveImgByte(String key, int reqWidth, int reqHeight, byte[] bytes) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("cannot load image on UI thread");
        }
        if (mDiskCache == null) {
            return null;
        }
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream output = editor.newOutputStream(DISK_CACHE_INDEX);
                output.write(bytes);
                output.flush();
                editor.commit();
                output.close();
                return loadBitmapFromDiskCache(key, reqWidth, reqHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
