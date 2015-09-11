package com.trinew.easytime.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class EasyChartFragment extends Fragment implements OnChartValueSelectedListener {

    private RelativeLayout progressContainer;
    private RelativeLayout errorContainer;
    private TextView genericErrorText;
    private TextView noStampsErrorText;

    private EasyChart mChart;

    // listener
    private OnGraphInteractionListener onGraphInteractionListener;

    public static EasyChartFragment newInstance() {
        EasyChartFragment fragment = new EasyChartFragment();
        return fragment;
    }

    public EasyChartFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_easy_chart, container, false);

        // init views
        progressContainer = (RelativeLayout) view.findViewById(R.id.progressContainer);
        errorContainer = (RelativeLayout) view.findViewById(R.id.errorContainer);
        genericErrorText = (TextView) view.findViewById(R.id.genericErrorText);
        noStampsErrorText = (TextView) view.findViewById(R.id.noStampsErrorText);

        // init charts
        mChart = (EasyChart) view.findViewById(R.id.graphChart);
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

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onGraphInteractionListener = (OnGraphInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGraphInteractionListener");
        }
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

                // prepare x values
                List<String> xVals = new ArrayList<>();
                int minHour = stampCollectionBox.getMinHour();
                int maxHour = stampCollectionBox.getMaxHour();
                int hoursDiff = maxHour - minHour;
                int intervalsSize = stampCollectionBox.getMaxSize();
                float intervalRatio = (float) hoursDiff / (float) intervalsSize;
                for (int i = 0; i < intervalsSize; i++) {
                    float timeValue = (float) minHour + intervalRatio * i;
                    xVals.add((int) timeValue + "");
                }

                xVals.add(maxHour + "");

                // stamps are arranged in collections based on the day they were created
                // this is implemented using the StampCollectionBox model
                // the box contains a list of StampCollection,
                // each StampCollection contains a list of stamps and the day that the collection
                // is associated with
                List<StampCollection> stampCollections = stampCollectionBox.getStampCollections();
                List<EasyDataSet> dataSets = new ArrayList<>();

                for (int i = 0; i < stampCollections.size(); i++) {
                    StampCollection stampCollection = stampCollections.get(i);
                    Date stampDate = stampCollection.getCollectionDate();

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d");
                    String collectionDateStr = outputDateFormat.format(stampDate);

                    // prepare the entries of the data set
                    List<ParseStamp> collectionStamps = stampCollection.getStamps();
                    List<EasyEntry> collectionEntries = new ArrayList<>();

                    // put each stamp value into an entry of the current data set
                    for (ParseStamp stamp : collectionStamps) {
                        xVals.add(collectionDateStr);
                        //collectionEntries.add(new EasyEntry(stamp.getTimeValue(), i, stamp.getFlag()));
                    }

                    EasyDataSet dataSet = new EasyDataSet(collectionEntries, collectionDateStr);
                    dataSets.add(dataSet);
                }

                //Log.i("GraphActivity", collectionEntries.size() + ":" + collectionEntries.get(0).getVals().length);

                EasyData data = new EasyData(xVals, dataSets);
                data.setValueTextSize(10f);
                //data.setValueTypeface(tf);

                mChart.setData(data);
                mChart.invalidate();
            }
        });
    }

    public interface OnGraphInteractionListener {
    }
}
