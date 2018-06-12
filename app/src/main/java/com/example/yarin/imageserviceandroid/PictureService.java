package com.example.yarin.imageserviceandroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PictureService extends Service{

    private Socket socket;
    private BroadcastReceiver receiver;
    private OutputStream outputStream;

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
                outputStream = socket.getOutputStream();
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

        establishWifi();

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

    public void establishWifi() {
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        theFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(wifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //get the different network states
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer(); // Starting the Transfer
                        }
                    }
                }
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.receiver, theFilter);
    }

    public void startTransfer() {
        // Getting the Camera Folder
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim == null) {
            return;
        }
        File[] pics = dcim.listFiles();
        int count =0;
        if (pics != null) {
            for (File pic : pics) {
                try {
                    FileInputStream fis = null;
                    fis = new FileInputStream(pic);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    byte[] imgbyte = getBytesFromBitmap(bm);
                    outputStream.write(imgbyte);
                    outputStream.flush();
                    count++;
                } catch(Exception e) {
                    Log.e("TCP", "S: Error:", e);
                }
            }
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}
