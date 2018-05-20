package com.android.eng.drydemo.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eng on 2018/5/21.
 * SQL open helper
 */

public class SisterOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sister.db";
    private static final int DB_VERSION = 1;

    public SisterOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + SisterTable.TABLE_NAME + " ("
                + SisterTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SisterTable.COLUMN_SI_ID + " TEXT, "
                + SisterTable.COLUMN_SI_CREATEAT + " TEXT, "
                + SisterTable.COLUMN_SI_DESC + " TEXT, "
                + SisterTable.COLUMN_SI_PUBLISHEDAT + " TEXT, "
                + SisterTable.COLUMN_SI_SOURCE + " TEXT, "
                + SisterTable.COLUMN_SI_TYPE + " TEXT, "
                + SisterTable.COLUMN_SI_URL + " TEXT, "
                + SisterTable.COLUMN_SI_USED + " BOOLEAN, "
                + SisterTable.COLUMN_SI_WHO + " TEXT"
                + ")";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
