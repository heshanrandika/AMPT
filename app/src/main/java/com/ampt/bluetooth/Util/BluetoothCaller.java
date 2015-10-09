/*package com.ampt.bluetooth.Util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.SampleGattAttributes;
import com.ampt.bluetooth.activities.BluetoothLeService;
import com.ampt.bluetooth.activities.DeviceControlActivity;
import com.ampt.bluetooth.chartView;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;
import com.ampt.bluetooth.database.model.DogsData;
import com.ampt.bluetooth.singleChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

*//**
 * Created by Heshan on 10/8/2015.
 *//*
public class BluetoothCaller {
    public String mDeviceAddress ="";
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String COLLAR_CLICK = "5";
    public static final String SYNC_PRESS = "2";



    //  private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;


    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    public BluetoothCaller(String mDeviceAddress){
        this.mDeviceAddress = mDeviceAddress;




    }












    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //TODO
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                saveData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);






    }



    public void onResume() {

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    public void onPause() {
        unregisterReceiver(mGattUpdateReceiver);
    }


    public void onDestroy() {
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }




    private void saveData(String data) {
        if (data != null) {
            Log.w("XXXXXXXXXXXXXXXXXXX","FFFFFFFFFFFFFFFFF"+data);
            String [] Data = data.split(" ");
            ActivityData activityData = new ActivityData();
            activityData.setDogId((int) dog_id);
            activityData.setSleep(Integer.parseInt(Data[0]));
            activityData.setWalk(Integer.parseInt(Data[1]));
            activityData.setPlay(Integer.parseInt(Data[2]));
            activityData.setSwimming(Integer.parseInt(Data[3]));
            daf.createDogActivity(activityData);
            Log.w("XXXXXXXXXXXXXXXXXXX","FFFFFFFFFFFFFFFFF DONE "+Data[0]);
            setActivityData();
        }
    }


    private void setActivityData(){
        ArrayList<ActivityData> activityList = daf.getAllActivityDogDateRange(dog_id,0);
        int slp = 0,wlk = 0,ply = 0,swm = 0;
        for (ActivityData listItem : activityList){
            slp += listItem.getSleep();
            wlk += listItem.getWalk();
            ply += listItem.getPlay();
            swm += listItem.getSwimming();
        }
        int slpH = slp/60; int slpM = slp%60;
        int wlkH = wlk/60; int wlkM = wlk%60;
        int plyH = ply/60; int plyM = ply%60;
        int swmH = swm/60; int swmM = swm%60;

       *//* sleepTime.setText(slpH+"h "+slpM+"min");
        walkTime.setText(wlkH+"h "+wlkM+"min");
        playTime.setText(plyH+"h "+plyM+"min");
        swimTime.setText(swmH+"h "+swmM+"min");*//*
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    // on change of bars write char
    private void makeChange(String value) {
        String str = value+"";
        Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }







}*/
