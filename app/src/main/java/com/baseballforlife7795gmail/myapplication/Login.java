package com.baseballforlife7795gmail.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by Kyle on 4/5/2016.
 */
public class Login extends TrendieProfile {

    private TrendieDB db;
    private AccountInformation info;
    private TextView username;
    private EditText password;
    private EditText handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendie_login);

        username = (TextView) findViewById(R.id.accountName);
        handle = (EditText) findViewById(R.id.handleText);

    }

    public void onLogin(View v){
        Database info = new Database(this);
        try {
            info.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        info.getData(handle.getText().toString());
        String data = info.getHandle();

        username.setText(data);
        info.close();

        Intent i = new Intent(Login.this, TrendieProfile.class);
        startActivity(i);
    }
}
