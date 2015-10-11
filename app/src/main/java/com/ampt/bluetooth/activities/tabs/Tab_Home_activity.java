package com.ampt.bluetooth.activities.tabs;



import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.SampleGattAttributes;
import com.ampt.bluetooth.Util.ImageSetter;
import com.ampt.bluetooth.Util.SharedPref;
import com.ampt.bluetooth.activities.BluetoothLeService;
import com.ampt.bluetooth.activities.DeviceScanActivity;
import com.ampt.bluetooth.activities.dialogs.EditDogDialog;
import com.ampt.bluetooth.activities.dialogs.ViewDogDialog;
import com.ampt.bluetooth.chartView;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;
import com.ampt.bluetooth.database.model.DogsData;
import com.ampt.bluetooth.singleChartView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Heshanr on 4/18/2015.
 */
public class Tab_Home_activity extends Activity {
    private final static String TAG = Tab_Home_activity.class.getSimpleName();
    DatabaseHelper daf = new DatabaseHelper(this);
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DOG_ID = "DOG_ID";
    public static final String COLLAR_CLICK = "5";
    public static final String SYNC_PRESS = "2";

    private TextView dogName;
  /*  private TextView dogAge;
    private TextView sleepTime;
    private TextView walkTime;
    private TextView playTime;
    private TextView swimTime;*/

    private ImageView iv;


    private String img;
    private long dog_id;
    private DogsData dogsData;
    private TextView engageTxt;
    private ImageButton infoBtn;
    private TextView dogNameTxt;

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
    private Intent gattServiceIntent;

    // Code to manage Service lifecycle.
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
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                saveData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void clearUI() {
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_home_layout);
        final Context context = this;

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        iv = (ImageView) findViewById(R.id.profile_image);
        engageTxt = (TextView) findViewById(R.id.engage_txt);
        infoBtn = (ImageButton) findViewById(R.id.tab_home_info);
        dogNameTxt = (TextView) findViewById(R.id.tab_home_dog_name);



        checkAtLeastOneDogAvailable(this);
        //  getActionBar().setDisplayHomeAsUpEnabled(true);
        gattServiceIntent = new Intent(this, BluetoothLeService.class);
        getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        engageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChange(COLLAR_CLICK);
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dogsData != null){
                    ViewDogDialog dialog = new ViewDogDialog(context, dogsData);

                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                }else{
                    Toast.makeText(Tab_Home_activity.this, "Data not available!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void checkAtLeastOneDogAvailable(Context context) {
        dog_id = SharedPref.getCurrentDogId(context);
        if(dog_id != 0){
            dogsData = daf.getDogProfile(dog_id);
            System.out.println("ccccccccccccccccccc    "+Long.parseLong(dogsData.getImageID()));
            mDeviceAddress = dogsData.getDeviceAddress();
            mDeviceName = dogsData.getDeviceName();
            dogNameTxt.setText(dogsData.getName());
            ImageSetter.setImage(this, iv, Long.parseLong(dogsData.getImageID()));
        }else{
            List<DogsData> dogsDataList = daf.getAllDogProfile();
            if (null != dogsDataList && dogsDataList.size() > 0) {
                long defaultId = SharedPref.getDefaultDogId(context);
                dogsData = daf.getDogProfile(defaultId);
                System.out.println("ccccccccccccccccccc    "+Long.parseLong(dogsData.getImageID()));
                mDeviceAddress = dogsData.getDeviceAddress();
                mDeviceName = dogsData.getDeviceName();
                dog_id = dogsData.getId();
                dogNameTxt.setText(dogsData.getName());
                ImageSetter.setImage(this, iv, Long.parseLong(dogsData.getImageID()));
            } else {
                startActivity(new Intent(context, DeviceScanActivity.class));
            }
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (mBluetoothLeService != null) {

            //  final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //  Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mGattUpdateReceiver);
        getApplicationContext().unbindService(mServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService = null;
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
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


    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
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

}
