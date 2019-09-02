package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class AddNoteActivity extends AppCompatActivity {
    public static final String EXTRA_LATITUDE=
            "com.example.myapplication.EXTRA_LATITUDE";
    public static final String EXTRA_LONGETUDE=
            "com.example.myapplication.EXTRA_LONGETUDE";
    public static final String EXTRA_PRIORITY=
            "com.example.myapplication.EXTRA_PRIORITY";
    public final static String BROADCAST_ACTION = "com.example.myapplication";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_STATUS = "status";
    public final static String PARAM_RESULT = "result";


    private TextView Lat;
    private TextView Lng;
    private NumberPicker numberPickerPriority;
    String lat,lng;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Lat = findViewById(R.id.text_lat);
        Lng = findViewById(R.id.text_lng);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);






        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {


                        lat = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                        lng = intent.getStringExtra(MyService.EXTRA_LONGETUDE);

                        Log.i("Script","mes= "+lat);
                        Lat.setText(lat);
                        Lng.setText(lng);


            }
        }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST));












        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        setTitle("add Note");

        Intent intent = new Intent(AddNoteActivity.this,MyService.class);
        intent.setAction(MyService.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);// Запуск сервиса

        Log.i("Script","My ip Address= "+getIpAddress());

    }


    public void saveNote(){


        String lat=Lat.getText().toString();
        String lng=Lng.getText().toString();
        int priority=numberPickerPriority.getValue();

        if(lat.trim().isEmpty()||lng.trim().isEmpty()){
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data=new Intent();
        data.putExtra(EXTRA_LATITUDE,lat);
        data.putExtra(EXTRA_LONGETUDE,lng);
        data.putExtra(EXTRA_PRIORITY,priority);

        setResult(RESULT_OK,data);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";

                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

}
