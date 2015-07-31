package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Heshanr on 4/19/2015.
 */
public class ArchivedActivity extends Activity {
    public static final String EXTRAS_DOG_ID = "DOG_ID";
    public static final String EXTRAS_DOG_NAME = "DOG_NAME";
    DatabaseHelper daf =  new DatabaseHelper(this);
    private long dog_id;
    private String dogName;
    private TableLayout stk;
    private ArrayList<ActivityData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.archived_activity);
        final Intent intent = getIntent();
        dog_id = Long.parseLong(intent.getStringExtra(EXTRAS_DOG_ID));
        getValue();
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    private void init() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
        stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" Date    ");
        tv0.setTextColor(Color.parseColor("#ff5176a0"));
        tv0.setGravity(Gravity.CENTER);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Sleep    ");
        tv1.setTextColor(Color.parseColor("#ff5176a0"));
        tv1.setGravity(Gravity.CENTER);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Walk     ");
        tv2.setTextColor(Color.parseColor("#ff5176a0"));
        tv2.setGravity(Gravity.CENTER);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Play     ");
        tv3.setTextColor(Color.parseColor("#ff5176a0"));
        tv3.setGravity(Gravity.CENTER);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Swim ");
        tv4.setTextColor(Color.parseColor("#ff5176a0"));
        tv4.setGravity(Gravity.CENTER);
        tbrow0.addView(tv4);
        stk.addView(tbrow0);
        if(dataList != null && dataList.size()>0){
            for (ActivityData actData : dataList) {
                Date date = dateFormat.parse(actData.getCreatedAt());
                TableRow tbrow = new TableRow(this);
                TextView t1v = new TextView(this);
                t1v.setText(" "+dateFormat.format(date));
                t1v.setTextColor(Color.BLACK);
                t1v.setBackgroundColor(Color.parseColor("#FFEBEBEB"));
                t1v.setGravity(Gravity.LEFT);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText("  "+(actData.getSleep()/60)+"h "+(actData.getSleep()%60)+"m");
                t2v.setTextColor(Color.parseColor("#fffb8721"));
                t2v.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                t2v.setGravity(Gravity.LEFT);
                tbrow.addView(t2v);
                TextView t3v = new TextView(this);
                t3v.setText("  "+(actData.getWalk()/60)+"h "+(actData.getWalk()%60)+"m");
                t3v.setTextColor(Color.parseColor("#fffb8721"));
                t3v.setBackgroundColor(Color.parseColor("#FFEBEBEB"));
                t3v.setGravity(Gravity.LEFT);
                tbrow.addView(t3v);
                TextView t4v = new TextView(this);
                t4v.setText("  "+(actData.getPlay()/60)+"h "+(actData.getPlay()%60)+"m");
                t4v.setTextColor(Color.parseColor("#fffb8721"));
                t4v.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                t4v.setGravity(Gravity.LEFT);
                tbrow.addView(t4v);
                TextView t5v = new TextView(this);
                t5v.setText("  "+(actData.getSwimming()/60)+"h "+(actData.getSwimming()%60)+"m");
                t5v.setTextColor(Color.parseColor("#fffb8721"));
                t5v.setBackgroundColor(Color.parseColor("#FFEBEBEB"));
                t5v.setGravity(Gravity.LEFT);
                tbrow.addView(t5v);

                stk.addView(tbrow);
            }

        }


    }

    private void getValue(){
        dataList = daf.getAllActivityDog(dog_id);
    }
}
