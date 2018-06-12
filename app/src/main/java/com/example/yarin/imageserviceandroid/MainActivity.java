package com.example.yarin.imageserviceandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);

        //creates the intent
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        String msg = editText.getText().toString();

        //adds data to the intent.
        intent.putExtra("message", msg);

        //starts an instance of the activity specified by the intent.
        startActivity(intent);
    }
}