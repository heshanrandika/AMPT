package com.ampt.bluetooth.activities.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ampt.bluetooth.AsyncTask.LoadImage;
import com.ampt.bluetooth.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by malith on 7/31/15.
 */
public class Tab_Home_activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_home_layout);

        ImageView iv = (ImageView) findViewById(R.id.profile_image);

        LoadImage li = new LoadImage(this);
        try {
            iv.setImageBitmap(li.execute(1).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}