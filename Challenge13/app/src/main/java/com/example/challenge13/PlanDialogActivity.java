package com.example.challenge13;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;

public class PlanDialogActivity extends AppCompatActivity
{
    private EditText txtPlanMessage;
    private EditText txtHour;
    private EditText txtMinute;

    private Button btnSunny;
    private Button btnCloudy;
    private Button btnRain;
    private Button btnSnow;

    private Button btnSave;
    private Button btnClose;

    private View.OnClickListener clickListener;
    private View.OnClickListener weatherClickListener;

    private String latitude;
    private String longitude;

    private String selectedWeather;

    private WeatherManager weatherManager;
    private OnResponseListener responseListener;
    private ArrayList<WeatherData> weatherDataArrayList;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_dialog);

        txtPlanMessage = (EditText) findViewById(R.id.txtPlanMessage);
        txtHour = (EditText) findViewById(R.id.txtHour);
        txtMinute = (EditText) findViewById(R.id.txtMinute);

        btnSunny = (Button) findViewById(R.id.btnSunny);
        btnCloudy = (Button) findViewById(R.id.btnCloudy);
        btnRain = (Button) findViewById(R.id.btnRain);
        btnSnow = (Button) findViewById(R.id.btnSnow);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnClose = (Button) findViewById(R.id.btnClose);

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch( view.getId() )
                {
                    case R.id.btnSave :
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra( "message", txtPlanMessage.getText().toString() );
                        resultIntent.putExtra( "hour", txtHour.getText().toString() );
                        resultIntent.putExtra( "min", txtMinute.getText().toString() );
                        resultIntent.putExtra( "weather", selectedWeather.toString() );
                        setResult( RESULT_OK, resultIntent );
                        finish();
                        break;
                    case R.id.btnClose :
                        finish();
                        break;
                }
            }
        };

        weatherClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch ( view.getId() )
                {
                    case R.id.btnSunny :
                        selectedWeather = "sunny";
                        break;
                    case R.id.btnCloudy :
                        selectedWeather = "cloudy";
                        break;
                    case R.id.btnRain :
                        selectedWeather = "rain";
                        break;
                    case R.id.btnSnow :
                        selectedWeather = "snow";
                        break;
                }

                setWeather();
            }
        };

        responseListener = new OnResponseListener() {
            @Override
            public void onResponseGetListener(ArrayList<WeatherData> responseWeather)
            {
                weatherDataArrayList = responseWeather;

                selectedWeather = convertCloudyString( weatherDataArrayList.get(0).getCloudy() );
                setWeather();
            }
        };

        btnSave.setOnClickListener( clickListener );
        btnClose.setOnClickListener( clickListener );

        btnSunny.setOnClickListener( weatherClickListener );
        btnCloudy.setOnClickListener( weatherClickListener );
        btnRain.setOnClickListener( weatherClickListener );
        btnSnow.setOnClickListener( weatherClickListener );

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        selectedYear = getIntent().getIntExtra("year", 0);
        selectedMonth = getIntent().getIntExtra("month", 0);
        selectedDay = getIntent().getIntExtra("day", 0);

        weatherManager = new WeatherManager();
        weatherManager.setOnResponseListener( responseListener );

        Date now = new Date();
        Date selected = new Date( Date.UTC(selectedYear, selectedMonth, selectedDay, 0, 0, 0) );

        selectedWeather = "";

        String message = getIntent().getStringExtra("message");
        String hour = getIntent().getStringExtra("hour");
        String min = getIntent().getStringExtra("min");
        String weather = getIntent().getStringExtra("weather");

        if( message != null )
        {
            txtPlanMessage.setText( message );
        }

        if( hour != null )
        {
            txtHour.setText( hour );
        }

        if( min != null )
        {
            txtMinute.setText( min );
        }

        if( weather != null && !weather.equals("") )
        {
            selectedWeather = weather;
            setWeather();
        }
        else
        {
            weatherManager.getWeather(latitude, longitude);
        }
    }

    @Override
    protected void onDestroy()
    {
        btnSave.setOnClickListener( null );
        btnClose.setOnClickListener( null );

        btnClose = null;
        btnSave = null;

        txtPlanMessage = null;
        txtHour = null;
        txtMinute = null;

        clickListener = null;

        super.onDestroy();
    }

    private void setWeather()
    {
        btnSunny.setBackgroundColor(Color.BLACK);
        btnCloudy.setBackgroundColor(Color.BLACK);
        btnRain.setBackgroundColor(Color.BLACK);
        btnSnow.setBackgroundColor(Color.BLACK);

        if( selectedWeather.equals("sunny") )
        {
            btnSunny.setBackgroundColor(Color.YELLOW);
        }
        else if( selectedWeather.equals("cloudy") )
        {
            btnCloudy.setBackgroundColor(Color.YELLOW);
        }
        else if( selectedWeather.equals("rain") )
        {
            btnRain.setBackgroundColor(Color.YELLOW);
        }
        else if( selectedWeather.equals("snow") )
        {
            btnSnow.setBackgroundColor(Color.YELLOW);
        }
    }

    private String convertCloudyString(String input)
    {
        String output = "";

        if( input.indexOf("sky") >= 0 )
        {
            if( input.indexOf("clear") >= 0 )
            {
                output = "sunny";
            }
            else
            {
                output = "cloudy";
            }
        }
        else if( input.indexOf("snow") >= 0 )
        {
            output = "snow";
        }
        else if( input.indexOf("rain") >= 0 )
        {
            output = "rain";
        }
        else if( input.indexOf("clouds") >= 0 )
        {
            output = "cloudy";
        }

        return output;
    }
}
