package com.trinew.easytime.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.ProfileBuilder;
import com.trinew.easytime.modules.stamps.StampCollection;
import com.trinew.easytime.modules.stamps.StampCollectionBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * The Activity MainActivity is the Main screen of the app. It simply shows a
 * dummy image with some filter options. You need to write actual code image
 * processing and filtering.
 *
 */
public class GraphActivity extends ActionBarActivity implements OnChartValueSelectedListener {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    private RelativeLayout progressContainer;
    private RelativeLayout errorContainer;
    private TextView genericErrorText;
    private TextView noStampsErrorText;

    private LineChart mChart;

    /* (non-Javadoc)
     * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
     */
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

        mChart = (LineChart) findViewById(R.id.graphChart);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        // mChart.setStartAtZero(true);

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        // begin charting data
        chartUserData();
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

                int minHour = stampCollectionBox.getMinHour();
                int maxHour = stampCollectionBox.getMaxHour();
                int midHour = (minHour + maxHour) / 2;
                List<String> xVals = new ArrayList<>();
                for(int z = 0; z < maxHour - minHour; z++) {
                    xVals.add((minHour + z) + "");
                }

                int color = getResources().getColor(R.color.primary);

                // stamps are arranged in collections based on the day they were created
                // this is implemented using the StampCollectionBox model
                // the box contains a list of StampCollection,
                // each StampCollection contains a list of stamps and the day that the collection
                // is associated with
                List<StampCollection> stampCollections = stampCollectionBox.getStampCollections();
                List<LineDataSet> dataSets = new ArrayList<>();

                int j = 0;
                for (int i = 0; i < stampCollections.size(); i++) {
                    StampCollection stampCollection = stampCollections.get(i);
                    int stampDay = stampCollection.getCollectionDay();
                    List<ParseStamp> collectionStamps = stampCollection.getStamps();
                    List<Entry> valueEntries = new ArrayList<>();

                    for(j = 0; j < collectionStamps.size(); i++) {
                        Calendar stampCalendar = Calendar.getInstance();
                        stampCalendar.setTime(collectionStamps.get(j).getCreatedAt());
                        int stampHour = stampCalendar.get(Calendar.HOUR_OF_DAY);
                        int adjustedTimeIndex = (int) ((float) stampHour / 24f * (maxHour - minHour) + minHour);
                        valueEntries.add(new Entry((float) stampDay, adjustedTimeIndex));
                    }

                    LineDataSet d = new LineDataSet(valueEntries, "DataSet " + (i + 1));
                    d.setLineWidth(2.5f);
                    d.setCircleSize(4f);

                    d.setColor(color);
                    d.setCircleColor(color);
                    dataSets.add(d);
                }

                LineData data = new LineData(xVals, dataSets);

                mChart.setData(data);
                mChart.invalidate();
            }
        });
    }
}