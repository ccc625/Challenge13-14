package com.example.challenge13;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by 지민 on 2016-12-03.
 */

public class CalendarMonthView extends GridView
{
    private OnDataSelectionListener selectionListener;
    CalendarMonthAdapter adapter;

    public CalendarMonthView(Context context)
    {
        super(context);

        init();
    }

    public CalendarMonthView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    private void init()
    {
        setBackgroundColor(Color.GRAY);
        setVerticalSpacing(1);
        setHorizontalSpacing(1);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setNumColumns(7);

        setOnItemClickListener( new OnItemClickAdapter() );
    }

    public void setAdapter(BaseAdapter adapter)
    {
        super.setAdapter(adapter);

        this.adapter = (CalendarMonthAdapter) adapter;
    }

    public BaseAdapter getAdapter()
    {
        return (BaseAdapter) super.getAdapter();
    }

    public void setOnDataSelectionListener(OnDataSelectionListener listener)
    {
        this.selectionListener = listener;
    }

    public OnDataSelectionListener getOnDataSelectionListener()
    {
        return selectionListener;
    }

    class OnItemClickAdapter implements OnItemClickListener
    {
        public OnItemClickAdapter()
        {

        }

        public void onItemClick( AdapterView parent, View v, int position, long id )
        {
            if( adapter != null )
            {
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetInvalidated();
            }

            if( selectionListener != null )
            {
                selectionListener.onDataSelected(parent, v, position, id);
            }
        }
    }
}