package com.trinew.easytime.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.trinew.easytime.R;

import java.lang.ref.WeakReference;

public class SplashActivity extends Activity {

    private final static int SPLASH_MSG_DECAY = 1;

    private final static long SPLASH_DECAY_SPAN = 0;

    private ImageView splashImage;

    private SplashHandler splashHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImage = (ImageView) findViewById(R.id.splashImage);

        splashHandler = new SplashHandler(this);
        Message msg = new Message();
        msg.what = SPLASH_MSG_DECAY;
        splashHandler.sendMessageDelayed(msg, SPLASH_DECAY_SPAN);
    }

    private void onEndSplash() {
        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

        finish();
    }

    private static class SplashHandler extends Handler {

        private final WeakReference<SplashActivity> mTarget;

        public SplashHandler(SplashActivity context)
        {
            mTarget = new WeakReference<SplashActivity>((SplashActivity) context);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SplashActivity target = mTarget.get();
            if (target != null) {
                target.onEndSplash();
            }
        }
    };
}
