package com.trinew.easytime.modules.time;

import android.os.Handler;

/**
 * Created by Jonathan on 7/27/2015.
 */
public abstract class BaseTimer {

    // internal timer data
    protected long startTime = 0;

    private boolean mIsRunning = false;
    private boolean mIsPaused = false;

    // handler for timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;

            onUpdate(millis);
            timerHandler.postDelayed(this, 1000);
        }
    };

    // events

    public abstract void onUpdate(long elapsedTimeMillis);

    // timer getters

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    // timer interaction

    public void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        mIsPaused = false;
        mIsRunning = true;
    }

    public void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);

        resetTimer();

        mIsPaused = false;
        mIsRunning = false;
    }

    public void pauseTimer() {
        timerHandler.removeCallbacks(timerRunnable);

        mIsPaused = true;
    }

    public void resumeTimer() {
        timerHandler.postDelayed(timerRunnable, 0);

        mIsPaused = false;
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
    }
}