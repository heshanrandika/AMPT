package com.ampt.bluetooth.activities.tabs;

import android.app.Activity;
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
import android.widget.ImageView;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.SampleGattAttributes;
import com.ampt.bluetooth.Util.ImageSetter;
import com.ampt.bluetooth.Util.SharedPref;
//import com.ampt.bluetooth.activities.AddDogActivity;
import com.ampt.bluetooth.activities.BluetoothLeService;
import com.ampt.bluetooth.activities.DeviceControlActivity;
import com.ampt.bluetooth.activities.DeviceScanActivity;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;
import com.ampt.bluetooth.database.model.DogsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by malith on 7/31/15.
 */
public class Tab_Home_activity extends Activity {
    public static final String COLLAR_CLICK = "5";
    DatabaseHelper daf = new DatabaseHelper(this);
    private long dog_id = SharedPref.getCurrentDogId(this);
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    private String mDeviceName;
    private String mDeviceAddress;
    //  private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;


    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_home_layout);

        iv = (ImageView) findViewById(R.id.profile_image);
        checkAtLeastOneDogAvailable(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void checkAtLeastOneDogAvailable(Context context) {
        if(dog_id != 0){
            DogsData dogsData = daf.getDogProfile(dog_id);
            System.out.println("ccccccccccccccccccc    "+Long.parseLong(dogsData.getImageID()));
            mDeviceAddress = dogsData.getDeviceAddress();
            ImageSetter.setImage(this, iv, Long.parseLong(dogsData.getImageID()));
        }else{
            List<DogsData> dogsDataList = daf.getAllDogProfile();
            if (null != dogsDataList && dogsDataList.size() > 0) {
                long defaultId = SharedPref.getDefaultDogId(context);
                DogsData dogsData = daf.getDogProfile(defaultId);
                System.out.println("ccccccccccccccccccc    "+Long.parseLong(dogsData.getImageID()));
                mDeviceAddress = dogsData.getDeviceAddress();
                ImageSetter.setImage(this, iv, Long.parseLong(dogsData.getImageID()));
            } else {
                startActivity(new Intent(context, DeviceScanActivity.class));
            }
        }

    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
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

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //saveData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {}
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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

       /* sleepTime.setText(slpH+"h "+slpM+"min");
        walkTime.setText(wlkH+"h "+wlkM+"min");
        playTime.setText(plyH+"h "+plyM+"min");
        swimTime.setText(swmH+"h "+swmM+"min");*/
    }

    private void makeChange(String value) {
        String str = value+"";
        Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAtLeastOneDogAvailable(this);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}