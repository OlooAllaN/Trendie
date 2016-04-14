package com.baseballforlife7795gmail.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by Kyle on 4/5/2016.
 */
public class Login extends Activity {

    private EditText handle;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendie_login);

        handle = (EditText) findViewById(R.id.handleText);
        password = (EditText) findViewById(R.id.passwordText);
        login = (Button) findViewById(R.id.loginButton);


    }

    public void onLogin(View v){
        Database info = new Database(this);
        try {
            info.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        info.getData(handle.getText().toString());

        if(info.getPassword().equals(password.getText().toString())) {
            Intent i = new Intent(Login.this, TrendieProfile.class);

            i.putExtra("Handle", info.getHandle());
            i.putExtra("Name", info.getName());

            startActivity(i);

            info.close();
        }
        else{
            Dialog d = new Dialog(this);
            d.setTitle("Invalid Information!");
            TextView tv = new TextView(this);
            tv.setText("Username or password incorrect!");
            d.setContentView(tv);
            d.show();
        }


    }

}
