package com.example.challenge13;

/**
 * Created by 지민 on 2016-12-03.
 */

public class MonthItem
{
    private int dayValue;

    public MonthItem()
    {

    }
    public MonthItem( int day )
    {
        dayValue = day;
    }

    public int getDay()
    {
        return dayValue;
    }

    public void setDay( int day )
    {
        this.dayValue = day;
    }
}

