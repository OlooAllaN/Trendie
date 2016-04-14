package com.baseballforlife7795gmail.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Kyle on 4/6/2016.
 */
public class Database {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "profile_name";
    public static final String KEY_HANDLE = "profile_handle";
    public static final String KEY_PASSWORD = "profile_password";

    private static final String DATABASE_NAME = "Trendiedb";
    private static final String DATABASE_TABLE = "profileTable";
    private static final int DATABASE_VERSION = 2;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public String name;
    public String handle;
    public String password;

    private static class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT NOT NULL, " +
                    KEY_PASSWORD + " TEXT NOT NULL, " +
                    KEY_HANDLE + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_PASSWORD + " TEXT");
        }
    }

    public Database(Context c){
        ourContext = c;
    }

    public Database open() throws SQLException{
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
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
        String[] columns = new String[]{ KEY_ROWID, KEY_NAME, KEY_PASSWORD, KEY_HANDLE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        String result = "";

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iName = c.getColumnIndex(KEY_NAME);
        int iPassword = c.getColumnIndex(KEY_PASSWORD);
        int iHandle = c.getColumnIndex(KEY_HANDLE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getString(iHandle).equals(handleTest)) {
                name = c.getString(iName);
                handle = c.getString(iHandle);
                password = c.getString(iPassword);
            }
        }

        return result;
    }

    public String getName(){
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public String getPassword(){
        return password;
    }
}
