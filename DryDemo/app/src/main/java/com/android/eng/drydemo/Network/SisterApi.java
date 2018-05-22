package com.android.eng.drydemo.Network;

import android.util.Log;

import com.android.eng.drydemo.Model.Sister;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class is for http request and response
 */

public class SisterApi {
    private static final String TAG = "SisterApi";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    public ArrayList<Sister> fetchSister(int count, int page) {
        Log.d(TAG, "fetchSister");
        String fetchUrl = BASE_URL + count + "/" + page;
        ArrayList<Sister> sisters = new ArrayList<>();

        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.d(TAG, "Server response: " + code);
            if (code == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data, "UTF-8");
                sisters = praseSister(result);
            } else {
                Log.e(TAG, "Request fail: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sisters;
    }

    private ArrayList<Sister> praseSister(String content) throws Exception {
        Log.d(TAG, "praseSister");
        ArrayList<Sister> sisters = new ArrayList<>();
        JSONObject object = new JSONObject(content);
        JSONArray array = object.getJSONArray("results");
        for (int i = 0; i < array.length(); i++) {
            JSONObject res = (JSONObject) array.get(i);
            Sister sister = new Sister();
            sister.set_id(res.optString(ServerInfo._id));
            sister.setCreateAt(res.optString(ServerInfo.createAt));
            sister.setDesc(res.optString(ServerInfo.desc));
            sister.setPublishedAt(res.optString(ServerInfo.publishe));
            sister.setSource(res.optString(ServerInfo.source));
            sister.setType(res.optString(ServerInfo.type));
            sister.setUrl(res.optString(ServerInfo.url));
            sister.setUsed(res.optBoolean(ServerInfo.used) ? 1 : 0);
            sister.setWho(res.optString(ServerInfo.who));
            sisters.add(sister);
        }
        return sisters;
    }

    /**
     * Get data form stream
     */
    private byte[] readFromStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    private static class ServerInfo {
        private static String _id = "_id";
        private static String createAt = "createAt";
        private static String desc = "desc";
        private static String publishe = "publishe";
        private static String source = "source";
        private static String type = "type";
        private static String url = "url";
        private static String used = "used";
        private static String who = "who";
    }

}
