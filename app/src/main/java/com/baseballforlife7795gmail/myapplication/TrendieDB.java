package com.baseballforlife7795gmail.myapplication;

/**
 * Created by Kyle on 3/28/2016.
 */
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class TrendieDB extends AccountInformation{

    private static final String LOG = "MyActivity";

    // database name and version
    public static final String DB_NAME = "trendie.db";
    public static final int DB_VERSION = 1;

    // Location table
    public static final String PROFILE_TABLE = "profile";

    public static final String PROFILE_ID = "_id";
    public static final int PROFILE_ID_COL = 0;

    public static final String PROFILE_NAME = "name";
    public static final int PROFILE_NAME_COL = 1;

    public static final String PROFILE_HANDLE = "handle";
    public static final int PROFILE_HANDLE_COL = 2;

    public static final String PROFILE_PASSWORD = "password";
    public static final int PROFILE_PASSWORD_COL = 3;

    /** Database SQL **/
    public static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + PROFILE_TABLE + " (" +
                    PROFILE_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PROFILE_NAME  + " TEXT, " +
                    PROFILE_HANDLE + " TEXT, " +
                    PROFILE_PASSWORD      + " TEXT)";

    public static final String DROP_PROFILE_TABLE =
            "DROP TABLE IF EXISTS " + PROFILE_TABLE;

    private static class TrendieDBHelper extends SQLiteOpenHelper {

        public TrendieDBHelper(Context context, String name,
                                  CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TrendieDB.CREATE_LOCATION_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int _oldVersion, int _newVersion) {
            db.execSQL(TrendieDB.DROP_PROFILE_TABLE);
            onCreate(db);
        }
    }

    private SQLiteDatabase db;
    private TrendieDBHelper dbHelper;
    public TrendieDB(Context context) {
        dbHelper = new TrendieDBHelper(context, DB_NAME, null, DB_VERSION);
    }

    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (db != null)
            db.close();
    }

    public boolean onNewAccount(String name, String handle, String password){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_NAME, name);
        contentValues.put(PROFILE_HANDLE, handle);
        contentValues.put(PROFILE_PASSWORD, password);
        db.insert(PROFILE_TABLE, null, contentValues);
        return true;
    }

    public AccountInformation getAccount(String handle) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DB_NAME + " WHERE "
                + PROFILE_HANDLE + " = " + handle;

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        AccountInformation info = new AccountInformation();
        info.setAccountID((c.getInt(c.getColumnIndex(PROFILE_ID))));//KEY_ID key for fetching id
        info.setUsername((c.getString(c.getColumnIndex(PROFILE_NAME))));//KEY_BREAKFAST key for fetching isBreakfast
        info.setHandle((c.getString(c.getColumnIndex(PROFILE_HANDLE))));//KEY_LUNCH key for fetching isLunch


        return info;
    }
}