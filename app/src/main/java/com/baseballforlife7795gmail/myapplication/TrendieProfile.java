package com.baseballforlife7795gmail.myapplication;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrendieProfile extends FragmentActivity
        implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int INTERVAL_REFRESH = 10 * 1000;   // 10 seconds

    private GoogleMap map;
    private TextView username;
    private TextView userHandle;
    public static ImageView userProfilePic;
    public String name;
    public Database db = new Database(this);

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

        username = (TextView) findViewById(R.id.name);
        userHandle = (TextView) findViewById(R.id.handle);
        userProfilePic = (ImageView) findViewById(R.id.profilePic);


        String name = getIntent().getExtras().getString("Name");
        String handle = "@" + getIntent().getExtras().getString("Handle");
        byte[] picture = getIntent().getExtras().getByteArray("Picture");

        username.setText(name);
        userHandle.setText(handle);

        if (picture != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
            userProfilePic.setImageBitmap(bitmap);
        }


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
        googleApiClient.disconnect();

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
                map.clear();      // clear old marker(s)
                map.addMarker(    // add new marker
                        new MarkerOptions()
                                .position(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .title("You are here"));
            }
        }
    }


    private void setMapToRefresh(){
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
            }
            catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    public void onLogout(View v){
        Intent i = new Intent(TrendieProfile.this, Login.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bm = BitmapFactory.decodeFile(picturePath,options);
            userProfilePic.setImageBitmap(bm);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();

            if(byteArray!=null) {
                try {
                    db.saveImage(userHandle.getText().toString(), byteArray);
                    Toast t = Toast.makeText(this, "Success!", Toast.LENGTH_LONG);
                    t.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }




        }
    }


}