package com.example.challenge13;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 지민 on 2016-12-03.
 */

public class MonthItemView extends TextView
{
    private MonthItem item;

    public MonthItemView(Context context)
    {
        super(context);

        init();
    }

    public MonthItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    private void init()
    {
        setBackgroundColor(Color.WHITE);
    }

    public MonthItem getItem()
    {
        return item;
    }

    public void setItem(MonthItem item)
    {
        this.item = item;

        int day = item.getDay();
        if( day != 0 )
        {
            setText(String.valueOf(day));
        }
        else
        {
            setText("");
        }
    }

    public void setBackGround(int color)
    {
        setBackgroundColor(color);
    }
}
