package com.trinew.easytime;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.trinew.easytime.models.ParseEmployer;
import com.trinew.easytime.models.ParseStamp;

/**
 * Created by Jonathan on 8/1/2015.
 */
public class MainApplication extends Application {

    private final static String parseAppId = "6UQPoyjqD9UoLsUsJMkgdZjEPiFkTWHMzkwL0o4n";
    private final static String parseClientKey = "Jm49erGyE229JxFgjW2ZGa8ETynkpl5hbKwORTqO";

    @Override
    public void onCreate() {
        super.onCreate();


        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);

        // Parse
        ParseObject.registerSubclass(ParseStamp.class);
        ParseObject.registerSubclass(ParseEmployer.class);
        Parse.initialize(this, parseAppId, parseClientKey);
    }
}