package com.trinew.easytime.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.ProfileBuilder;
import com.trinew.easytime.modules.data.EasyData;
import com.trinew.easytime.modules.data.EasyDataSet;
import com.trinew.easytime.modules.data.EasyEntry;
import com.trinew.easytime.modules.stamps.StampCollection;
import com.trinew.easytime.modules.stamps.StampCollectionBox;
import com.trinew.easytime.views.charts.EasyChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The Activity MainActivity is the Main screen of the app. It simply shows a
 * dummy image with some filter options. You need to write actual code image
 * processing and filtering.
 *
 */
public class GraphActivity extends ActionBarActivity implements OnChartValueSelectedListener {

    private RelativeLayout progressContainer;
    private RelativeLayout errorContainer;
    private TextView genericErrorText;
    private TextView noStampsErrorText;

    private EasyChart mChart;

    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Set up toolbar_
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_launcher));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // init views
        progressContainer = (RelativeLayout) findViewById(R.id.progressContainer);
        errorContainer = (RelativeLayout) findViewById(R.id.errorContainer);
        genericErrorText = (TextView) findViewById(R.id.genericErrorText);
        noStampsErrorText = (TextView) findViewById(R.id.noStampsErrorText);

        // init charts
        mChart = (EasyChart) findViewById(R.id.graphChart);
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setHighlightEnabled(false);

        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(366);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);

        // mChart.setDrawYLabels(false);

        //tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        //yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        //yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        chartUserData();
        mChart.animateY(2500);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(0f);
        l.setXEntrySpace(4f);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    private void chartUserData() {
        mChart.resetTracking();

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseStamp> stamps = currentUser.getList(ProfileBuilder.PROFILE_KEY_STAMPS);

        // check for valid data
        if(stamps == null) {
            errorContainer.setVisibility(View.VISIBLE);
            genericErrorText.setVisibility(View.VISIBLE);
            return;
        }

        // check if there are any stamps
        if(stamps.size() == 0) {
            errorContainer.setVisibility(View.VISIBLE);
            noStampsErrorText.setVisibility(View.VISIBLE);

            return;
        }

        progressContainer.setVisibility(View.VISIBLE);

        // fetch the stamps and fill our charts
        ParseStamp.fetchAllIfNeededInBackground(stamps, new FindCallback<ParseStamp>() {
            @Override
            public void done(List<ParseStamp> stampList, ParseException e) {
                progressContainer.setVisibility(View.GONE);

                if (e != null || stampList == null) {
                    errorContainer.setVisibility(View.VISIBLE);
                    genericErrorText.setVisibility(View.VISIBLE);

                    return;
                }

                StampCollectionBox stampCollectionBox = new StampCollectionBox(stampList);

                List<String> xVals = new ArrayList<>();

                // stamps are arranged in collections based on the day they were created
                // this is implemented using the StampCollectionBox model
                // the box contains a list of StampCollection,
                // each StampCollection contains a list of stamps and the day that the collection
                // is associated with
                List<StampCollection> stampCollections = stampCollectionBox.getStampCollections();
                List<EasyDataSet> dataSets = new ArrayList<>();
                List<EasyEntry> collectionEntries = new ArrayList<>();

                int j = 0;
                for (int i = 0; i < stampCollections.size(); i++) {
                    StampCollection stampCollection = stampCollections.get(i);
                    Date stampDate = stampCollection.getCollectionDate();

                    // prepare the xVal
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d");
                    String collectionDateStr = outputDateFormat.format(stampDate);
                    xVals.add(collectionDateStr);

                    // prepare the yVal array
                    List<ParseStamp> collectionStamps = stampCollection.getStamps();
                    collectionEntries.add(new EasyEntry(collectionStamps, i));
                }

                //Log.i("GraphActivity", collectionEntries.size() + ":" + collectionEntries.get(0).getVals().length);

                EasyDataSet dataSet = new EasyDataSet(collectionEntries, "DataSet");
                dataSets.add(dataSet);

                EasyData data = new EasyData(xVals, dataSets);
                data.setValueTextSize(10f);
                //data.setValueTypeface(tf);

                mChart.setData(data);
                mChart.invalidate();
            }
        });
    }
}