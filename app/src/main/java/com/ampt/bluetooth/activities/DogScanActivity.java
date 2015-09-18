/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DogScanActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    DatabaseHelper daf = new DatabaseHelper(this);
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static int deviceCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();



        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    private void getDogListFromDB() {
        List<DogsData> persistList = daf.getAllDogProfile();
        if(persistList.size()>0){
            for(DogsData dd : persistList){
                mLeDeviceListAdapter.addDevice(dd);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            deviceCount = persistList.size();
        }
        mLeDeviceListAdapter.addDevice(null);
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                 mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
      /*  mLeDeviceListAdapter.addDevice(null);
        mLeDeviceListAdapter.notifyDataSetChanged();*/
        getDogListFromDB();
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(position == deviceCount){
            final Intent intent = new Intent(this, DeviceScanActivity.class);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }else {
            final DogsData device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;
            final Intent intent = new Intent(this, DogProfileControlActivity.class);
            intent.putExtra(DogProfileControlActivity.EXTRAS_DEVICE_NAME, device.getDeviceName());
            intent.putExtra(DogProfileControlActivity.EXTRAS_DEVICE_ADDRESS, device.getDeviceAddress());
            intent.putExtra(DogProfileControlActivity.EXTRAS_DOG_ID, device.getId()+"");
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<DogsData> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<DogsData>();
            mInflator = DogScanActivity.this.getLayoutInflater();
        }

        public void addDevice(DogsData device) {
            if(!mLeDevices.contains(device)) {
                if(device != null)
                device.setStatus(false);
                mLeDevices.add(device);
            }
        }

        public void invalidateDevice() {
            for(DogsData dgsData : mLeDevices){
                if(dgsData != null )
                    dgsData.setStatus(false);
            }
            notifyDataSetChanged();
        }

        public void updateDevice(BluetoothDevice device) {
            String addrss = device.getAddress();
            for(DogsData dgsData : mLeDevices){
                if(dgsData != null )
                if(dgsData.getDeviceAddress().equals(addrss)){
                    dgsData.setStatus(true);
                }
            }
        }

        public DogsData getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            invalidateDevice();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if(i == deviceCount){
                if (view == null) {
                    view = mInflator.inflate(R.layout.add_item, null);

                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                return view;
            }else {
                if (view == null) {
                    view = mInflator.inflate(R.layout.device_item, null);
                    viewHolder = new ViewHolder();
                    //viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                    viewHolder.dogName = (TextView) view.findViewById(R.id.dog_name);
/*                    viewHolder.dogAge = (TextView) view.findViewById(R.id.dog_age);
                    viewHolder.dogGoal= (TextView) view.findViewById(R.id.dog_goal);
                    viewHolder.dogImage = (ImageView) view.findViewById(R.id.dog_image);*/
                    viewHolder.bluetooth = (ImageView) view.findViewById(R.id.ble_image);

                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                DogsData dogsData = mLeDevices.get(i);
                final String dogsDataName = dogsData.getName();
//                final int dogsDataAge = dogsData.getAge();
//                final String dogsDataGoal = dogsData.getGoal();
                if (dogsDataName != null && dogsDataName.length() > 0)
                    viewHolder.dogName.setText(dogsDataName);
                else
                    viewHolder.dogName.setText(R.string.unknown_device);

               /* if (dogsDataAge != 0)
                    viewHolder.dogAge.setText(dogsDataAge+"");
                else
                    viewHolder.dogAge.setText("0");

                if (dogsDataGoal != null && dogsDataGoal.length() > 0)
                    viewHolder.dogGoal.setText(dogsDataGoal);
                else
                    viewHolder.dogGoal.setText("");
                if(dogsData.getImageID() !=  null){
//                    byte[] outImage=dogsData.getImageID();
//                    ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
//                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//                    viewHolder.dogImage.setImageBitmap(theImage);
                }else{
                    viewHolder.dogImage.setImageResource(R.drawable.pack_btn);
                }*/

                if(dogsData.isStatus()){
                    viewHolder.bluetooth.setImageResource(R.drawable.bluetooth);
                }else{
                    viewHolder.bluetooth.setImageResource(R.drawable.discnt);
                }

                return view;
            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.updateDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView dogName;
/*        TextView dogAge;
        TextView dogGoal;
        ImageView dogImage;*/
        ImageView bluetooth;
    }
}