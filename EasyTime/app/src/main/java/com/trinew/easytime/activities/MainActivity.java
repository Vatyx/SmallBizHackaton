package com.trinew.easytime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trinew.easytime.R;
import com.trinew.easytime.fragments.GameFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The Activity MainActivity is the Main screen of the app. It simply shows a
 * dummy image with some filter options. You need to write actual code image
 * processing and filtering.
 *
 */
public class MainActivity extends ActionBarActivity implements GameFragment.OnGameInteractionListener {

    private TextView currDateTextView;

    /* (non-Javadoc)
     * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // init tool bar
        ImageView graphButton = (ImageView) findViewById(R.id.btn_graph);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            }
        });

        Date currDate = Calendar.getInstance().getTime();

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d");
        String currDateStr = outputDateFormat.format(currDate);

        currDateTextView = (TextView) findViewById(R.id.headerDateText);
        currDateTextView.setText(currDateStr);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}