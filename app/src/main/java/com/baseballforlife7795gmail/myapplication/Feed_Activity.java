package com.baseballforlife7795gmail.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;


public class Feed_Activity extends Activity {
    private Button btnClick;
    public static TextView msgLikes;
    public static ImageView profileImage;
    public static ImageView profileImage2;
    public static ImageView profileImage3;
    public static TextView nameLayout;
    public static TextView nameLayout2;
    public static TextView nameLayout3;
    public static ImageView userImage;
    private final static String TAG = "MainActivity";
    public final static String PREFS = "PrefsFile";
    public static ImageView userImage2;
    public static TextView msgLikes2;
    public static ImageView userImage3;
    public static TextView msgLikes3;

    private SharedPreferences settings = null;
    private SharedPreferences.Editor editor = null;
    int counter = 0;
    int counter2 = 0;
    int counter3 = 0;
    private static Long MILLISEC_PER_DAY = 86400000L;
    private static Long MILLISECS_PER_MIN = 60000L;
    String handle;
    String name;
    byte[] pic;
    private static Long delay = MILLISECS_PER_MIN * 2; //2 minutes(for testing)
    //private static long delay_1 = MILLISEC_PER_DAY * 1; //Number of days
    Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        msgLikes = (TextView) findViewById(R.id.likes1);
        msgLikes2 = (TextView) findViewById(R.id.likes);
        msgLikes3 = (TextView) findViewById(R.id.likes2);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileImage2= (ImageView) findViewById(R.id.profileImage_1);
        profileImage3= (ImageView) findViewById(R.id.profileImage2);
        nameLayout = (TextView) findViewById(R.id.nameLayout);
        nameLayout2 = (TextView) findViewById(R.id.nameLayout_1);
        nameLayout3 = (TextView) findViewById(R.id.nameLayout2);
        userImage = (ImageView) findViewById(R.id.parkImage_1);
        userImage2 = (ImageView) findViewById(R.id.parkImage);
        userImage3 = (ImageView) findViewById(R.id.parkImage2);

        Log.v(TAG, "Service started");
        SharedPreferences setting = getSharedPreferences(Feed_Activity.PREFS, MODE_PRIVATE);

        //save time of run:
        settings = getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = settings.edit();

        Log.v(TAG, "Starting CheckRecentRun service...");
        // startService(new Intent(this, CheckRecentRun.clas);

        handle = getIntent().getExtras().getString("Handle");
        name = getIntent().getExtras().getString("Name");
        pic = getIntent().getExtras().getByteArray("Picture");



        setAlarm();
        Log.v(TAG, "Service stopped");
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.getAllPictures();

    }

    private void setAlarm() {
        Intent serviceIntent = new Intent(this, Feed_Activity.class);
        PendingIntent pi = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES /5, AlarmManager.INTERVAL_FIFTEEN_MINUTES /5, pi);
        Log.v(TAG, "Alarm set");
    }



    public void sendNotitification() {
        Intent mainIntent = new Intent(this, Login.class);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("Your Feed Misses You!")
                .setContentText("Don't forget to check the feed!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.trendieicon)
                .setTicker("We Miss You! Please come back and see your radius")
                .setWhen(System.currentTimeMillis())
                .getNotification();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final int NOTIFICTION_ID = 1;
        notificationManager.notify(NOTIFICTION_ID, notification);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Log.v(TAG, "notification sent");
    }

    boolean liked=false;
    boolean liked2=false;
    boolean liked3=false;
    public void onLike(View v) {
        if(v == userImage) {
            if (!liked){
                counter++;
                liked=true;
            } else {
                counter--;
                liked=false;
            }
            msgLikes.setText("Likes: " + Integer.toString(counter));
        }
        else if (v==userImage2){
            if (!liked2){
                counter2++;
                liked2=true;
            } else {
                counter2--;
                liked2=false;
            }
            msgLikes2.setText("Likes: " + Integer.toString(counter2));
        }
        else{
            if (!liked3){
                counter3++;
                liked3=true;
            } else {
                counter3--;
                liked3=false;
            }
            msgLikes3.setText("Likes: " + Integer.toString(counter3));
        }

    }

    @Override
    protected void onDestroy(){
        sendNotitification();
        super.onDestroy();
    }

    public void loadProfile(View v){
        Intent i = new Intent(Feed_Activity.this, TrendieProfile.class);
        i.putExtra("Handle", handle);
        i.putExtra("Name", name);
        i.putExtra("Picture", pic);
        startActivity(i);
    }

    public void onLogout(View v) {
        Intent i = new Intent(Feed_Activity.this, Login.class);
        startActivity(i);
    }
}
