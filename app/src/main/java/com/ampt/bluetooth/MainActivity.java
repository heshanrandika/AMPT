package com.ampt.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton packBtn= (ImageButton)findViewById(R.id.pack_btn);
        ImageButton activityBtn= (ImageButton)findViewById(R.id.activity_btn);
        packBtn.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, DogScanActivity.class);
                startActivity(intent);
            }
        });

        activityBtn.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AllChartView.class);
                startActivity(intent);
            }
        });
    }

}
