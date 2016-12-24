package com.example.challenge13;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by 지민 on 2016-12-03.
 */

public interface OnDataSelectionListener
{
    public void onDataSelected(AdapterView parent, View v, int position, long id);
}