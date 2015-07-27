package com.ampt.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.ActivityData;
import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Heshanr on 5/6/2015.
 */
public class singleChartView extends Activity implements AdapterView.OnItemSelectedListener {
    DatabaseHelper daf = new DatabaseHelper(this);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat dateFormatDate = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
    SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    public static final String EXTRAS_DOG_ID = "DOG_ID";
    public static final String EXTRAS_ACTIVITY_ID = "ACTIVITY_ID";
    private XYPlot singlePlot;

    private  long dogId;
    private  int activityId;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.single_chart_view);
        final Intent intent = getIntent();
        dogId = Integer.parseInt(intent.getStringExtra(EXTRAS_DOG_ID));
        activityId = Integer.parseInt(intent.getStringExtra(EXTRAS_ACTIVITY_ID));
        //  dogId =0;



        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Today");
        categories.add("Last 7 Days");
        categories.add("Last 30 Days");
        categories.add("Monthly");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<ActivityData> activityDatas = null;
        switch (position){
            case 0:
                activityDatas = daf.getAllActivityDogDateRange(dogId,0);
                break;

            case 1:
                activityDatas = daf.getAllActivityDogDateRange(dogId,7);
                break;

            case 2:
                activityDatas = daf.getAllActivityDogDateRange(dogId,30);
                break;

            case 3:
                activityDatas = daf.getAllActivityDog(dogId);
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

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }



    public void processData( ArrayList<ActivityData> activityData, int pos) throws ParseException {

        HashMap<String, Double[]> map = new HashMap<String, Double[]>();
        Date date;
        String dateString = "";

        if(pos == 0){
            drawPlot(new Number[]{}, new String[]{},0);
            for(ActivityData activityD  : activityData){
                Double [] tempNum = new Double[4];
                tempNum[0] = Double.valueOf(activityD.getPlay());
                tempNum[1] = Double.valueOf(activityD.getSwimming());
                tempNum[2] = Double.valueOf(activityD.getSleep());
                tempNum[3] = Double.valueOf(activityD.getWalk());
                date = dateFormat.parse(activityD.getCreatedAt());
                dateString = dateFormatTime.format(date);
                if(map.containsKey(dateString)){
                    map.get(dateString)[0] += tempNum[0];
                    map.get(dateString)[1] += tempNum[1];
                    map.get(dateString)[2] += tempNum[2];
                    map.get(dateString)[3] += tempNum[3];
                }else{
                    map.put(dateString,tempNum);
                }
            }


        }else if(pos == 1 || pos == 2){
            drawPlot(new Number[]{}, new String[]{},0);
            for(ActivityData activityD  : activityData){
                Double [] tempNum = new Double[4];
                tempNum[0] = Double.valueOf(activityD.getPlay());
                tempNum[1] = Double.valueOf(activityD.getSwimming());
                tempNum[2] = Double.valueOf(activityD.getSleep());
                tempNum[3] = Double.valueOf(activityD.getWalk());
                date = dateFormat.parse(activityD.getCreatedAt());
                dateString = dateFormatDate.format(date);
                if(map.containsKey(dateString)){
                    map.get(dateString)[0] += tempNum[0];
                    map.get(dateString)[1] += tempNum[1];
                    map.get(dateString)[2] += tempNum[2];
                    map.get(dateString)[3] += tempNum[3];
                }else{
                    map.put(dateString,tempNum);
                }
            }

        }else if(pos == 3){
            drawPlot(new Number[]{}, new String[]{},0);
            for(ActivityData activityD  : activityData){
                Double [] tempNum = new Double[4];
                tempNum[0] = Double.valueOf(activityD.getPlay());
                tempNum[1] = Double.valueOf(activityD.getSwimming());
                tempNum[2] = Double.valueOf(activityD.getSleep());
                tempNum[3] = Double.valueOf(activityD.getWalk());
                date = dateFormat.parse(activityD.getCreatedAt());
                dateString = dateFormatMonth.format(date);
                if(map.containsKey(dateString)){
                    map.get(dateString)[0] += tempNum[0];
                    map.get(dateString)[1] += tempNum[1];
                    map.get(dateString)[2] += tempNum[2];
                    map.get(dateString)[3] += tempNum[3];
                }else{
                    map.put(dateString,tempNum);
                }
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
        for(String tmpTbl : map.keySet()){
            plyArray[k]  = Math.round((map.get(tmpTbl)[0]/60)  * 10.0 ) / 10.0;
            swimArray[k] = Math.round((map.get(tmpTbl)[1]/60)  * 10.0 ) / 10.0;
            slpArray[k]  = Math.round((map.get(tmpTbl)[2]/60)  * 10.0 ) / 10.0;
            wlkArray[k]  = Math.round((map.get(tmpTbl)[3]/60)  * 10.0 ) / 10.0;
            xLabels[k] = tmpTbl;
            k++;
        }
        singlePlot.clear();
        if(activityId == 0)
            drawPlot(slpArray, xLabels, activityId);
        if(activityId == 1)
            drawPlot(wlkArray, xLabels, activityId);
        if(activityId == 2)
            drawPlot(plyArray, xLabels, activityId);
        if(activityId == 3)
            drawPlot(swimArray, xLabels, activityId);
        singlePlot.redraw();

    }


    public void drawPlot(Number[] dataArray, String[] xLabels, int color){


        // initialize our XYPlot reference:


        // Create a couple arrays of y-values to plot:

        // initialize our XYPlot reference:
        singlePlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        singlePlot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat(xLabels));

        singlePlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        singlePlot.setPlotMargins(0, 0, 0, 0);
        singlePlot.setPlotPadding(0, 0, 0, 0);
        singlePlot.setGridPadding(0, 10, 5, 0);

        singlePlot.setBackgroundColor(Color.WHITE);

      /*  singlePlot.position(
                singlePlot.getGraphWidget(),
                0,
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                0,
                YLayoutStyle.RELATIVE_TO_CENTER,
                AnchorPosition.LEFT_MIDDLE);*/

        singlePlot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        singlePlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        singlePlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        singlePlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        singlePlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        singlePlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        singlePlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        singlePlot.setDomainStep(XYStepMode.SUBDIVIDE, xLabels.length);
        singlePlot.setTicksPerRangeLabel(1);
        //  plot.setTicksPerDomainLabel(3);
        singlePlot.getGraphWidget().setDomainLabelOrientation(-10);

        // Domain
/*        singlePlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, xLabels.length);
        singlePlot.setDomainValueFormat(new DecimalFormat("0"));
        singlePlot.setDomainStepValue(1);

        //Range
       // singlePlot.setRangeBoundaries(0, 4500, BoundaryMode.FIXED);
        singlePlot.setRangeStepValue(1);
        singlePlot.setRangeStep(XYStepMode.SUBDIVIDE, dataArray.length);
        singlePlot.setRangeValueFormat(new DecimalFormat("0"));*/

        //Remove legend
        singlePlot.getLayoutManager().remove(singlePlot.getLegendWidget());
        singlePlot.getLayoutManager().remove(singlePlot.getDomainLabelWidget());
        singlePlot.getLayoutManager().remove(singlePlot.getRangeLabelWidget());
        singlePlot.getLayoutManager().remove(singlePlot.getTitleWidget());

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(dataArray), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,   "");

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.CYAN,null);                            // fill color
        // LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        if(color == 0)
            lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
        if(color == 1)
            lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));
        if(color == 2)
            lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.YELLOW, Shader.TileMode.MIRROR));
        if(color == 3)
            lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GRAY, Shader.TileMode.MIRROR));

        series1Format.setFillPaint(lineFill);

        // add a new series' to the xyplot:
        singlePlot.addSeries(series1, series1Format);

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
