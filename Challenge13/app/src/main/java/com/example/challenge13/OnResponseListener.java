package com.example.challenge13;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by 지민 on 2016-12-24.
 */

///NOTE @jimin API콜에 대한 응답이 들어왔을 때 호출되는 Listener
public interface OnResponseListener
{
    public void onResponseGetListener(int code, ArrayList<WeatherData> responseWeather);
}
