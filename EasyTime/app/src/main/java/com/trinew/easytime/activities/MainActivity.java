package com.trinew.easytime.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.location.LocationHandler;
import com.trinew.easytime.modules.time.EasyTimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The Activity MainActivity is the Main screen of the app. It simply shows a
 * dummy image with some filter options. You need to write actual code image
 * processing and filtering.
 *
 */
public class MainActivity extends ActionBarActivity {

    private final static int CHECK_STATE_IN = 0;
    private final static int CHECK_STATE_OUT = 1;

    // views
    private TextView timerMinutesTextView;
    private TextView timerColonTextView;
    private TextView timerSecondsTextView;

    private TextView currDateTextView;

    private Button checkInButton;

    // modules
    private EasyTimer easyTimer;

    // state data
    private int checkState = CHECK_STATE_OUT;

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

        Date currDate = Calendar.getInstance().getTime();

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d");
        String currDateStr = outputDateFormat.format(currDate);

        currDateTextView = (TextView) findViewById(R.id.headerDateText);
        currDateTextView.setText(currDateStr);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

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
        });

        // init views
        timerMinutesTextView = (TextView) findViewById(R.id.timerMinutesTextView);
        timerColonTextView = (TextView) findViewById(R.id.timerColonTextView);
        timerSecondsTextView = (TextView) findViewById(R.id.timerSecondsTextView);

        checkInButton = (Button) findViewById(R.id.checkInButton);

        // fill views
        checkInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkState == CHECK_STATE_IN) {
                    checkOut();
                } else if(checkState == CHECK_STATE_OUT) {
                    checkIn();
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // interaction

    private void checkOut() {
        checkState = CHECK_STATE_OUT;
        checkInButton.setEnabled(false);

        if (easyTimer.isRunning()) {
            easyTimer.stopTimer();
        }

        // begin animations
        final Animation a = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(1000);
        checkInButton.startAnimation(a);

        timerColonTextView.clearAnimation();

        final ParseUser user = ParseUser.getCurrentUser();

        final ParseStamp stamp = new ParseStamp();
        stamp.setFlag(ParseStamp.FLAG_CHECK_OUT);
        stamp.setComment("Checked out!");

        ParseGeoPoint quickLocation = LocationHandler.getQuickLocation(getApplicationContext());
        if(quickLocation != null) {
            stamp.setLocation(quickLocation);

            List<ParseStamp> parseStamps = user.getList("stamps");
            if(parseStamps == null)
                parseStamps = new ArrayList<ParseStamp>();

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(MainActivity.this, "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(MainActivity.this, "Thanks for checking out!", Toast.LENGTH_LONG).show();

                    checkInButton.clearAnimation();
                    checkInButton.setBackgroundResource(R.drawable.check_in_button_selector);
                    checkInButton.setEnabled(true);
                }
            });
        } else {
            LocationHandler.requestLocation(getApplicationContext(), new LocationHandler.OnLocationReceivedListener() {
                @Override
                public void done(ParseGeoPoint geoPoint, Exception e) {
                    if (e == null && geoPoint != null) {
                        stamp.setLocation(geoPoint);

                        List<ParseStamp> parseStamps = user.getList("stamps");
                        if (parseStamps == null)
                            parseStamps = new ArrayList<ParseStamp>();

                        parseStamps.add(stamp);
                        user.put("stamps", parseStamps);

                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(MainActivity.this, "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Toast.makeText(MainActivity.this, "Thanks for checking out!", Toast.LENGTH_LONG).show();

                                checkInButton.clearAnimation();
                                checkInButton.setBackgroundResource(R.drawable.check_in_button_selector);
                                checkInButton.setEnabled(true);
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkIn() {
        checkInButton.setEnabled(false);

        final Animation a = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(1000);
        checkInButton.startAnimation(a);

        final ParseUser user = ParseUser.getCurrentUser();

        final ParseStamp stamp = new ParseStamp();
        stamp.setFlag(ParseStamp.FLAG_CHECK_IN);
        stamp.setComment("Checked in!");
        ParseGeoPoint quickLocation = LocationHandler.getQuickLocation(getApplicationContext());
        if(quickLocation != null) {
            stamp.setLocation(quickLocation);

            List<ParseStamp> parseStamps = user.getList("stamps");
            if(parseStamps == null)
                parseStamps = new ArrayList<ParseStamp>();

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(MainActivity.this, "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(MainActivity.this, "Nice! You checked in!", Toast.LENGTH_LONG).show();

                    checkState = CHECK_STATE_IN;

                    if (!easyTimer.isRunning()) {
                        easyTimer.startTimer();
                    }

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    timerColonTextView.startAnimation(anim);

                    checkInButton.clearAnimation();
                    checkInButton.setBackgroundResource(R.drawable.check_out_button_selector);
                    checkInButton.setEnabled(true);
                }
            });
        } else {
            LocationHandler.requestLocation(getApplicationContext(), new LocationHandler.OnLocationReceivedListener() {
                @Override
                public void done(ParseGeoPoint geoPoint, Exception e) {
                    if (e == null && geoPoint != null) {
                        stamp.setLocation(geoPoint);

                        List<ParseStamp> parseStamps = user.getList("stamps");
                        if (parseStamps == null)
                            parseStamps = new ArrayList<ParseStamp>();

                        parseStamps.add(stamp);
                        user.put("stamps", parseStamps);

                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(MainActivity.this, "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Toast.makeText(MainActivity.this, "Nice! You checked in!", Toast.LENGTH_LONG).show();

                                checkState = CHECK_STATE_IN;

                                if (!easyTimer.isRunning()) {
                                    easyTimer.startTimer();
                                }

                                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                                anim.setDuration(500); //You can manage the blinking time with this parameter
                                anim.setStartOffset(20);
                                anim.setRepeatMode(Animation.REVERSE);
                                anim.setRepeatCount(Animation.INFINITE);
                                timerColonTextView.startAnimation(anim);

                                checkInButton.clearAnimation();
                                checkInButton.setBackgroundResource(R.drawable.check_out_button_selector);
                                checkInButton.setEnabled(true);
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}