package com.example.challenge13;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1101;
    private static final int UPDATE_REQUEST = 101;
    private static final int REMOVE_REQUEST = 102;

    private HashMap hashData;

    private MonthItem currentSelectedDay;
    private int curYear;
    private int curMonth;

    private Button btnPrev;
    private Button btnNext;
    private TextView txtMonth;

    private Button btnSaveWeather;

    private CalendarMonthView calendarMonthView;
    private CalendarMonthAdapter calendarMonthAdapter;

    private View.OnClickListener clickListener;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location currentLocation;

    private int currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        txtMonth = (TextView) findViewById(R.id.txtMonth);

        btnSaveWeather = (Button) findViewById(R.id.btnSaveWeather);

        calendarMonthView = (CalendarMonthView) findViewById(R.id.calendarMonthView);

        calendarMonthAdapter = new CalendarMonthAdapter(this);
        hashData = new HashMap();

        calendarMonthAdapter.setData(hashData);
        calendarMonthView.setAdapter(calendarMonthAdapter);

        setMonthText();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        requestLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String key = curYear + "/" + curMonth + "/" + currentSelectedDay.getDay();
                String message = data.getStringExtra("message");
                String hour = data.getStringExtra("hour");
                String min = data.getStringExtra("min");
                String weather = data.getStringExtra("weather");

                DayData dayData = new DayData();
                dayData.setMessage(message);
                dayData.setHour(hour);
                dayData.setMin(min);
                dayData.setWeather(weather);

                hashData.put(key, dayData);

                calendarMonthAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ///TODO 권한 허가 해당 권한을 사용해서 작업을 진행
                    switch ( currentRequest )
                    {
                        case UPDATE_REQUEST :
                            requestLocationUpdates();
                            break;
                        case REMOVE_REQUEST :
                            removeLocationUpdates();
                            break;
                        default :
                            break;
                    }
                    currentRequest = 0;
                } else {
                    ///TODO 권한 거부 사용자가 해당 권한을 거부했을 때 해주어야할 동작을 수행
                }
                break;
        }
    }

    private void initListener()
    {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if( view == btnPrev )
                {
                    calendarMonthAdapter.setPreviousMonth();
                    calendarMonthAdapter.notifyDataSetChanged();

                    setMonthText();
                }
                else if( view == btnNext )
                {
                    calendarMonthAdapter.setNextMonth();
                    calendarMonthAdapter.notifyDataSetChanged();

                    setMonthText();
                }
            }
        };

        btnPrev.setOnClickListener( clickListener );
        btnNext.setOnClickListener( clickListener );

        calendarMonthView.setOnDataSelectionListener(new OnDataSelectionListener() {
            @Override
            public void onDataSelected(AdapterView parent, View v, int position, long id)
            {
                currentSelectedDay = (MonthItem) calendarMonthAdapter.getItem( position );

                openDialogActivity();
            }
        });

        btnSaveWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openDialogActivity();
            }
        });
    }

    private void openDialogActivity()
    {
        if( currentLocation == null )
            return;

        String key = curYear + "/" + curMonth + "/" + currentSelectedDay.getDay();
        DayData dayData = (DayData) hashData.get(key);

        Intent intent = new Intent(getApplicationContext(), PlanDialogActivity.class);

        if( dayData != null )
        {
            intent.putExtra("message", dayData.getMessage());
            intent.putExtra("hour", dayData.getHour());
            intent.putExtra("min", dayData.getMin());
            intent.putExtra("weather", dayData.getWeather());
        }

        intent.putExtra( "latitude", String.valueOf( currentLocation.getLatitude() ) );
        intent.putExtra( "longitude", String.valueOf( currentLocation.getLongitude() ) );
        intent.putExtra( "year", curYear );
        intent.putExtra( "month", curMonth );
        intent.putExtra( "day", currentSelectedDay.getDay() );
        startActivityForResult(intent, REQUEST_CODE );
    }

    private void setMonthText()
    {
        curYear = calendarMonthAdapter.getCurYear();
        curMonth = calendarMonthAdapter.getCurMonth();

        txtMonth.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    private void requestLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                currentRequest = UPDATE_REQUEST;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // 다이어로그를 띄워서 사용자에게 권한이 필요한 이유에 대해 설명
                // 해당 설명이끝난뒤 requestPermissions()를 호출 하여 권한 허가를 요청
            }
            else
            {
                currentRequest = UPDATE_REQUEST;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                //필요한 권한과 요청 코드를 넣어서 권한허가 요청에 대한 결과를 받아야 합니다.
            }
        }
        else
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            initListener();
        }
    }

    private void removeLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                currentRequest = REMOVE_REQUEST;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // 다이어로그를 띄워서 사용자에게 권한이 필요한 이유에 대해 설명
                // 해당 설명이끝난뒤 requestPermissions()를 호출 하여 권한 허가를 요청
            }
            else
            {
                currentRequest = REMOVE_REQUEST;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                //필요한 권한과 요청 코드를 넣어서 권한허가 요청에 대한 결과를 받아야 합니다.
            }
        }
        else
        {
            locationManager.removeUpdates(locationListener);
        }
    }
}
