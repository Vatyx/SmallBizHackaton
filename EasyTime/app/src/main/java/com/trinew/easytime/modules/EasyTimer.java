package com.trinew.easytime.modules;

import android.util.Log;

/**
 * Created by Jonathan on 7/27/2015.
 */
public class EasyTimer extends BaseTimer {

    private ShowerTimerListener showerTimerListener;

    private long endTime;

    // cached time used to bump end time to compensate for pauses
    private long pauseTime;

    public void setShowerTimerListener(ShowerTimerListener listener) {
        showerTimerListener = listener;
    }

    // event overrides

    @Override
    public void onUpdate(long currTime) {
        if(showerTimerListener != null) {
            // update with remaining time converted to seconds
            Log.i("MainActivity", currTime + " / " + endTime);
            showerTimerListener.onUpdate((int)((endTime - currTime) / 1000));

            if(currTime >= endTime) {
                showerTimerListener.onFinished();
                stopTimer();
            }
        }
    }

    // interaction overrides

    @Override
    public void pauseTimer() {
        super.pauseTimer();

        pauseTime = System.currentTimeMillis();
    }

    @Override
    public void resumeTimer() {
        super.resumeTimer();

        long pauseTimeDiff = System.currentTimeMillis() - pauseTime;
        endTime += pauseTimeDiff;
    }

    // interaction

    public void setEndTime(long newTime) {
        endTime = newTime;
    }

    public void setRemainingTime(int remainingTimeSeconds) {
        long remainingTime = remainingTimeSeconds * 1000;
        setEndTime(remainingTime);
    }

    public interface ShowerTimerListener {
        void onUpdate(int remainingTime);
        void onFinished();
    }
}
