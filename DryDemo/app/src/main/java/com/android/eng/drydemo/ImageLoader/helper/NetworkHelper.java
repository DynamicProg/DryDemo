package com.android.eng.drydemo.ImageLoader.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Eng on 2018/5/16.
 * Related to load image from network
 */

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * download image(bitmap) by URL
     */
    public static Bitmap downloadBitmapFromUrl(String imgUrl) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(imgUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            Log.e(TAG, "downlad image error: " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * download image(byte) by URL
     */
    public static byte[] downloadUrlToStream(String imgUrl) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();
                out = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                return out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * convert URL to MD5
     */
    public static String hasKeyForURL(String url) {
        String hashKey = "";
        try {
            final MessageDigest mDig = MessageDigest.getInstance("MD5");
            mDig.update(url.getBytes());
            hashKey = bytesToHexString(mDig.digest());
        } catch (NoSuchAlgorithmException e) {
            hashKey = String.valueOf(url.hashCode());
        }
        return hashKey;
    }


    /**
     * convert bytes to MD5
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte item : bytes) {
            String hex = Integer.toHexString(0xFF & item);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
