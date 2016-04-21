package com.baseballforlife7795gmail.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * Created by Kyle on 4/6/2016.
 */
public class Database {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "profile_name";
    public static final String KEY_HANDLE = "profile_handle";
    public static final String KEY_PASSWORD = "profile_password";
    public static final String KEY_PPICTURE = "profile_picture";

    private static final String DATABASE_NAME = "Trendiedb";
    private static final String DATABASE_TABLE = "profileTable";
    private static final int DATABASE_VERSION = 4;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public String name;
    public String handle;
    public String password;
    public byte[] picture;
    public int id;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_NAME + " TEXT NOT NULL, " +
                            KEY_PASSWORD + " TEXT NOT NULL, " +
                            KEY_HANDLE + " TEXT NOT NULL, " +
                            KEY_PPICTURE + " BLOB );"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ;
        }

    }


    public Database(Context c) {
        ourContext = c;
    }

    public Database open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String name, String handle, String password) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_PASSWORD, password);
        cv.put(KEY_HANDLE, handle);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public String getData(String handleTest) {
        String[] columns = new String[]{KEY_ROWID, KEY_NAME, KEY_PASSWORD, KEY_HANDLE, KEY_PPICTURE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        String result = "";

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iName = c.getColumnIndex(KEY_NAME);
        int iPassword = c.getColumnIndex(KEY_PASSWORD);
        int iHandle = c.getColumnIndex(KEY_HANDLE);
        int iPicture = c.getColumnIndex(KEY_PPICTURE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getString(iHandle).equals(handleTest)) {
                name = c.getString(iName);
                handle = c.getString(iHandle);
                id = c.getInt(iRow);
                password = c.getString(iPassword);

                if (c.getBlob(iPicture) != null) {
                    picture = c.getBlob(iPicture);
                } else {
                    picture = null;
                }
            }
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileP() {
        return picture;
    }

    public boolean saveImage(String name, byte[] pic) throws SQLException {
       System.out.println("--" + open());
       ContentValues cv = new ContentValues();
       cv.put(KEY_PPICTURE, pic);
       String where = KEY_HANDLE +"='"+ name + "'";
       return ourDatabase.update(DATABASE_TABLE, cv,where, null) != 0;
    }

}



