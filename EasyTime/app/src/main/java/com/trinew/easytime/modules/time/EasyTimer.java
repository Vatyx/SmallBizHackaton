package com.trinew.easytime.modules.time;

/**
 * Created by Jonathan on 7/27/2015.
 */
public class EasyTimer extends BaseTimer {

    private EasyTimerListener easyTimerListener;

    // cached time used to bump end time to compensate for pauses
    private long pauseTime;

    public void setEasyTimerListener(EasyTimerListener listener) {
        easyTimerListener = listener;
    }

    // event overrides

    @Override
    public void onUpdate(long elapsedTimeMillis) {
        if(easyTimerListener != null) {
            // update with remaining time converted to seconds
            easyTimerListener.onUpdate(elapsedTimeMillis);
        }
    }

    // interaction overrides

    @Override
    public void pauseTimer() {
        super.pauseTimer();
    }

    @Override
    public void stopTimer() {
        super.stopTimer();
    }

    @Override
    public void resumeTimer() {
        super.resumeTimer();
    }

    // interaction

    public interface EasyTimerListener {
        void onUpdate(long elapsedTimeMillis);
    }
}