package com.trinew.easytime.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.ProfileBuilder;
import com.trinew.easytime.modules.location.LocationHandler;
import com.trinew.easytime.modules.time.EasyTimer;
import com.trinew.easytime.utils.PrettyTime;
import com.trinew.easytime.utils.StampHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GameFragment extends Fragment {

    // listener
    private OnGameInteractionListener onGameInteractionListener;

    // views
    private TextView dateText;

    private TextView timerHoursText;
    private TextView timerColonText;
    private TextView timerMinutesText;

    private ViewAnimator feedAnimator;
    private TextView feedText1;
    private TextView feedText2;

    private RelativeLayout errorContainer;
    private RelativeLayout progressContainer;

    private TextView genericErrorText;

    private Button checkInButton;
    private Button checkOutButton;

    // time
    private EasyTimer easyTimer;
    private long currDuration;

    // state
    private int checkState = -1;
    private int checkIndex = 0;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    public GameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // init timer
        easyTimer = new EasyTimer();
        easyTimer.setEasyTimerListener(new EasyTimer.EasyTimerListener() {
            @Override
            public void onUpdate(long elapsedTime) {
                syncDurationDisplay(elapsedTime + currDuration);
            }
        });

        // init views
        dateText = (TextView) view.findViewById(R.id.dateText);

        timerHoursText = (TextView) view.findViewById(R.id.timerHoursText);
        timerColonText = (TextView) view.findViewById(R.id.timerColonText);
        timerMinutesText = (TextView) view.findViewById(R.id.timerMinutesText);

        feedAnimator = (ViewAnimator) view.findViewById(R.id.feedAnimator);
        feedAnimator.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_in_top));
        feedAnimator.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_out_top));

        feedText1 = (TextView) view.findViewById(R.id.feedText1);
        feedText2 = (TextView) view.findViewById(R.id.feedText2);

        errorContainer = (RelativeLayout) view.findViewById(R.id.errorContainer);
        progressContainer = (RelativeLayout) view.findViewById(R.id.progressContainer);

        genericErrorText = (TextView) view.findViewById(R.id.genericErrorText);

        checkInButton = (Button) view.findViewById(R.id.checkInButton);
        checkOutButton = (Button) view.findViewById(R.id.checkOutButton);

        checkInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkIn();
            }
        });

        checkOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkOut();
            }
        });

        // fill views
        Date currDate = Calendar.getInstance().getTime();

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d");
        String currDateStr = outputDateFormat.format(currDate);

        dateText.setText(currDateStr);

        // init from cloud
        progressContainer.setVisibility(View.VISIBLE);

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseStamp> stamps = currentUser.getList(ProfileBuilder.PROFILE_KEY_STAMPS);
        ParseStamp.fetchAllIfNeededInBackground(stamps, new FindCallback<ParseStamp>() {
            @Override
            public void done(List<ParseStamp> list, ParseException e) {
                progressContainer.setVisibility(View.GONE);

                if (e != null) {
                    errorContainer.setVisibility(View.VISIBLE);
                    genericErrorText.setVisibility(View.VISIBLE);
                    Log.e("GameFragment", "Problem fetching stamps: " + e.toString());

                    return;
                }

                List<ParseStamp> todayStamps = StampHelper.getTodayStamps(list);
                if(todayStamps.size() == 0)
                    return;

                currDuration = StampHelper.getStampListDuration(todayStamps);

                // we need to get the current time then get the stamp right before that to see what the appropriate
                ParseStamp lastStamp = todayStamps.get(todayStamps.size() - 1);
/// TODO: Reset on next day
                switch (lastStamp.getFlag()) {
                    case ParseStamp.FLAG_CHECK_IN:
                        changeState(ParseStamp.FLAG_CHECK_OUT);

                        // sync timer to elapsed time
                        long elapsedTime = Calendar.getInstance().getTime().getTime() - lastStamp.getLogDate().getTime();
                        currDuration += elapsedTime;

                        // automatically restart the timer since were synced
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(500); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        timerColonText.startAnimation(anim);

                        feedText1.setText("Last checked in at " + PrettyTime.getPrettyTime(lastStamp.getLogDate()));

                        if (!easyTimer.isRunning()) {
                            easyTimer.startTimer();
                        }
                        break;
                    case ParseStamp.FLAG_CHECK_OUT:
                        feedText1.setText("Last checked out at " + PrettyTime.getPrettyTime(lastStamp.getLogDate()));
                        changeState(ParseStamp.FLAG_CHECK_IN);
                        break;
                }

                syncDurationDisplay(currDuration);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onGameInteractionListener = (OnGameInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameInteractionListener");
        }
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

        //if(easyTimer.isRunning() && easyTimer.isPaused())
          //  easyTimer.resumeTimer();
    }

    // pushes a new feed entry onto the check view
    private void pushFeedEntry(ParseStamp stamp) {
        checkIndex = (checkIndex + 1) % 2;

        Date stampDate = stamp.getLogDate();
        int stampFlag = stamp.getFlag();

        String flagStr = "";
        switch(stampFlag) {
            case ParseStamp.FLAG_CHECK_IN:
                flagStr = "in";
                break;
            case ParseStamp.FLAG_CHECK_OUT:
                flagStr = "out";
                break;
        }

        String stampStr = "Last checked " + flagStr + " at " + PrettyTime.getPrettyTime(stampDate);

        switch(checkIndex) {
            case 0:
                feedText1.setText(stampStr);
                break;
            case 1:
                feedText2.setText(stampStr);
                break;
        }

        feedAnimator.showNext();
    }

    // updates the duration textviews with the given time duration
    private void syncDurationDisplay(long totalTime) {
        long hours = TimeUnit.MILLISECONDS.toHours(totalTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime) - TimeUnit.HOURS.toMinutes(hours);
        NumberFormat f = new DecimalFormat("00");
        String hoursStr = f.format(hours);
        String minutesStr = f.format(minutes);
        timerHoursText.setText(hoursStr);
        timerMinutesText.setText(minutesStr);
    }

    // changes the state of the page
    private void changeState(int newState) {
        checkState = newState;

        switch (newState) {
            case ParseStamp.FLAG_CHECK_IN:
                checkInButton.setVisibility(View.VISIBLE);
                checkOutButton.setVisibility(View.GONE);
                break;
            case ParseStamp.FLAG_CHECK_OUT:
                checkInButton.setVisibility(View.GONE);
                checkOutButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    // interaction
    private void checkOut() {
        checkOutButton.setEnabled(false);

        if (easyTimer.isRunning()) {
            easyTimer.pauseTimer();
        }

        timerColonText.clearAnimation();

        final ParseUser user = ParseUser.getCurrentUser();

        // clone the list of stamps from the user
        final List<ParseStamp> parseStamps = user.getList("stamps");

        // init the stamp
        final ParseStamp stamp = new ParseStamp();
        stamp.setLogDate(new Date());
        stamp.setFlag(ParseStamp.FLAG_CHECK_OUT);
        stamp.setComment("Checked out!");

        // now we need a location for the stamp
        // first try to get a quick fix location, if that doesn't work
        // then request a location
        ParseGeoPoint quickLocation = LocationHandler.getQuickLocation(getActivity().getApplicationContext());
        if(quickLocation != null) {
            stamp.setLocation(quickLocation);

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    checkState = ParseStamp.FLAG_CHECK_OUT;
                }
            });
        } else {

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    checkState = ParseStamp.FLAG_CHECK_OUT;
                }
            });
            /*
            LocationHandler.requestLocation(getActivity(), new LocationHandler.OnLocationReceivedListener() {
                @Override
                public void done(ParseGeoPoint geoPoint, Exception e) {
                    if (e != null || geoPoint == null) {
                        Toast.makeText(getActivity(), "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                    }

                    stamp.setLocation(geoPoint);

                    parseStamps.add(stamp);
                    user.put("stamps", parseStamps);

                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "There was a problem checking out, please try again.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            checkState = CHECK_STATE_OUT;
                        }
                    });
                }
            });*/
        }

        // begin animations
        final Animation a = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(1000);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (checkState == ParseStamp.FLAG_CHECK_OUT) {
                    Log.i("GameFragment", "Checked out!");
                    pushFeedEntry(stamp);

                    checkOutButton.clearAnimation();
                    checkOutButton.setEnabled(true);

                    checkOutButton.setVisibility(View.GONE);
                    checkInButton.setVisibility(View.VISIBLE);
                }
            }
        });

        checkOutButton.startAnimation(a);
    }

    private void checkIn() {
        checkInButton.setEnabled(false);

        final ParseUser user = ParseUser.getCurrentUser();

        final List<ParseStamp> parseStamps = user.getList("stamps");

        final ParseStamp stamp = new ParseStamp();
        stamp.setLogDate(new Date());
        stamp.setFlag(ParseStamp.FLAG_CHECK_IN);
        stamp.setComment("Checked in!");

        ParseGeoPoint quickLocation = LocationHandler.getQuickLocation(getActivity().getApplicationContext());
        if(quickLocation != null) {
            stamp.setLocation(quickLocation);

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    checkState = ParseStamp.FLAG_CHECK_IN;
                }
            });
        } else {

            parseStamps.add(stamp);
            user.put("stamps", parseStamps);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    checkState = ParseStamp.FLAG_CHECK_IN;
                }
            });
            /*
            LocationHandler.requestLocation(getActivity().getApplicationContext(), new LocationHandler.OnLocationReceivedListener() {
                @Override
                public void done(ParseGeoPoint geoPoint, Exception e) {
                    if (e == null && geoPoint != null) {
                        stamp.setLocation(geoPoint);

                        List<ParseStamp> parseStamps = user.getList("stamps");

                        parseStamps.add(stamp);
                        user.put("stamps", parseStamps);

                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(getActivity(), "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                checkState = CHECK_STATE_IN;
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "There was a problem checking in, please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });*/
        }

        final Animation a = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(1000);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (checkState == ParseStamp.FLAG_CHECK_IN) {
                    if (!easyTimer.isRunning()) {
                        easyTimer.startTimer();
                    }

                    Log.i("GameFragment", "Checked in!");

                    // TODO: The start time of the timer may not be the same as the log date of the flag
                    pushFeedEntry(stamp);

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    timerColonText.startAnimation(anim);

                    checkInButton.clearAnimation();
                    checkInButton.setVisibility(View.GONE);
                    checkInButton.setEnabled(true);

                    checkOutButton.setVisibility(View.VISIBLE);
                }
            }
        });

        checkInButton.startAnimation(a);
    }

    private interface StampListener {
        void done(Exception error);
    }

    public interface OnGameInteractionListener {
    }
}
