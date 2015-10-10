package com.ampt.bluetooth.activities.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ampt.bluetooth.Adapters.DogsAdapter;
import com.ampt.bluetooth.R;
import com.ampt.bluetooth.activities.AddDogActivity;
import com.ampt.bluetooth.activities.DogScanActivity;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

/**
 * Created by malith on 7/31/15.
 */
public class Tab_Dogs_activity extends Activity {
    private ListView lv;
    private DogsAdapter dogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_dogs_layout);
        // DatabaseHelper db = new DatabaseHelper(this);
        // db.addDog(new DogsData("aaaa","1",true,"11111111","222222",2,"sdf","sdfdsf"));

        setAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(lv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Tab_Dogs_activity.this, DogScanActivity.class), 123);
            }
        });
    }

    public void setAdapter() {
        lv = (ListView) findViewById(R.id.listViewdogs);
        List<DogsData> dogsList = new DatabaseHelper(Tab_Dogs_activity.this).getAllDogProfile();
        Log.i("TAB DOGS", " dogs count : " + dogsList.size());
        dogsAdapter = new DogsAdapter(Tab_Dogs_activity.this, R.layout.dogs_row, dogsList);
        lv.setAdapter(dogsAdapter);
        dogsAdapter.setNotifyOnChange(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 123) {
            setAdapter();
        }
    }
}