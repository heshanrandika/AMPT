package com.ampt.bluetooth;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Heshanr on 5/7/2015.
 */
public class AllChartView extends Activity implements AdapterView.OnItemSelectedListener {
    DatabaseHelper daf = new DatabaseHelper(this);
    private XYPlot plot;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<ActivityData> activityDatas = null;
        switch (position){
            case 0:
                activityDatas = daf.getAllActivityDateRange(0);
                break;

            case 1:
                activityDatas = daf.getAllActivityDateRange(7);
                break;

            case 2:
                activityDatas = daf.getAllActivityDateRange(30);
                break;

            case 3:
                activityDatas = daf.getAllActivityDateRange(90);
                break;

            default:
        }

        try {
            if(activityDatas.size()>0){
                processData(activityDatas,position);
                Toast.makeText(parent.getContext(), "Graph Processing...", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(parent.getContext(), "No Data", Toast.LENGTH_LONG).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.chart);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Today");
        categories.add("Last 7 Days");
        categories.add("Last Month");
        categories.add("Last 3 Month");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    public void processData( ArrayList<ActivityData> activityData, int pos) throws ParseException {

        HashMap<Integer, Double[]> map = new HashMap<Integer, Double[]>();
        int dogID = 0;


        drawPlot(new Number[]{}, new Number[]{}, new Number[]{}, new Number[]{}, new String[]{});
        for(ActivityData activityD  : activityData){
            Double [] tempNum = new Double[4];
            tempNum[0] = Double.valueOf(activityD.getPlay());
            tempNum[1] = Double.valueOf(activityD.getSwimming());
            tempNum[2] = Double.valueOf(activityD.getSleep());
            tempNum[3] = Double.valueOf(activityD.getWalk());
            dogID = activityD.getDogId();
            if(map.containsKey(dogID)){
                map.get(dogID)[0] += tempNum[0];
                map.get(dogID)[1] += tempNum[1];
                map.get(dogID)[2] += tempNum[2];
                map.get(dogID)[3] += tempNum[3];
            }else{
                map.put(dogID,tempNum);
            }
        }

        Number[] slpArray = new Number[map.size()+2];
        Number[] wlkArray = new Number[map.size()+2];
        Number[] swimArray = new Number[map.size()+2];
        Number[] plyArray = new Number[map.size()+2];
        String[] xLabels = new String[map.size()+2];
        slpArray[0] = wlkArray[0] = swimArray[0] = plyArray[0] = 0;
        slpArray[slpArray.length-1] = wlkArray[wlkArray.length-1] = swimArray[swimArray.length-1] = plyArray[plyArray.length-1] = 0;
        xLabels[0] = ".";
        xLabels[xLabels.length-1] = ".";
        int k = 1;
        for(int tmpTbl : map.keySet()){
            plyArray[k]  = Math.round((map.get(tmpTbl)[0]/60)  * 10.0 ) / 10.0;
            swimArray[k] = Math.round((map.get(tmpTbl)[1]/60)  * 10.0 ) / 10.0;
            slpArray[k]  = Math.round((map.get(tmpTbl)[2]/60)  * 10.0 ) / 10.0;
            wlkArray[k]  = Math.round((map.get(tmpTbl)[3]/60)  * 10.0 ) / 10.0;
            xLabels[k]   = (daf.getDogBasic(tmpTbl)).getName();
            k++;
        }
        plot.clear();
        drawPlot(slpArray, wlkArray, swimArray, plyArray, xLabels);
        plot.redraw();

    }


    public void drawPlot(Number[] slpArray, Number[] wlkArray, Number[] swimArray, Number[] plyArray, String[] xLabels){


        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat(xLabels));

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(slpArray), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,   "sleep");
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(wlkArray), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,   "walk");
        XYSeries series3 = new SimpleXYSeries(Arrays.asList(plyArray), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,   "play");
        XYSeries series4 = new SimpleXYSeries(Arrays.asList(swimArray), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,  "swim");
        //     XYSeries series3 = new SimpleXYSeries(Arrays.asList(series3Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series3");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);
        //  LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf2);
        //  LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        plot.addSeries(series2, series2Format);

        LineAndPointFormatter series3Format = new LineAndPointFormatter();
        series3Format.setPointLabelFormatter(new PointLabelFormatter());
        series3Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf3);
        //  LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        plot.addSeries(series3, series3Format);

        LineAndPointFormatter series4Format = new LineAndPointFormatter();
        series4Format.setPointLabelFormatter(new PointLabelFormatter());
        series4Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf4);
        //  LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        plot.addSeries(series4, series4Format);

        // reduce the number of range labels
        //  plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 125);
        plot.setDomainStep(XYStepMode.SUBDIVIDE, xLabels.length);
        plot.setTicksPerRangeLabel(1);
        //  plot.setTicksPerDomainLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-10);
        //  plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.setDomainLabel("Name");
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.WHITE);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.WHITE);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.WHITE);

    }

    class GraphXLabelFormat extends Format {
        String[] xLabels;

        GraphXLabelFormat(String[] xLabels) {
            this.xLabels = xLabels;
        }

        @Override
        public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
            // TODO Auto-generated method stub

            int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
            //  Log.d("test", parsedInt + " " + arg1 + " " + arg2);
            String labelString = xLabels[parsedInt];
            arg1.append(labelString);
            return arg1;
        }

        @Override
        public Object parseObject(String arg0, ParsePosition arg1) {
            // TODO Auto-generated method stub
            return java.util.Arrays.asList(xLabels).indexOf(arg0);
        }
    }
}
