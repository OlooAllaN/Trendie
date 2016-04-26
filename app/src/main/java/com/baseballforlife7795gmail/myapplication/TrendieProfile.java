package com.baseballforlife7795gmail.myapplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrendieProfile extends FragmentActivity
        implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_UPLOAD_IMAGE = 2;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int INTERVAL_REFRESH = 10 * 1000;   // 10 seconds

    public static GoogleMap map;
    private TextView username;
    private TextView userHandle;
    private Button upload;
    public static ImageView userProfilePic;
    public static ImageView uploadView;
    public static ImageView uploadView2;
    public static ImageView uploadView3;
    public String name;
    public String handle;
    public Database db = new Database(this);
    public Bitmap bitmap;
    public String myName;
    public byte[] picture;

    private GoogleApiClient googleApiClient;

    private Timer timer;

    //**************************************************************
    // Activity lifecycle methods
    //****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendie_profile);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        // if GPS is not enabled, start GPS settings activity
        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        upload = (Button) findViewById(R.id.upload);
        username = (TextView) findViewById(R.id.name);
        userHandle = (TextView) findViewById(R.id.handle);
        userProfilePic = (ImageView) findViewById(R.id.profilePic);
        uploadView = (ImageView) findViewById(R.id.imageView);
        uploadView2 = (ImageView) findViewById(R.id.imageView2);
        uploadView3 = (ImageView) findViewById(R.id.imageView3);

        myName = getIntent().getExtras().getString("Name");
        handle = getIntent().getExtras().getString("Handle");
        picture = getIntent().getExtras().getByteArray("Picture");

        username.setText(myName);
        userHandle.setText("@" + handle);

        if (picture != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
            userProfilePic.setImageBitmap(bitmap);
        }
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onStart();
        db.getPictures(db.getUserId());
    }


    @Override
    protected void onStart() {
        super.onStart();

        // if GoogleMap object is not already available, get it
        if (map == null) {
            FragmentManager manager = getSupportFragmentManager();
            SupportMapFragment fragment =
                    (SupportMapFragment) manager.findFragmentById(R.id.map);
            map = fragment.getMap();
        }

        // if GoogleMap object is available, configure it
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
        }

        googleApiClient.connect();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    //**************************************************************
    // Private methods
    //****************************************************************
    private void updateMap() {
        if (googleApiClient.isConnected()) {
            setCurrentLocationMarker();
        }
    }

    double lng;
    double lat;

    private void setCurrentLocationMarker() {
        if (map != null) {
            //get current location
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);

            if (location != null) {
                // zoom in on current location
                map.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(),
                                                location.getLongitude()))
                                        .zoom(16.5f)
                                        .bearing(0)
                                        .tilt(25)
                                        .build()));

                // add a marker for the current location
                    // clear old marker(s)

                //setiing icon to pitcher
                Drawable d=(Drawable) getResources().getDrawable(R.drawable.profilepic);
                d.setLevel(1234);
                BitmapDrawable bd=(BitmapDrawable) d.getCurrent();
                Bitmap b=bd.getBitmap();
                Bitmap bhalfsize=Bitmap.createScaledBitmap(b, 60, 60, false);
                bhalfsize = getCircularBitmap(bhalfsize);

                map.addMarker(    // add new marker
                        new MarkerOptions()
                                .position(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize)));
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    private void setMapToRefresh() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                TrendieProfile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMap();
                    }
                });
            }
        };
        timer.schedule(task, INTERVAL_REFRESH, INTERVAL_REFRESH);
    }

    //**************************************************************
    // Implement ConnectionCallbacks interface
    //****************************************************************
    @Override
    public void onConnected(Bundle dataBundle) {
        updateMap();
        setMapToRefresh();
    }

    @Override
    public void onConnectionSuspended(int i) {
        timer.cancel();
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    //**************************************************************
    // Implement OnConnectionFailedListener
    //****************************************************************
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // if Google Play services can resolve the error, display activity
        if (connectionResult.hasResolution()) {
            try {
                // start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Connection failed. Error code: "
                            + connectionResult.getErrorCode())
                    .show();
        }
    }

    //**************************************************************
    // Implement OnClickListener
    //****************************************************************
    @Override
    public void onClick(View v) {
        if (v.getId() == userProfilePic.getId()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        } else if (v.getId() == upload.getId()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_UPLOAD_IMAGE);
        }

    }

    public void onLogout(View v) {
        Intent i = new Intent(TrendieProfile.this, Login.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            final BitmapFactory.Options options = new BitmapFactory.Options();

            Bitmap bm = BitmapFactory.decodeFile(picturePath, options);
            userProfilePic.setImageBitmap(bm);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();

            System.out.println(handle);
            if(db.saveImage(handle, byteArray)) {
                Toast t = Toast.makeText(this, "Success!", Toast.LENGTH_LONG);
                t.show();
            }


        }
        if (requestCode == RESULT_UPLOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bm = BitmapFactory.decodeFile(picturePath, options);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();

            Database entry = new Database(TrendieProfile.this);
            try {
                entry.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (entry.uploadImage(byteArray, lng, lat))
            {
                Toast t = Toast.makeText(this, "success",Toast.LENGTH_LONG);
                t.show();
            }
            entry.close();
            db.getPictures(db.getUserId());
        }
    }



    public void showAddress(View v) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1);
        if(v.getId()==uploadView.getId()) {
            addresses = geocoder.getFromLocation(db.getLat(), db.getLng(), 1);
        }
        else if(v.getId() == uploadView2.getId()){
            addresses = geocoder.getFromLocation(db.getLat2(), db.getLng2(), 1);
        }
        else if(v.getId() == uploadView3.getId()){
            addresses = geocoder.getFromLocation(db.getLat3(), db.getLng3(), 1);
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();

        Toast t = Toast.makeText(this, address + ", " + city + ", " + state, Toast.LENGTH_LONG);
        t.show();
    }

    public void loadTrends(View v){
        Intent i = new Intent(this, Feed_Activity.class);

        i.putExtra("Handle", handle);
        i.putExtra("Name", myName);
        i.putExtra("Picture", picture);

        startActivity(i);
    }





}