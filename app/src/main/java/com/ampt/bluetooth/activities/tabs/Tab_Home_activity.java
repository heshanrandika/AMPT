package com.ampt.bluetooth.activities.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ampt.bluetooth.R;

/**
 * Created by malith on 7/31/15.
 */
public class Tab_Home_activity  extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        TextView txtView = new TextView(this);
        txtView.setText("Second Tab is Selected");
        setContentView(R.layout.tab_home_layout);
    }
}