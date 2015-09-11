package com.trinew.easytime.fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.trinew.easytime.R;
import com.trinew.easytime.adapters.StampCollectionAdapter;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.ProfileBuilder;
import com.trinew.easytime.modules.stamps.StampCollection;
import com.trinew.easytime.modules.stamps.StampCollectionBox;
import com.trinew.easytime.views.AnimatedExpandableListView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class EasyBoxFragment extends Fragment {

    private RelativeLayout progressContainer;
    private RelativeLayout errorContainer;
    private TextView genericErrorText;
    private TextView noStampsErrorText;

    private AnimatedExpandableListView listView;

    private StampCollectionAdapter stampCollectionAdapter;

    // listener
    private OnBoxInteractionListener onBoxInteractionListener;

    public static EasyBoxFragment newInstance() {
        EasyBoxFragment fragment = new EasyBoxFragment();
        return fragment;
    }

    public EasyBoxFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stamp_box, container, false);

        // init views
        progressContainer = (RelativeLayout) view.findViewById(R.id.progressContainer);
        errorContainer = (RelativeLayout) view.findViewById(R.id.errorContainer);
        genericErrorText = (TextView) view.findViewById(R.id.genericErrorText);
        noStampsErrorText = (TextView) view.findViewById(R.id.noStampsErrorText);

        listView = (AnimatedExpandableListView) view.findViewById(R.id.stampBox);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                final ParseStamp stamp = (ParseStamp) stampCollectionAdapter.getChild(groupPosition, childPosition);
                final Calendar logCalendar = Calendar.getInstance();
                Date currLogDate = stamp.getLogDate();
                final String currCommentStr = stamp.getComment();

                logCalendar.setTime(currLogDate);

                AlertDialog dialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.dialog_title_edit)
                        .setMessage(Html.fromHtml("This tutorial on Dialog Boxes was written for &lt;a href=\"http://osamashabrez.com\"&gt;OsamaShabrez.com&lt;/a&gt;"))
                        .setNegativeButton(R.string.dialog_action_label_dismiss, null)
                        .setPositiveButton(R.string.dialog_action_label_submit, null);

                dialog = builder.create();

                //dialog.show();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        logCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        logCalendar.set(Calendar.MINUTE, minute);

                        ParseUser currentUser = ParseUser.getCurrentUser();
                        List<ParseStamp> stamps = currentUser.getList(ProfileBuilder.PROFILE_KEY_STAMPS);
                        for(int i = 0; i < stamps.size(); i++) {
                            if(stamp.getLogDate().equals(stamps.get(i).getLogDate())) {
                                stamps.get(i).setLogDate(logCalendar.getTime());
                                break;
                            }
                        }

                        Collections.sort(stamps, new Comparator<ParseStamp>() {
                            @Override
                            public int compare(ParseStamp t0, ParseStamp t1) {
                                return (int)(t0.getLogDate().getTime() - t1.getLogDate().getTime());
                            }
                        });

                        currentUser.put(ProfileBuilder.PROFILE_KEY_STAMPS, stamps);

                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(getActivity(), "There was a problem editing!", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                chartUserData();
                            }
                        });
                    }
                }, logCalendar.get(Calendar.HOUR_OF_DAY), logCalendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();

                return true;
            }
        });

        stampCollectionAdapter = new StampCollectionAdapter(getActivity());
        listView.setAdapter(stampCollectionAdapter);

        // fill list up
        chartUserData();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onBoxInteractionListener = (OnBoxInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBoxInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void chartUserData() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseStamp> stamps = currentUser.getList(ProfileBuilder.PROFILE_KEY_STAMPS);

        // check for valid data
        if(stamps == null) {
            errorContainer.setVisibility(View.VISIBLE);
            genericErrorText.setVisibility(View.VISIBLE);
            return;
        }

        // check if there are any stamps
        if(stamps.size() == 0) {
            errorContainer.setVisibility(View.VISIBLE);
            noStampsErrorText.setVisibility(View.VISIBLE);

            return;
        }

        progressContainer.setVisibility(View.VISIBLE);

        // fetch the stamps and fill our charts
        ParseStamp.fetchAllIfNeededInBackground(stamps, new FindCallback<ParseStamp>() {
            @Override
            public void done(List<ParseStamp> stampList, ParseException e) {
                progressContainer.setVisibility(View.GONE);

                if (e != null || stampList == null) {
                    errorContainer.setVisibility(View.VISIBLE);
                    genericErrorText.setVisibility(View.VISIBLE);

                    return;
                }

                StampCollectionBox stampCollectionBox = new StampCollectionBox(stampList);
                List<StampCollection> stampCollections = stampCollectionBox.getStampCollections();

                stampCollectionAdapter.clearCollections();

                stampCollectionAdapter.setCollections(stampCollections);

                Calendar currCalendar = Calendar.getInstance();
                Calendar todayCalendar = Calendar.getInstance();

                for(int i = 0; i < stampCollections.size(); i++) {
                    currCalendar.setTime(stampCollections.get(i).getCollectionDate());

                    if(currCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
                        listView.expandGroupWithAnimation(i);
                        break;
                    }
                }
            }
        });
    }

    public interface OnBoxInteractionListener {
    }
}
