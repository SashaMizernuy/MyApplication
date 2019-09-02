package com.example.myapplication;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.util.Pools;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class MyService extends IntentService {

    ServerSocket serverSocket;
    Handler handler;
    String message ="";
    NotificationManager notificationManager;
    NotificationCompat.Builder notification;
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGETUDE = "extra_longetude";
    public static final String ACTION_LOCATION_BROADCAST="SASHA";



    public MyService() {
        super(" ");
    }


    public class LocalBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }


    private final IBinder myBinder=new LocalBinder();


    @Override
    public IBinder onBind(Intent intent){
        return myBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Script","onHandleIntent");




    }
    public void startForegroundService(){

        Log.d("Script", "Start foreground service.");

        //create notification builder
        displayNotification();

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, MyService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        notification.addAction(playAction);

        // Start Server.
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        // Start foreground service.
        startForeground(1,notification.build());


    }

    public void displayNotification(){
        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

         notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle("title")
                .setContentText("task")
                .setSmallIcon(R.mipmap.ic_launcher);
        // Make the notification max priority.
        notification.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        notification.setFullScreenIntent(pendingIntent, true);

    }




    @Override
    public void onCreate() {
        handler=new Handler();
        super.onCreate();
        Log.i("Script","OnCreate");
    }


    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }




@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Script", "MyService onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    return START_STICKY;
    }


//    private void displayNotification(String title, String task) {
//        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
//                .setContentTitle(title)
//                .setContentText(task)
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//        notificationManager.notify(3, notification.build());
//    }


            class SocketServerThread extends Thread {

                static final int SocketServerPORT = 8080;
                int count = 0;

                @Override
                public void run() {
                    try {
                        serverSocket = new ServerSocket(SocketServerPORT);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //                    info.setText("I'm waiting here: "
                                //                            + serverSocket.getLocalPort());
                                Log.i("Script", "I'm waiting here: " + serverSocket.getLocalPort());
                            }
                        });

                        while (true) {
                            Socket socket = serverSocket.accept();
                            count++;

                            Log.i("Script", "Ip address=" + "#" + count + " from " + socket.getInetAddress()
                                    + ":" + socket.getPort() + "\n");

                            message += "#" + count + " from " + socket.getInetAddress()
                                    + ":" + socket.getPort() + "\n";

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //msg.setText(message);
                                    Log.i("Script", "message=" + message);

                                }
                            });

                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                                    socket, count);
                            socketServerReplyThread.run();

                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("Script", "server accept=" + e.getMessage());
                    }
                }
            }

            class SocketServerReplyThread extends Thread {

                private Socket hostThreadSocket;
                private BufferedReader input;
                int cnt;


                SocketServerReplyThread(Socket socket, int c) {
                    hostThreadSocket = socket;
                    cnt = c;
                    try {
                        this.input = new BufferedReader(new InputStreamReader(this.hostThreadSocket.getInputStream()));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("Script", "SocketReplyThread" + e.getMessage());
                    }
                }

                @Override
                public void run() {
                    OutputStream outputStream;
                    String msgReply = "Hello from Android, you are #" + cnt;

                    try {

                        String read = input.readLine();

                        handler.post(new updateUIThread(read));
                        outputStream = hostThreadSocket.getOutputStream();
                        PrintStream printStream = new PrintStream(outputStream);
                        printStream.print(msgReply);
                        printStream.close();
                        message += "replayed: " + msgReply + "\n";
                        Log.i("Script", "messasge= " + "replayed: " + msgReply + "\n");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // msg.setText(message);
                                Log.i("Script", "REPLY_RUNNABLE" + message);
                            }
                        });

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("Script", "Handler Output=" + e.getMessage());
                        message += "Something wrong! " + e.toString() + "\n";
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //msg.setText(message);
                            Log.i("Script", "RUNLAST " + message);
                        }
                    });
                }


                class updateUIThread implements Runnable {
                    private String msg;

                    public updateUIThread(String str) {
                        this.msg = str;
                        Log.i("Script", "Client Says: " + msg + "\n");
                        String[] latLngMessage=str.split(",");
                        String lat="",lng="";
                            lat = latLngMessage[0];
                            lng = latLngMessage[1];
                            //sendMessageToUI(lat,lng);
                        sendMessageToUI(lat,lng);

                    }

                    @Override
                    public void run() {
                        //editText.setText(editText.getText().toString()+"Client Says: "+ msg + "\n");
//                        Log.i("Script", "Client Says: " + msg + "\n");

                    }
                }
            }

    private void sendMessageToUI(String lat, String lng) {

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGETUDE,lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }


    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.i("Script", "onDestroy");

        if (serverSocket != null) {
            try {
                serverSocket.close();
                Log.i("Script", "Socket close");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("Script", "onDestroy= " + e.getMessage());
            }
        }
    }
}
