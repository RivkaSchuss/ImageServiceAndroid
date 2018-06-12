package com.example.yarin.imageserviceandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //gets the intent that started this activity.
        Intent intent = getIntent();

        //extracts the message.
        String msg = intent.getStringExtra("message");

        //displays the message on the textview.
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(msg);
    }
}