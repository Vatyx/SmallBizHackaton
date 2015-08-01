package com.trinew.easytime;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trinew.easytime.modules.EasyTimer;

/**
 * The Activity MainActivity is the Main screen of the app. It simply shows a
 * dummy image with some filter options. You need to write actual code image
 * processing and filtering.
 *
 */
public class MainActivity extends ActionBarActivity {

    // views
    private TextView timerMinutesTextView;
    private TextView timerSecondsTextView;

    // modules
    private EasyTimer easyTimer;

    /* (non-Javadoc)
     * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar_
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.mipmap.ic_drawer);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Easy Time");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setLogo(R.drawable.icon);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        timerMinutesTextView = (TextView) findViewById(R.id.timerMinutesTextView);
        timerSecondsTextView = (TextView) findViewById(R.id.timerSecondsTextView);

        // init timer
        easyTimer = new EasyTimer();
        easyTimer.setEasyTimerListener(new EasyTimer.EasyTimerListener() {
            @Override
            public void onUpdate(int remainingSeconds) {
                int minutes = remainingSeconds / 60;
                remainingSeconds = remainingSeconds % 60;

                timerMinutesTextView.setText(String.format("%02d", minutes));
                timerSecondsTextView.setText(String.format("%02d", remainingSeconds));
            }

            @Override
            public void onFinished() {
                Log.i("MainActivity", "Finished shower!");
            }
        });

        // fill views
        Button easyButton = (Button) findViewById(R.id.easyButton);
        easyButton.setText("Check In");
        easyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("Check Out")) {
                    if (easyTimer.isRunning()) {
                        easyTimer.stopTimer();
                    }

                    b.setText("Check In");
                } else {
                    if (!easyTimer.isRunning()) {
                        easyTimer.startTimer();
                    }

                    b.setText("Check Out");
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.i("MainActivity", easyTimer.isRunning() + " | " + easyTimer.isPaused());

        /*
        if(easyTimer.isRunning() && !easyTimer.isPaused())
            easyTimer.pauseTimer();
           */
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(easyTimer.isRunning() && easyTimer.isPaused())
            easyTimer.resumeTimer();
    }
}