package com.example.yarin.imageserviceandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class PictureService extends Service {

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
                    socket = new Socket(serverAddr, 8600);
                    try {
                        outputStream = socket.getOutputStream();
                    } catch (Exception e) {
                        Log.e("TCP", "S: Error:", e);
                    }
                } catch (Exception e) {
                    Log.e("TCP", "S: Error:", e);
                }
            }
        });
        thread.start();


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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(wifiManager.EXTRA_NETWORK_INFO);

                final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("default", "progress", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
                builder.setContentTitle("Picture Transfer").setContentText("Transfer in progress").setSmallIcon(R.drawable.ic_launcher_background);


                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //get the different network states
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer(builder, notificationManager); // Starting the Transfer
                        }
                    }
                }
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.receiver, theFilter);
    }

    public void startTransfer(final NotificationCompat.Builder builder, final NotificationManager manager) {
        // Getting the Camera Folder
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File dcim = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                if (dcim == null) {
                    return;
                }
                File[] files = dcim.listFiles();
                int count = 0;
                if (files != null) {
                    for (File file : files) {
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            Bitmap bm = BitmapFactory.decodeStream(fis);
                            byte[] imgbyte = getBytesFromBitmap(bm);
                            try {
                                int i = imgbyte.length;

                                //sends the size of the array bytes.
                                String toSend = imgbyte.length + "";
                                outputStream.write(toSend.getBytes(), 0, toSend.getBytes().length);
                                outputStream.flush();
                                Thread.sleep(100);

                                //sends the name of file.
                                toSend = file.getName();
                                outputStream.write(toSend.getBytes(), 0, toSend.getBytes().length);
                                outputStream.flush();
                                Thread.sleep(100);

                                //sends the array bytes.
                                outputStream.write(imgbyte, 0, imgbyte.length);
                                outputStream.flush();
                                Thread.sleep(500);

                            } catch (Exception e) {
                                Log.e("TCP", "S: Error:", e);
                            }
                        } catch (Exception e) {
                            Log.e("TCP", "S: Error:", e);
                        }
                        count++;
                        int progress = (count/files.length) * 100;
                        builder.setProgress(100, progress, false);
                        builder.setContentText(progress + "%");
                        manager.notify(1, builder.build());

                    }
                    try {
                        String toSend = "End\n";
                        outputStream.write(toSend.getBytes(), 0, toSend.getBytes().length);
                        outputStream.flush();
                        builder.setContentTitle("Transfer Completed");
                        builder.setContentText("Your photos have been backed up!");
                        manager.notify(1, builder.build());

                    } catch (Exception e) {
                        builder.setContentTitle("Error");
                        builder.setContentText("Your photos could not be backed up!");
                        manager.notify(1, builder.build());
                        Log.e("TCP", "S: Error:", e);
                    }
                }
            }
        });

        thread.start();
    }


    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}
