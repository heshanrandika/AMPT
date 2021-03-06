package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.ampt.bluetooth.R;
import com.androidplot.LineRegion;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Iterator;



/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class MainActivity extends Activity
{

    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;

    private XYSeries series1;
    private XYSeries series2;
/*
    private enum SeriesSize {
        TEN,
        TWENTY,
        SIXTY
    }
*/

    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers10 = {2, null, 5, 2,    7, 4, 3, 7, 4, 5};
    Number[] series2Numbers10 = {4, 6,    3, null, 2, 0, 7, 4, 5, 4};
    Number[] series1Numbers = series1Numbers10;
    Number[] series2Numbers = series2Numbers10;

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        formatter1 = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        formatter2 = new MyBarFormatter(Color.argb(200, 100, 100, 150), Color.LTGRAY);
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new SizeMetrics(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(0, XLayoutStyle.RELATIVE_TO_CENTER,PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();


        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        plot.getGraphWidget().setGridPadding(30, 10, 30, 0);

        plot.setTicksPerDomainLabel(2);



        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });


     /*   ArrayAdapter<BarRenderer.BarRenderStyle> adapter = new ArrayAdapter <BarRenderer.BarRenderStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarRenderStyle.values() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter <BarRenderer.BarWidthStyle> adapter1 = new ArrayAdapter <BarRenderer.BarWidthStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarWidthStyle.values() );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter <SeriesSize> adapter11 = new ArrayAdapter <SeriesSize> (this, android.R.layout.simple_spinner_item, SeriesSize.values() );
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/


        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                int year = (int) (value + 0.5d) / 12;
                int month = (int) ((value + 0.5d) % 12);
                return new StringBuffer(DateFormatSymbols.getInstance().getShortMonths()[month] + " '0" + year);
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
        updatePlot();

    }

    private void updatePlot() {

        // Remove all current series from each plot
        Iterator<XYSeries> iterator1 = plot.getSeriesSet().iterator();
        while(iterator1.hasNext()) {
            XYSeries setElement = iterator1.next();
            plot.removeSeries(setElement);
        }

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Us");
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        // add a new series' to the xyplot:
        plot.addSeries(series1, formatter1);
        plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer)plot.getRenderer(MyBarRenderer.class));


        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.getGraphWidget().getGridRect().contains(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (XYSeries series : plot.getSeriesSet()) {
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if(selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }


    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection formatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if(selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}
