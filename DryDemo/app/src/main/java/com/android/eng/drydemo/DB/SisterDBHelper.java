package com.android.eng.drydemo.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.eng.drydemo.Model.Sister;

import java.util.ArrayList;

/**
 * Created by Eng on 2018/5/21.
 * DB helper
 */

public class SisterDBHelper {
    private static final String TAG = "SisterDBHelper";

    private static SisterDBHelper sInstance;
    private SisterOpenHelper mOpenHelper;
    private SQLiteDatabase db;

    public SisterDBHelper(Context context) {
        mOpenHelper = new SisterOpenHelper(context.getApplicationContext());
    }

    static SisterDBHelper getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (SisterDBHelper.class) {
                if (sInstance == null) {
                    sInstance = new SisterDBHelper(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * insert a sister
     */
    public void insertSister(Sister sister) {
        db = getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SisterTable.COLUMN_SI_ID, sister.get_id());
        contentValues.put(SisterTable.COLUMN_SI_CREATEAT, sister.getCreateAt());
        contentValues.put(SisterTable.COLUMN_SI_DESC, sister.getDesc());
        contentValues.put(SisterTable.COLUMN_SI_PUBLISHEDAT, sister.getPublishedAt());
        contentValues.put(SisterTable.COLUMN_SI_SOURCE, sister.getSource());
        contentValues.put(SisterTable.COLUMN_SI_TYPE, sister.getType());
        contentValues.put(SisterTable.COLUMN_SI_URL, sister.getUrl());
        contentValues.put(SisterTable.COLUMN_SI_USED, sister.isUsed());
        contentValues.put(SisterTable.COLUMN_SI_WHO, sister.getWho());
        db.insert(SisterTable.TABLE_NAME, null, contentValues);
        closeIO(null);
    }

    /**
     * insert sister list
     */
    public void insertSisterList(ArrayList<Sister> sisterList) {
        db = getWritableDB();
        db.beginTransaction();
        try {
            for (Sister sister : sisterList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SisterTable.COLUMN_SI_ID, sister.get_id());
                contentValues.put(SisterTable.COLUMN_SI_CREATEAT, sister.getCreateAt());
                contentValues.put(SisterTable.COLUMN_SI_DESC, sister.getDesc());
                contentValues.put(SisterTable.COLUMN_SI_PUBLISHEDAT, sister.getPublishedAt());
                contentValues.put(SisterTable.COLUMN_SI_SOURCE, sister.getSource());
                contentValues.put(SisterTable.COLUMN_SI_TYPE, sister.getType());
                contentValues.put(SisterTable.COLUMN_SI_URL, sister.getUrl());
                contentValues.put(SisterTable.COLUMN_SI_USED, sister.isUsed());
                contentValues.put(SisterTable.COLUMN_SI_WHO, sister.getWho());
                db.insert(SisterTable.TABLE_NAME, null, contentValues);
            }
        } finally {
            if (db != null && db.isOpen()) {
                db.endTransaction();
                closeIO(null);
            }
        }
    }

    /**
     * delete sister by id
     */
    public void deleteSister(String _id) {
        db = getWritableDB();
        db.delete(SisterTable.TABLE_NAME,
                SisterTable.COLUMN_SI_ID + " =?",
                new String[]{_id});
        closeIO(null);
    }

    /**
     * delete all sisters
     */
    public void deleteAllSisters() {
        db = getWritableDB();
        db.delete(SisterTable.TABLE_NAME, null, null);
        closeIO(null);
    }

    /**
     * update sister info by id
     */
    public void updateSister(String _id, Sister sister) {
        db = getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SisterTable.COLUMN_SI_ID, sister.get_id());
        contentValues.put(SisterTable.COLUMN_SI_CREATEAT, sister.getCreateAt());
        contentValues.put(SisterTable.COLUMN_SI_DESC, sister.getDesc());
        contentValues.put(SisterTable.COLUMN_SI_PUBLISHEDAT, sister.getPublishedAt());
        contentValues.put(SisterTable.COLUMN_SI_SOURCE, sister.getSource());
        contentValues.put(SisterTable.COLUMN_SI_TYPE, sister.getType());
        contentValues.put(SisterTable.COLUMN_SI_URL, sister.getUrl());
        contentValues.put(SisterTable.COLUMN_SI_USED, sister.isUsed());
        contentValues.put(SisterTable.COLUMN_SI_WHO, sister.getWho());
        db.update(SisterTable.TABLE_NAME, contentValues,
                SisterTable.COLUMN_SI_ID + " =?", new String[]{_id});
        closeIO(null);
    }

    /**
     * get num of sisters in db
     */
    public int getNumOfSister() {
        db = getWritableDB();
        Cursor cursor = db.rawQuery("SELETE COUNT (*) FROM" + SisterTable.TABLE_NAME, null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        Log.d(TAG, "cnt: " + cnt);
        closeIO(cursor);
        return cnt;
    }

    /**
     * get db readable
     */
    private SQLiteDatabase getReadableDB() {
        return mOpenHelper.getReadableDatabase();
    }

    /**
     * get db writable
     */
    private SQLiteDatabase getWritableDB() {
        return mOpenHelper.getWritableDatabase();
    }

    /**
     * close cursor and db
     */
    private void closeIO(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }
}
