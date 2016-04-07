package com.baseballforlife7795gmail.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Kyle on 3/31/2016.
 */
public class CreateAccount extends TrendieProfile {

    private EditText nameText;
    private EditText handleText;
    private EditText passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendie_create_account);

        nameText = (EditText) findViewById(R.id.nameText);
        handleText = (EditText) findViewById(R.id.handleText);
        passwordText = (EditText) findViewById(R.id.passwordText);


    }

    public void onCreate(View v) {
        boolean didItWork = true;
        try {
            String name = nameText.getText().toString();
            String handle = handleText.getText().toString();

            Database entry = new Database(CreateAccount.this);
            entry.open();

            entry.createEntry(name, handle);

            entry.close();
        }catch (Exception e){
            didItWork = false;
            String error = e.toString();
            Dialog d = new Dialog(this);
            d.setTitle("Dang it!");
            TextView tv = new TextView(this);
            tv.setText(error);
            d.setContentView(tv);
            d.show();
        }finally {
            if(didItWork){
                Dialog d = new Dialog(this);
                d.setTitle("Heck Yea! " + handleText.getText().toString());
                TextView tv = new TextView(this);
                tv.setText("success");
                d.setContentView(tv);
                d.show();
                Intent i = new Intent(CreateAccount.this, Login.class);
                startActivity(i);
            }
        }
    }
}
