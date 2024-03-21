package com.example.indoorpointer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AndroidExample";
    private static final int MY_REQUEST_CODE = 123;
    private WifiManager wifiManager;
    private Button submit;
    private EditText roll;
    private EditText pass;
    private WifiBroadcastReceiver wifiReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("-------------------------------onCreate_Method----------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.wifiReceiver = new WifiBroadcastReceiver();
        this.roll=(EditText)this.findViewById(R.id.roll_no);
        this.pass=(EditText)this.findViewById(R.id.password);
        this.submit=(Button) this.findViewById(R.id.submit);
        
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login auth=new login();
                Boolean verify=auth.auth(roll.getText().toString(),pass.getText().toString());
                if(verify)
                {
                    setContentView(R.layout.activity_main);
                    askAndStartScanWifi();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Crediantials MissMatch!", Toast.LENGTH_SHORT).show();
                    System.out.println("-------------------------------Auth Fail----------------------------------------");
                }

            }
        });

//        askAndStartScanWifi();
//        setContentView(R.layout.activity_main);
    }



    private void askAndStartScanWifi() {

        // With Android Level >= 23, you have to ask the user
        // for permission to Call.
        String user = this.roll.getText().toString();
        System.out.println("------------------------------"+user+"--------------------");
        System.out.println("-------------------------------askAndStartScanWifi----------------------------------------");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
            int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            // Check for permissions
            if (permission1 != PackageManager.PERMISSION_GRANTED) {

                Log.d(LOG_TAG, "Requesting Permissions");

                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE
                        }, MY_REQUEST_CODE);
                return;
            }
            Log.d(LOG_TAG, "Permissions Already Granted");
        }
        for (int i=0;i<=5;i++) {
            this.doStartScanWifi();
        }
//        this.doStartScanWifi();
    }

    private void doStartScanWifi()  {
        System.out.println("------------------------------doStartScanWifi------------------------------");
        this.wifiManager.startScan();
    }


    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("--------------------------------OnReceive_Method---------------------------------------");
            Log.d(LOG_TAG, "onReceive()");

            Toast.makeText(MainActivity.this, "Scan Complete!", Toast.LENGTH_SHORT).show();

//            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            boolean ok=true;

            if (ok) {
                Log.d(LOG_TAG, "Scan OK");

                List<ScanResult> results = wifiManager.getScanResults();
//
//                MainActivity.this.showNetworks(results);
                for (final ScanResult result : results) {
                    System.out.println("----------------------------data manupulation------------------");
                    final String networkCapabilities = result.capabilities;
                    final String networkSSID = result.SSID; // Network Name.
                    //
                    int RSSI = result.level; //RSSI
                   System.out.println(networkSSID + " (" + networkCapabilities + ")" + RSSI);


                    SocketClient socketClient = new SocketClient("172.20.10.5", 555);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socketClient.connect();
                                socketClient.send(networkSSID + " (" + networkCapabilities + ")" + RSSI );
                                String response = socketClient.receive();
                                Log.d("SocketClient", "Received: " + response );
                                socketClient.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            } else {
                Log.d(LOG_TAG, "Scan not OK");
                Toast.makeText(MainActivity.this, "Scan Not Complete!", Toast.LENGTH_SHORT).show();

            }
            System.out.println("---------------------out of thread-----------------------");


        }
    }
    @Override
    protected void onStop()  {
        this.unregisterReceiver(this.wifiReceiver);
        super.onStop();
    }
}