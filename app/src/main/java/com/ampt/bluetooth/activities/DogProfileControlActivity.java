package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.SampleGattAttributes;
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
public class DogProfileControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
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

    private ImageView dogImage;
    //private ImageButton editProfile;
    private ImageButton onCollarClick;
    //private ImageButton archivedBtn;
    private ImageButton syncBtn;
    private ImageButton activityBtn;
    private ImageButton packBtn;
    private ImageButton slpBtn;
    private ImageButton wlkBtn;
    private ImageButton swimBtn;
    private ImageButton plyBtn;

    private String img;
    private long dog_id;
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
        setContentView(R.layout.dog_profile_new);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        dog_id = Long.parseLong(intent.getStringExtra(EXTRAS_DOG_ID));
        // Sets up UI references.

        dogName = (TextView) findViewById(R.id.name_txt);
       /* dogAge = (TextView) findViewById(R.id.age_txt);
        sleepTime = (TextView) findViewById(R.id.sleep_time);
        walkTime = (TextView) findViewById(R.id.walk_time);
        playTime = (TextView) findViewById(R.id.play_time);
        swimTime = (TextView) findViewById(R.id.swim_time);*/


        dogImage = (ImageView) findViewById(R.id.profile_image_btn);
       // editProfile = (ImageButton) findViewById(R.id.edit_profile_btn);
        onCollarClick = (ImageButton) findViewById(R.id.on_coller_click);
       // archivedBtn = (ImageButton) findViewById(R.id.archived_btn);
        syncBtn = (ImageButton) findViewById(R.id.sync_btn);
        activityBtn = (ImageButton) findViewById(R.id.activity_btn);
        packBtn = (ImageButton) findViewById(R.id.pack_btn);
        wlkBtn = (ImageButton) findViewById(R.id.wlk_btn);
        slpBtn = (ImageButton) findViewById(R.id.slp_btn);
        plyBtn = (ImageButton) findViewById(R.id.ply_btn);
        swimBtn = (ImageButton) findViewById(R.id.swim_btn);


        setUpUI();
        getActionBar().setTitle(R.string.profile_title);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        dogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent intent = new Intent(DogProfileControlActivity.this, AddOrEditDogProfile.class);
//                intent.putExtra(AddOrEditDogProfile.EXTRAS_DEVICE_NAME, mDeviceName);
//                intent.putExtra(AddOrEditDogProfile.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
//                intent.putExtra(AddOrEditDogProfile.EXTRAS_EDIT_OR_SAVE, AddOrEditDogProfile.EXTRAS_EDIT);
//
//                startActivity(intent);
            }
        });
        onCollarClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChange(COLLAR_CLICK);
            }
        });

        slpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, singleChartView.class);
                intent.putExtra(singleChartView.EXTRAS_DOG_ID, dog_id+"");
                intent.putExtra(singleChartView.EXTRAS_ACTIVITY_ID, "0");
                startActivity(intent);
            }
        });
        wlkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, singleChartView.class);
                intent.putExtra(singleChartView.EXTRAS_DOG_ID, dog_id+"");
                intent.putExtra(singleChartView.EXTRAS_ACTIVITY_ID, "1");
                startActivity(intent);
            }
        });
        plyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, singleChartView.class);
                intent.putExtra(singleChartView.EXTRAS_DOG_ID, dog_id+"");
                intent.putExtra(singleChartView.EXTRAS_ACTIVITY_ID, "2");
                startActivity(intent);
            }
        });
        swimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, singleChartView.class);
                intent.putExtra(singleChartView.EXTRAS_DOG_ID, dog_id+"");
                intent.putExtra(singleChartView.EXTRAS_ACTIVITY_ID, "3");
                startActivity(intent);
            }
        });


       /* archivedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, ArchivedActivity.class);
                intent.putExtra(ArchivedActivity.EXTRAS_DOG_ID, dog_id+"");
                intent.putExtra(ArchivedActivity.EXTRAS_DOG_NAME, dogName.getText().toString());

                startActivity(intent);
            }
        });*/
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChange(SYNC_PRESS);
            }
        });
        activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DogProfileControlActivity.this, chartView.class);
                intent.putExtra(chartView.EXTRAS_DOG_ID, dog_id+"");
                startActivity(intent);
            }
        });
        packBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setUpUI() {
        DogsData dogProfileData = daf.getDogProfile(dog_id);
        // Sets up UI
        if(dogProfileData != null){
            dogName.setText(dogProfileData.getName());
           // dogAge.setText(dogProfileData.getAge() + "");
            img = dogProfileData.getImageID();
            if(img != null){
//                ByteArrayInputStream imageStream = new ByteArrayInputStream(img);
//                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//                dogImage.setImageBitmap(theImage);

            }
            setActivityData();
        }else{
            DogProfileControlActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpUI();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

       /* sleepTime.setText(slpH+"h "+slpM+"min");
        walkTime.setText(wlkH+"h "+wlkM+"min");
        playTime.setText(plyH+"h "+plyM+"min");
        swimTime.setText(swmH+"h "+swmM+"min");*/
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
