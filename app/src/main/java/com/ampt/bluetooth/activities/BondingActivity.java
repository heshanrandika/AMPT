package com.ampt.bluetooth.activities;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.ampt.bluetooth.R;


/**
 * Created by Heshanr on 4/13/2015.
 */
public class BondingActivity extends Activity {
    public static final BluetoothDevice EXTRAS_DOG_ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device_bonding);
        final Intent intent = getIntent();


    }
}
