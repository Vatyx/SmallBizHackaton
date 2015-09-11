package com.trinew.easytime.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;

/**
 * Created by Jonathan on 8/1/2015.
 */
public class CreateProfileActivity extends Activity {

    // views
    private Button submitButton;

    private EditText legalNameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    // data
    private String legalName;
    private String username;
    private String password;
    private String confirmPassword;

    // the employer id from employer verification
    private String employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Intent intent = getIntent();
        if(intent != null) {
            employerId = intent.getStringExtra("employerId");
        }

        // init views
        submitButton = (Button) findViewById(R.id.submitButton);

        legalNameEditText = (EditText) findViewById(R.id.legalNameEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.passwordConfirmEditText);

        // fill views
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void generateProfile(final ProfileGenerationListener profileGenerationListener) {

        legalName = legalNameEditText.getText().toString();
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();

        // required fields
        if(legalName.isEmpty()) {
            profileGenerationListener.done(new Exception("We need to know your name!"));
            return;
        }

        if(username.isEmpty()) {
            profileGenerationListener.done(new Exception("You need to enter a username!"));
            return;
        }

        if(password.isEmpty()) {
            profileGenerationListener.done(new Exception("You need to enter a password!"));
            return;
        }

        if(!password.equals(confirmPassword)) {
            profileGenerationListener.done(new Exception("Your passwords aren't matching!"));
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("name", legalName);
        //user.put("employerId", employerId);
        user.put("privilege", 0);
        user.put("stamps", new ArrayList<ParseStamp>());

        user.signUpInBackground(new SignUpCallback() {
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    profileGenerationListener.done(null);
                } else {
                    Toast.makeText(CreateProfileActivity.this, "There was a problem creating your account, please try again.", Toast.LENGTH_LONG).show();
                    Log.e("CreateProfileActivity", "Problem signing up: " + e.toString());
                    return;
                }
            }
        });
    }

    private void onSubmit() {
        generateProfile(new ProfileGenerationListener() {
            @Override
            public void done(Exception err) {
                if(err != null) {
                    Toast.makeText(CreateProfileActivity.this, err.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    private interface ProfileGenerationListener {
        void done(Exception err);
    }
}
