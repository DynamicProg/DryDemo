package com.android.eng.drydemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eng on 2018/1/23.
 */

public class PictureLoader {
    public static final String TAG = "PictureLoader";

    private static int MSG_GET_SUCCESS = 100;

    private ImageView mLoadImg;
    private String mImgUrl;
    private byte[] mPictureByte;

    public void load(ImageView loadImg, String imgUrl) {
        Log.d(TAG, "load: start");
        Log.d(TAG, "load: loadImg: " + loadImg);
        Log.d(TAG, "load: imgUrl: " + imgUrl);
        this.mLoadImg = loadImg;
        this.mImgUrl = imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(mImgUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(5000);
                Log.d(TAG, "run: http request");
                if (httpURLConnection.getResponseCode() == 200) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(bytes)) != -1) {
                        byteArrayOutputStream.write(bytes, 0, len);
                    }
                    mPictureByte = byteArrayOutputStream.toByteArray();
                    inputStream.close();
                    byteArrayOutputStream.close();
                    handler.sendEmptyMessage(MSG_GET_SUCCESS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_GET_SUCCESS) {
                if (mPictureByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(mPictureByte, 0, mPictureByte.length);
                    mLoadImg.setImageBitmap(bitmap);
                }
            }
        }
    };
}
