package com.trinew.easytime.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseEmployer;

/**
 * Created by Jonathan on 8/1/2015.
 */
public class EmployerTokenVerificationActivity extends Activity {

    // views
    private Button nextButton;
    private EditText employerTokenEditText;

    // data
    private String employerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_token_verification);

        // init views
        nextButton = (Button) findViewById(R.id.nextButton);

        employerTokenEditText = (EditText) findViewById(R.id.employerEditText);

        // fill views
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNext();
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

    private void verifyEmployer(final EmployerVerificationListener employerVerificationListener) {
        employerToken = "YcNp90HAhx";

        if(employerToken.isEmpty()) {
            employerVerificationListener.done(new Exception("You need to enter an employer token!"));
            return;
        }

        ParseQuery<ParseEmployer> query = ParseQuery.getQuery(ParseEmployer.class);
        query.getInBackground(employerToken, new GetCallback<ParseEmployer>() {
            public void done(ParseEmployer employer, ParseException e) {
                if (e != null || employer == null) {
                    employerVerificationListener.done(new Exception("We couldn't link you to your employer, please try again."));
                    return;
                }

                employerVerificationListener.done(null);
            }
        });
    }

    private void onNext() {
        verifyEmployer(new EmployerVerificationListener() {
            @Override
            public void done(Exception err) {
                if (err != null) {
                    Toast.makeText(EmployerTokenVerificationActivity.this, err.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                final Intent intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                intent.putExtra("employerId", employerToken);
                startActivity(intent);

                finish();
            }
        });
    }

    private interface EmployerVerificationListener {
        void done(Exception err);
    }
}
