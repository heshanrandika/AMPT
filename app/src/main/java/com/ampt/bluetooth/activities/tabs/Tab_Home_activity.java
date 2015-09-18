package com.ampt.bluetooth.activities.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.Util.ImageSetter;
import com.ampt.bluetooth.Util.SharedPref;
//import com.ampt.bluetooth.activities.AddDogActivity;
import com.ampt.bluetooth.activities.DeviceScanActivity;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;

import java.util.List;

/**
 * Created by malith on 7/31/15.
 */
public class Tab_Home_activity extends Activity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_home_layout);

        iv = (ImageView) findViewById(R.id.profile_image);
        checkAtLeastOneDogAvailable(this);


    }


    private void checkAtLeastOneDogAvailable(Context context) {
        DatabaseHelper dbh = new DatabaseHelper(context);
        List<DogsData> dogsDataList = dbh.getAllDogProfile();
        if (null != dogsDataList && dogsDataList.size() > 0) {
            long defaultId = SharedPref.getDefaultDogId(context);
            DogsData dogsData = dbh.getDogProfile(defaultId);
            System.out.println("ccccccccccccccccccc    "+Long.parseLong(dogsData.getImageID()));
            ImageSetter.setImage(this, iv, Long.parseLong(dogsData.getImageID()));
        } else {
          //  startActivity(new Intent(context, DeviceScanActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAtLeastOneDogAvailable(this);
    }
}