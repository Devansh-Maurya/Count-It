package com.android.countit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.countit.data.ItemsContract.Category;

/**
 * Created by devansh on 24/8/18.
 */

public class ItemsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemsDbHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "items.db";
    public static final int DATABASE_VERSION = 1;

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "Calling onCreate to create DB");

        String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + Category.CATEGORY_TABLE_NAME + " ( "
                + Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Category.COLUMN_CATEGORY_NAME + " TEXT NOT NULL, "
                + Category.COLUMN_CATEGORY_COLOR + " INTEGER NOT NULL, "
                + Category.COLUMN_TOTAL_ITEMS + " INTEGER DEFAULT 0); ";

        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
