package com.baseballforlife7795gmail.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
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
    public static final String KEY_PICID = "pic_id";
    public static final String KEY_PICTURES = "user_pictures";
    public static final String KEY_LIKES = "picture_likes";
    public static final String KEY_LONG = "picture_long";
    public static final String KEY_LAT = "picture_lat";

    private static final String DATABASE_NAME = "Trendiedb";
    private static final String DATABASE_TABLE = "profileTable";
    private static final String PICTURE_TABLE = "pictureTable";
    private static final int DATABASE_VERSION = 6;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public String name;
    public String handle;
    public String password;
    public byte[] picture;
    public int userId;
    public int picId;
    public byte[] pic;
    public String longitude;
    public String latitude;
    public int likes;
    public double lat;
    public double lat2;
    public double lat3;
    public double lng;
    public double lng2;
    public double lng3;


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
            db.execSQL("CREATE TABLE " + PICTURE_TABLE + " (" +
                    KEY_PICID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_ROWID + " INT NOT NULL, " +
                    KEY_PICTURES + " BLOB, " +
                    KEY_LIKES + " INT NOT NULL, " +
                    KEY_LONG + " REAL, " +
                    KEY_LAT + " REAL );");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + DATABASE_TABLE);
            db.execSQL("DROP TABLE " + PICTURE_TABLE);
            onCreate(db);
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
                userId = c.getInt(iRow);
                password = c.getString(iPassword);

                if (c.getBlob(iPicture) != null) {
                    picture = c.getBlob(iPicture);
                } else {
                    picture = null;
                }
            }
        }
        if(password == null){
            password = "wrong";
            handle = "wrong";
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public int getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileP() {
        return picture;
    }

    public boolean saveImage(String name, byte[] pic) {
       ContentValues cv = new ContentValues();
       cv.put(KEY_PPICTURE, pic);
       String where = KEY_HANDLE +"='"+ name + "'";
       return ourDatabase.update(DATABASE_TABLE, cv,where, null) != 0;
    }
    //Picture Methods
    public String getPictures(int id) {
        String[] columns = new String[]{KEY_PICID, KEY_ROWID, KEY_PICTURES, KEY_LIKES, KEY_LONG, KEY_LAT};
        Cursor c = ourDatabase.query(PICTURE_TABLE, columns, null, null, null, null, null);

        int counter = 0;
        String result = "";
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iPicId = c.getColumnIndex(KEY_PICID);
        int iPictures = c.getColumnIndex(KEY_PICTURES);
        int iLikes = c.getColumnIndex(KEY_LIKES);
        int iLong = c.getColumnIndex(KEY_LONG);
        int iLat = c.getColumnIndex(KEY_LAT);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getInt(iRow) == id){
                picId = c.getInt(iPicId);
                likes = c.getInt(iLikes);
                userId = c.getInt(iRow);
                longitude = c.getString(iLong);
                latitude = c.getString(iLat);

                if (c.getBlob(iPictures) != null) {
                    pic = c.getBlob(iPictures);
                } else {
                    pic = null;
                }

                result = result + likes + "," + userId + "," + longitude + "," + latitude + "," + pic + "/";
                final BitmapFactory.Options options = new BitmapFactory.Options();

                Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
                Bitmap b=bitmap;
                Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 60, 60, false);
                bhalfsize = TrendieProfile.getCircularBitmap(bhalfsize);
                double latit = Double.parseDouble(latitude);
                double lngit = Double.parseDouble(longitude);

                if(counter == c.getCount()-1) {
                    TrendieProfile.uploadView.setImageBitmap(bitmap);
                    TrendieProfile.uploadView.setVisibility(View.VISIBLE);
                    lng = lngit;
                    lat = latit;
                }
                else if(counter == c.getCount()-2){
                    TrendieProfile.uploadView2.setImageBitmap(bitmap);
                    TrendieProfile.uploadView2.setVisibility(View.VISIBLE);
                    lng2 = lngit;
                    lat2 = latit;
                }
                else if(counter == c.getCount()-3){
                    TrendieProfile.uploadView3.setImageBitmap(bitmap);
                    TrendieProfile.uploadView3.setVisibility(View.VISIBLE);
                    lng3 = lngit;
                    lat3 = latit;
                }

                TrendieProfile.map.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(latit, lngit))
                                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize)));

                counter++;
            }
        }
        return result;
    }

    public void getUser(int id) {
        String[] columns = new String[]{KEY_ROWID, KEY_NAME, KEY_PASSWORD, KEY_HANDLE, KEY_PPICTURE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);


        int iRow = c.getColumnIndex(KEY_ROWID);
        int iName = c.getColumnIndex(KEY_NAME);
        int iPassword = c.getColumnIndex(KEY_PASSWORD);
        int iHandle = c.getColumnIndex(KEY_HANDLE);
        int iPicture = c.getColumnIndex(KEY_PPICTURE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getInt(iRow)==(id)) {
                name = c.getString(iName);
                handle = c.getString(iHandle);
                userId = c.getInt(iRow);
                password = c.getString(iPassword);

                if (c.getBlob(iPicture) != null) {
                    picture = c.getBlob(iPicture);
                } else {
                    picture = null;
                }

            }
        }
    }

    public String getAllPictures() {
        String[] columns = new String[]{KEY_PICID, KEY_ROWID, KEY_PICTURES, KEY_LIKES, KEY_LONG, KEY_LAT};
        Cursor c = ourDatabase.query(PICTURE_TABLE, columns, null, null, null, null, null);
        int counter = 0;
        String result = "";
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iPicId = c.getColumnIndex(KEY_PICID);
        int iPictures = c.getColumnIndex(KEY_PICTURES);
        int iLikes = c.getColumnIndex(KEY_LIKES);
        int iLong = c.getColumnIndex(KEY_LONG);
        int iLat = c.getColumnIndex(KEY_LAT);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                picId = c.getInt(iPicId);
                likes = c.getInt(iLikes);
                userId = c.getInt(iRow);
                longitude = c.getString(iLong);
                latitude = c.getString(iLat);

                if (c.getBlob(iPictures) != null) {
                    pic = c.getBlob(iPictures);
                } else {
                    pic = null;
                }

                result = result + likes + "," + userId + "," + longitude + "," + latitude + "," + pic + "/";
                final BitmapFactory.Options options = new BitmapFactory.Options();

                Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length, options);

                if(counter == c.getCount()-3) {
                    Feed_Activity.userImage.setImageBitmap(bitmap);
                    Feed_Activity.userImage.setVisibility(View.VISIBLE);
                    System.out.println(userId);
                    getUser(userId+1);

                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
                    Bitmap b2=bitmap2;
                    Bitmap bhalfsize2=Bitmap.createScaledBitmap(b2, 60, 60, false);
                    Feed_Activity.nameLayout.setText(handle);
                    Feed_Activity.nameLayout.setVisibility(View.VISIBLE);
                    Feed_Activity.profileImage.setImageBitmap(bhalfsize2);
                    Feed_Activity.profileImage.setVisibility(View.VISIBLE);
                    Feed_Activity.msgLikes.setVisibility(View.VISIBLE);
                    System.out.println("3");
                }
                else if(counter == c.getCount()-2){
                    Feed_Activity.userImage2.setImageBitmap(bitmap);
                    Feed_Activity.userImage2.setVisibility(View.VISIBLE);
                    getUser(2);
                    if(picture != null) {
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
                        Bitmap b2 = bitmap2;
                        Bitmap bhalfsize2 = Bitmap.createScaledBitmap(b2, 60, 60, false);
                        Feed_Activity.profileImage2.setImageBitmap(bhalfsize2);
                    }
                    Feed_Activity.nameLayout2.setText(handle);
                    Feed_Activity.nameLayout2.setVisibility(View.VISIBLE);
                    Feed_Activity.profileImage2.setVisibility(View.VISIBLE);
                    Feed_Activity.msgLikes2.setVisibility(View.VISIBLE);
                    System.out.println("2");
                }
                else if(counter == c.getCount()-1){
                    Feed_Activity.userImage3.setImageBitmap(bitmap);
                    Feed_Activity.userImage3.setVisibility(View.VISIBLE);
                    getUser(1);
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
                    Bitmap b2=bitmap2;
                    Bitmap bhalfsize2=Bitmap.createScaledBitmap(b2, 60, 60, false);
                    Feed_Activity.nameLayout3.setText(handle);
                    Feed_Activity.nameLayout3.setVisibility(View.VISIBLE);
                    Feed_Activity.profileImage3.setImageBitmap(bhalfsize2);
                    Feed_Activity.profileImage3.setVisibility(View.VISIBLE);
                    Feed_Activity.msgLikes3.setVisibility(View.VISIBLE);
                    System.out.println("1");
                }

                counter++;

        }
        return result;
    }

    public double getLng(){
        return lng;
    }

    public double getLat(){
        return lat;
    }

    public double getLng2(){
        return lng2;
    }

    public double getLat2(){
        return lat2;
    }

    public double getLng3(){
        return lng3;
    }

    public double getLat3(){
        return lat3;
    }

    public boolean uploadImage(byte[] pic, double longit, double latit) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_ROWID, getUserId());
            cv.put(KEY_LIKES, 0);
            cv.put(KEY_PICTURES, pic);
            cv.put(KEY_LONG, longit);
            cv.put(KEY_LAT, latit);
            return ourDatabase.insert(PICTURE_TABLE, null, cv) != 0;
    }
}



