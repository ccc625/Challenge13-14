package com.example.challenge13;

import android.os.Handler;
import android.provider.DocumentsContract;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by 지민 on 2016-12-24.
 */

///NOTE @jimin OpenWeatherAPI를 이용하여 오늘부터 +15일 까지의 날씨를 받아오는 클래스
public class WeatherManager
{
    private static final String api = "http://api.openweathermap.org/data/2.5/forecast/daily?&APPID=%s&lat=%s&lon=%s&mode=xml&units=metric&cnt=15";
    private static final String key = "dc49382e1dcc49fa2a8bbd3af97f05d0";

    private OnResponseListener responseListener;
    int currentResCode;

    public Handler handler;

    public WeatherManager()
    {
        handler = new Handler();
    }

    public void getWeather(String latitude, String longitude)
    {
        String url = String.format(api, key, latitude, longitude);

        ConnectThread thread = new ConnectThread( url );
        thread.start();
    }

    public void setOnResponseListener( OnResponseListener listener )
    {
        this.responseListener = listener;
    }

    public class ConnectThread extends Thread
    {
        private String urlStr;

        public ConnectThread(String inStr)
        {
            urlStr = inStr;
        }

        @Override
        public void run()
        {
            try
            {
                final Document output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        ///NOTE @jimin API콜에대한 응답을 받게되면 파싱과정을 거친 후 listener를 호출 한다.
                        ArrayList<WeatherData> weatherDataArrayList = new ArrayList<WeatherData>();

                        if( output != null )
                        {
                            Element element = output.getDocumentElement();
                            Element forecast = (Element) element.getElementsByTagName("forecast").item(0);
                            NodeList times = forecast.getElementsByTagName("time");

                            WeatherData weatherDayData;
                            for (int i = 0; i < times.getLength(); i++) {
                                Element time = (Element) times.item(i);
                                Element symbol = (Element) time.getElementsByTagName("symbol").item(0);

                                weatherDayData = new WeatherData();
                                weatherDayData.setDay(time.getAttribute("day"));
                                weatherDayData.setCloudy(symbol.getAttribute("name"));

                                weatherDataArrayList.add(weatherDayData);
                            }
                            weatherDayData = null;
                        }

                        if( responseListener != null )
                            responseListener.onResponseGetListener(currentResCode, weatherDataArrayList);
                    }
                });
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        private Document request(String urlStr)
        {
            Document document = null;
            try
            {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if( conn != null )
                {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    int resCode = conn.getResponseCode();

                    currentResCode = resCode;

                    document = builder.parse( conn.getInputStream() );
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return document;
        }
    }
}
