package com.ampt.bluetooth;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;


/**
 * Created by Heshanr on 4/13/2015.
 */
public class DogProfile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dog_profile);

    }
}
