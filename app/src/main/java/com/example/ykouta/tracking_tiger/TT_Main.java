package com.example.ykouta.tracking_tiger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TT_Main extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private ArrayList<String> arrayList;
    private Timer timer = null;
    private double latitude,longitude;
    private int flg = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt__main);

        //GPSパーミッションチェック
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        else{
            locationStart();
        }

        arrayList = new ArrayList<>();
        TextView text = (TextView)findViewById(R.id.tablet_id);
        text.setText("ID:"+ Build.SERIAL);

        timerSend();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void locationStart(){
        Log.d("debug","locationStart()");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "拒否されました", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    //位置情報の変更を更新
    @Override
    public void onLocationChanged(Location location) {
        // 緯度の表示
        TextView textView1 = (TextView) findViewById(R.id.text_view1);
        Log.d("debug","location.getLatitude()");
        textView1.setText("緯度:"+location.getLatitude());

        // 経度の表示
        TextView textView2 = (TextView) findViewById(R.id.text_view2);
        Log.d("debug","location.getLongitude()");
        textView2.setText("経度:"+location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        /*Http thread = new Http(this);
        thread.execute(location.getLatitude(),location.getLongitude());*/
        //timerSend();
    }

    //一定時間ごとに位置情報を送信
    public void timerSend(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(latitude != 0.0 && longitude != 0.0) {
                            Http tread = new Http(TT_Main.this);
                            tread.execute("&flag=0&latitude=" + String.valueOf(latitude), "&longitude=" + String.valueOf(longitude));
                        }
                    }
                });
            }
        },0,30000);
    }

    @Override
    public void onDestroy(){
        locationManager.removeUpdates(this);
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() == null) {
            } else {
                if(flg == 1) {
                    if (!arrayList.contains(intentResult.getContents())) {
                        //荷物リスト

                        /*TextView textView = new TextView(this);
                        textView.setText(intentResult.getContents());

                        TableLayout tableLayout = (TableLayout)findViewById(R.id.qr);
                        TableRow tableRow = new TableRow(this);
                        tableRow.addView(textView);*/

                        ListView listview = (ListView) findViewById(R.id.list);
                        arrayList.add(intentResult.getContents());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, arrayList);
                        listview.setAdapter(adapter);

                        Http baggage = new Http(TT_Main.this);
                        baggage.execute("&flag=1&Baggage_ID=" + intentResult.getContents());
                    } else {
                        Toast.makeText(this, "積み込み済みです。", Toast.LENGTH_SHORT).show();
                    }
                }else if(flg == 2){
                    if (arrayList.contains(intentResult.getContents())) {
                        //荷物リスト
                        ListView listview = (ListView) findViewById(R.id.list);
                        int index = arrayList.indexOf(intentResult.getContents());
                        arrayList.remove(index);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, arrayList);
                        listview.setAdapter(adapter);

                        Http baggage = new Http(TT_Main.this);
                        baggage.execute("&flag=2&Baggage_ID=" + intentResult.getContents());
                    } else {
                        Toast.makeText(this, "積み降ろし済みです。", Toast.LENGTH_SHORT).show();
                    }
                }

                /*<TextView
                    android:id="@+id/text"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content" />
                TextView textView = (TextView) findViewById(R.id.text);
                textView.setText(intentResult.getContents());*/

                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_button:
                flg = 1;
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();

                break;
            case R.id.read_button2:
                flg = 2;
                IntentIntegrator integrator2 = new IntentIntegrator(this);
                integrator2.setBeepEnabled(false);
                integrator2.initiateScan();

                break;
        }
    }
}
