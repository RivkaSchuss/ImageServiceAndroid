package com.example.yarin.imageserviceandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PictureService extends Service{

    private Socket socket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
            socket = new Socket(serverAddr, 8000);
            try{
                OutputStream output = socket.getOutputStream();
                //FileInputStream fis = new FileInputStream(pic);
                /////////
                //output.write(imgbyte);
                //output.flush();
            }catch (Exception e) {
                Log.e("TCP", "S: Error:", e);
            }
        }
        catch (Exception e) {
            Log.e("TCP", "S: Error:", e);
        }
    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        Toast.makeText(this, "Service starting...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Service ending...", Toast.LENGTH_SHORT).show();

        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e("TCP", "S: Error:", e);
        }
    }
}
