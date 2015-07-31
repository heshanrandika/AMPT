package com.ampt.bluetooth.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.activities.tabs.Tab_Dogs_activity;
import com.ampt.bluetooth.activities.tabs.Tab_Home_activity;
import com.ampt.bluetooth.activities.tabs.Tab_activities_activity;


/**
 * Created by malith on 7/31/15.
 */
public class TabbedMainActivity extends TabActivity{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main_layout);



        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, Tab_Home_activity.class);
        spec = tabHost.newTabSpec("Home").setIndicator("Home")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Tab_activities_activity.class);
        spec = tabHost.newTabSpec("Activities").setIndicator("Activities")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Tab_Dogs_activity.class);
        spec = tabHost.newTabSpec("Dogs").setIndicator("Dogs")
                .setContent(intent);
        tabHost.addTab(spec);


    }
}