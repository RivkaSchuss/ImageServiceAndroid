package com.example.yarin.imageserviceandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    /**
     * the function called when the application is activated.
     * @param savedInstanceState the instance state save
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * the function called when the start service button is pressed
     * @param view the current view
     */
    public void startService(View view) {

        Intent intent = new Intent(this, PictureService.class);
        startService(intent);
    }

    /**
     * the function called when the stop service button is pressed
     * @param view the current view
     */
    public void stopService(View view) {
        Intent intent = new Intent(this, PictureService.class);
        stopService(intent);
    }
}